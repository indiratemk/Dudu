package com.example.dudu.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dudu.*
import com.example.dudu.databinding.MainActivityBinding
import com.example.dudu.models.Task
import com.example.dudu.ui.task.CreateTaskActivity
import com.example.dudu.ui.tasks.TaskClickListener
import com.example.dudu.ui.tasks.TasksAdapter
import com.example.dudu.util.Constants
import com.example.dudu.util.DateFormatter
import com.example.dudu.util.SwipeHelper
import com.example.dudu.util.TasksReminderWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), TaskClickListener {

    private lateinit var binding: MainActivityBinding
    private val tasksAdapter = TasksAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleReminderWork()
        initUI()
    }

    private fun initUI() {
        with(binding) {
            fabCreateTask.setOnClickListener {
                CreateTaskActivity.startActivityForResult(
                    this@MainActivity,
                    Constants.REQUEST_CREATE_TASK
                )
            }
            headerLayout.ibVisibility.apply {
                setOnClickListener {
                    isSelected = !isSelected
                    if (isSelected) {
                        tasksAdapter.showDoneTasks()
                        rvTasks.smoothScrollToPosition(0)
                    } else {
                        tasksAdapter.hideDoneTasks()
                    }
                }
            }
            headerLayout.ibChangeMode.apply {
                val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                isSelected = when (currentMode) {
                    Configuration.UI_MODE_NIGHT_YES -> true
                    else -> false
                }
                setOnClickListener {
                    AppCompatDelegate.setDefaultNightMode(
                        if (!isSelected)
                            AppCompatDelegate.MODE_NIGHT_YES
                        else
                            AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
            headerLayout.ctToolbar.setOnClickListener {
                binding.rvTasks.smoothScrollToPosition(0)
            }
        }
        initRV()
        updateHeader()
    }

    private fun initRV() {
        tasksAdapter.setTasks(getPreparedData())
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tasksAdapter
            setHasFixedSize(true)
        }
        val swipeHelper = object : SwipeHelper() {
            override fun createLeftButton(): ControlButton {
                return ControlButton(
                    ContextCompat.getColor(this@MainActivity, R.color.green),
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_check)
                ) {
                    tasksAdapter.updateTaskStatus(it)
                    updateHeader()
                }
            }

            override fun createRightButton(): ControlButton {
                return ControlButton(
                    ContextCompat.getColor(this@MainActivity, R.color.red),
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_remove)
                ) {
                    tasksAdapter.removeTask(it)
                    updateHeader()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeHelper)
        touchHelper.attachToRecyclerView(binding.rvTasks)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_EDIT_TASK -> {
                val task = data?.getParcelableExtra<Task>(Constants.EXTRA_TASK)
                when (resultCode) {
                    Constants.RESULT_TASK_REMOVED -> {
                        task?.let { tasksAdapter.removeTask(it) }
                    }
                    Constants.RESULT_TASK_EDITED -> {
                        task?.let { tasksAdapter.updateTask(it) }
                    }
                }
            }
            Constants.REQUEST_CREATE_TASK -> {
                val task = data?.getParcelableExtra<Task>(Constants.EXTRA_TASK)
                if (resultCode == Constants.RESULT_TASK_CREATED) {
                    task?.let { tasksAdapter.addTask(it) }
                }
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val isSelected = savedInstanceState.getBoolean(Constants.EXTRA_SHOW_DONE_TASKS)
        binding.headerLayout.ibVisibility.isSelected = isSelected
        if (isSelected) {
            tasksAdapter.showDoneTasks()
        } else {
            tasksAdapter.hideDoneTasks()
        }
        val doneTasks = savedInstanceState.getParcelableArrayList<Task>(Constants.EXTRA_DONE_TASKS)
        doneTasks?.let {
            tasksAdapter.doneTasks.clear()
            tasksAdapter.doneTasks.addAll(it)
        }
        val undoneTasks = savedInstanceState.getParcelableArrayList<Task>(Constants.EXTRA_UNDONE_TASKS)
        undoneTasks?.let {
            tasksAdapter.undoneTasks.clear()
            tasksAdapter.undoneTasks.addAll(it)
        }
        tasksAdapter.notifyDataSetChanged()
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val doneTasks = arrayListOf<Task>()
        doneTasks.addAll(tasksAdapter.doneTasks)
        val undoneTasks = arrayListOf<Task>()
        undoneTasks.addAll(tasksAdapter.undoneTasks)

        outState.putParcelableArrayList(Constants.EXTRA_DONE_TASKS, doneTasks)
        outState.putParcelableArrayList(Constants.EXTRA_UNDONE_TASKS, undoneTasks)
        outState.putBoolean(Constants.EXTRA_SHOW_DONE_TASKS, binding.headerLayout.ibVisibility.isSelected)
        super.onSaveInstanceState(outState)
    }

    private fun scheduleReminderWork() {
        val currentDate = Calendar.getInstance()
        val notificationDate = Calendar.getInstance()
        notificationDate.set(Calendar.HOUR_OF_DAY, 6)
        notificationDate.set(Calendar.MINUTE, 0)
        notificationDate.set(Calendar.SECOND, 0)
        if (notificationDate.before(currentDate)) {
            notificationDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = notificationDate.timeInMillis.minus(currentDate.timeInMillis)
        val dailyWorkRequest = OneTimeWorkRequestBuilder<TasksReminderWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(this)
            .enqueue(dailyWorkRequest)
    }

    private fun getPreparedData(): MutableList<Task> {
        val tasks = getMockTasks()
        tasks.addAll(getCachedTasks())
        tasks.sortWith { o1, o2 ->
            if (o1.deadline == null && o2.deadline == null) {
                return@sortWith when {
                    o1.priority > o2.priority -> -1
                    o1.priority == o2.priority -> 0
                    else -> 1
                }
            } else if (o1.deadline != null && o2.deadline == null) {
                return@sortWith -1
            } else if (o1.deadline == null && o2.deadline != null) {
                return@sortWith 1
            } else {
                return@sortWith when {
                    o1.deadline!!.before(o2.deadline) -> -1
                    o1.deadline.after(o2.deadline) -> 1
                    else -> {
                        when {
                            o1.priority > o2.priority -> -1
                            o1.priority == o2.priority -> 0
                            else -> 1
                        }
                    }
                }
            }
        }
        return tasks
    }

    private fun getMockTasks(): MutableList<Task> {
        val tasks = mutableListOf<Task>()
        val currentDate = DateFormatter.getCurrentDateWithoutTime()
        for (i in 1..10) {
            val task = when {
                i % 2 == 0 -> {
                    Task(i.toString(), "$i" + getString(R.string.task_short), currentDate, 1, true)
                }
                i % 3 == 0 -> {
                    Task(i.toString(), "$i" + getString(R.string.task_normal), currentDate, 0, false)
                }
                else -> {
                    Task(i.toString(), "$i" + getString(R.string.task_long), currentDate, 2, false)
                }
            }
            tasks.add(task)
        }
        return tasks
    }

    private fun getCachedTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        try {
            val inputStream =
                FileInputStream(File("$cacheDir/${Constants.FILE_NAME}${Constants.FILE_EXTENSION}"))
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }

            val json = String(buffer, Charsets.UTF_8)
            val type = object : TypeToken<List<Task>>() {}.type
            tasks.addAll(Gson().fromJson<List<Task>>(json, type))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasks
    }

    private fun updateHeader() {
        binding.headerLayout.tvCompleted.text = getString(
            R.string.main_completed_label,
            tasksAdapter.getDoneTasksSize()
        )
    }

    override fun onTaskCheckedClick(task: Task) {
        tasksAdapter.updateTaskStatus(task)
        updateHeader()
    }

    override fun onTaskClick(task: Task) {
        CreateTaskActivity.startActivityForResult(this, task, Constants.REQUEST_EDIT_TASK)
    }
}