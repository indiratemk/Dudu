package com.example.dudu.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dudu.*
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import com.example.dudu.databinding.MainActivityBinding
import com.example.dudu.ui.task.CreateTaskActivity
import com.example.dudu.ui.tasks.*
import com.example.dudu.util.*
import kotlinx.coroutines.flow.collect
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), TaskClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: MainActivityBinding
    private lateinit var tasksViewModel: TasksViewModel
    private val tasksAdapter = TaskAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DuduApp.appComponent.inject(this)
        tasksViewModel =
            ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]

        scheduleReminderWork()
        initUI()
        subscribeObservers()
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
                    tasksViewModel.setShowDoneTasks(!isSelected)
                }
            }
            headerLayout.ibChangeMode.apply {
                val currentMode =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
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
    }

    private fun initRV() {
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
                ) { position ->
                    val task = tasksAdapter.tasks[position]
                    tasksViewModel.onTaskCheckedChanged(task, !task.isDone)
                    if (binding.headerLayout.ibVisibility.isSelected) {
                        tasksAdapter.notifyItemChanged(position)
                    }
                }
            }

            override fun createRightButton(): ControlButton {
                return ControlButton(
                    ContextCompat.getColor(this@MainActivity, R.color.red),
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_remove)
                ) { position ->
                    val task = tasksAdapter.tasks[position]
                    tasksViewModel.onTaskRemoved(task)
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeHelper)
        touchHelper.attachToRecyclerView(binding.rvTasks)
    }

    private fun subscribeObservers() {
        tasksViewModel.tasksResource.observe(this, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    with(binding) {
                        rvTasks.visibility = View.GONE
                        tvEmpty.visibility = View.GONE
                        pbLoading.visibility = View.VISIBLE
                    }
                }
                is Resource.Loaded -> {
                    tasksAdapter.updateTasks(resource.data)
                    with(binding) {
                        if (tasksAdapter.itemCount == 0) {
                            rvTasks.visibility = View.GONE
                            tvEmpty.visibility = View.VISIBLE
                            pbLoading.visibility = View.GONE
                        } else {
                            rvTasks.visibility = View.VISIBLE
                            tvEmpty.visibility = View.GONE
                            pbLoading.visibility = View.GONE
                        }
                    }
                }
                is Resource.Error -> {
                    // TODO: 7/8/21 убрать захардкоденный текст
                    UIUtil.showSnackbar(binding.coordinatorContainer, "Произошла ошибка")
                }
            }
        })

        tasksViewModel.doneTasksCount.observe(this, {
            binding.headerLayout.tvCompleted.text = getString(
                R.string.main_completed_label, it
            )
        })

        tasksViewModel.showDone.observe(this, {
            binding.headerLayout.ibVisibility.isSelected = it
        })

        lifecycleScope.launchWhenStarted {
            tasksViewModel.taskEvent.collect { event ->
                when (event) {
                    is TaskEvent.SuccessRemoving -> {
                        UIUtil.showSnackbar(
                            binding.coordinatorContainer,
                            getString(R.string.main_task_removed_message),
                            getString(R.string.main_task_removed_action)
                        ) {
                            tasksViewModel.onUndoTaskRemove(event.task)
                        }
                    }
                    is TaskEvent.SuccessCreating ->
                        UIUtil.showSnackbar(
                            binding.coordinatorContainer,
                            getString(R.string.main_task_restored_message)
                        )
                    is TaskEvent.SuccessUpdating -> {}
                    is TaskEvent.Error -> UIUtil.showSnackbar(
                        binding.coordinatorContainer,
                        event.message ?: getString(R.string.main_unknown_error_message)
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_EDIT_TASK -> {
                when (resultCode) {
                    Constants.RESULT_TASK_REMOVED -> {
                        data?.getParcelableExtra<Task>(Constants.EXTRA_TASK)?.let {
                            tasksViewModel.onTaskRemoved(it)
                        }
                    }
                }
            }
        }
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

    override fun onTaskClick(task: Task) {
        CreateTaskActivity.startActivityForResult(this, task, Constants.REQUEST_EDIT_TASK)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        tasksViewModel.onTaskCheckedChanged(task, isChecked)
    }
}