package com.example.dudu.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dudu.*
import com.example.dudu.databinding.MainActivityBinding
import com.example.dudu.models.Task
import com.example.dudu.ui.task.CreateTaskActivity
import com.example.dudu.ui.tasks.TaskClickListener
import com.example.dudu.ui.tasks.TasksAdapter
import com.example.dudu.util.Constants
import com.example.dudu.util.SwipeHelper
import java.util.*

class MainActivity : AppCompatActivity(), TaskClickListener {

    private lateinit var binding: MainActivityBinding
    private val tasksAdapter = TasksAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        with(binding) {
            fabCreateTask.setOnClickListener {
                CreateTaskActivity.startActivityForResult(this@MainActivity, Constants.REQUEST_CREATE_TASK)
            }
            headerLayout.ibVisibility.apply {
                setOnClickListener {
                    isSelected = !isSelected
                    if (isSelected) {
                        tasksAdapter.showDoneTasks()
                    } else {
                        tasksAdapter.hideDoneTasks()
                    }
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
        tasksAdapter.setNewTasks(getMockTasks())
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
                        task?.let{ tasksAdapter.removeTask(it) }
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

    private fun getMockTasks(): MutableList<Task> {
        val tasks = mutableListOf<Task>()
        val c = Calendar.getInstance()
        c.set(2020, 1, 1)
        for (i in 1..10) {
            val task = when {
                i % 2 == 0 -> {
                    Task(i.toString(), "$i" + getString(R.string.task_short), Calendar.getInstance().time, 1, true)
                }
                i % 3 == 0 -> {
                    Task(i.toString(), "$i" + getString(R.string.task_normal), c.time, 0, false)
                }
                else -> {
                    Task(i.toString(), "$i" + getString(R.string.task_long), Calendar.getInstance().time, 2, false)
                }
            }
            tasks.add(task)
        }
        return tasks
    }

    private fun updateHeader() {
        binding.headerLayout.tvCompleted.text = getString(
            R.string.main_completed_label,
            tasksAdapter.getDoneTasksSize()
        )
    }

    override fun onTaskCheckedClick(position: Int) {
        tasksAdapter.updateTaskStatus(position)
        updateHeader()
    }

    override fun onTaskClick(task: Task) {
        CreateTaskActivity.startActivityForResult(this, task, Constants.REQUEST_EDIT_TASK)
    }
}