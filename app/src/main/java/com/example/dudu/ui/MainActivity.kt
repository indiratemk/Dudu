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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.dudu.*
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import com.example.dudu.databinding.MainActivityBinding
import com.example.dudu.ui.task.CreateTaskActivity
import com.example.dudu.ui.tasks.*
import com.example.dudu.util.*
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), TaskClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: MainActivityBinding
    private lateinit var tasksViewModel: TasksViewModel
    private val tasksAdapter = TaskAdapter(this)
    private lateinit var loadingDialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DuduApp.appComponent.inject(this)
        tasksViewModel =
            ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]

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
        loadingDialog = MaterialDialog(this)
            .customView(R.layout.layout_loading)
            .cancelable(false)
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
                    UIUtil.createDialogWithAction(
                        this@MainActivity,
                        R.string.task_removing_message,
                        onCancel = { tasksAdapter.notifyItemChanged(position) },
                        onPositive = {
                            loadingDialog.show()
                            val task = tasksAdapter.tasks[position]
                            tasksViewModel.removeTask(task)
                        },
                        onNegative = { tasksAdapter.notifyItemChanged(position) }
                    ).show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeHelper)
        touchHelper.attachToRecyclerView(binding.rvTasks)
    }

    private fun subscribeObservers() {
        val networkConnection = NetworkConnection(this)

        networkConnection.observe(this, { isConnected ->
            if (isConnected) {
                tasksViewModel.synchronizeTasks()
                binding.tvConnection.visibility = View.GONE
            } else {
                binding.tvConnection.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, R.color.label_tertiary)
                    )
                    text = getString(R.string.main_offline_mode)
                    visibility = View.VISIBLE
                }
            }
        })

        tasksViewModel.tasksResource.observe(this, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Loaded -> {
                    tasksAdapter.updateTasks(resource.data)
                    showData()
                    tasksViewModel.setShouldFetchRemote(false)
                }
                is Resource.Error -> {
                    showData()
                    resource.message?.let {
                        UIUtil.showSnackbar(binding.coordinatorContainer, it)
                    }
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
                with(binding) {
                    when (event) {
                        is TaskEvent.SuccessRemoving -> {
                            loadingDialog.dismiss()
                            UIUtil.showSnackbar(
                                coordinatorContainer,
                                getString(R.string.main_task_removed_message)
                            )
                        }
                        is TaskEvent.FailRemoving -> {
                            loadingDialog.dismiss()
                            tasksAdapter.notifyItemChanged(tasksAdapter.tasks.indexOf(event.task))
                            UIUtil.showSnackbar(
                                coordinatorContainer,
                                event.message ?: getString(R.string.unknown_error_message)
                            )
                        }
                        is TaskEvent.SuccessUpdating -> {}
                        is TaskEvent.FailUpdating -> UIUtil.showSnackbar(
                            coordinatorContainer,
                            event.message ?: getString(R.string.unknown_error_message)
                        )
                        is TaskEvent.SynchronizationLoading -> {
                            tvConnection.apply {
                                setBackgroundColor(
                                    ContextCompat.getColor(this@MainActivity, R.color.blue)
                                )
                                text = getString(R.string.main_synchronization)
                                visibility = View.VISIBLE
                            }
                            showLoading()
                        }
                        is TaskEvent.SuccessSynchronization -> {
                            tvConnection.visibility = View.GONE
                            showData()
                            UIUtil.showSnackbar(
                                coordinatorContainer,
                                getString(R.string.main_data_synchronized)
                            )
                        }
                        is TaskEvent.FailSynchronization -> {
                            tvConnection.visibility = View.GONE
                            showData()
                            UIUtil.showSnackbar(
                                coordinatorContainer,
                                event.message ?: getString(R.string.main_synchronization_error)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            rvTasks.visibility = View.GONE
            tvEmpty.visibility = View.GONE
            shimmerLoading.visibility = View.VISIBLE
        }
    }

    private fun showData() {
        with(binding) {
            if (tasksAdapter.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
            }
            rvTasks.visibility = View.VISIBLE
            shimmerLoading.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_EDIT_TASK -> {
                when (resultCode) {
                    Constants.RESULT_TASK_REMOVED ->
                        UIUtil.showSnackbar(binding.coordinatorContainer,
                            getString(R.string.main_task_removed_message))
                    Constants.RESULT_TASK_UPDATED ->
                        UIUtil.showSnackbar(binding.coordinatorContainer,
                            getString(R.string.main_task_updated_message))
                }
            }
            Constants.REQUEST_CREATE_TASK -> {
                when (resultCode) {
                    Constants.RESULT_TASK_CREATED ->
                        UIUtil.showSnackbar(binding.coordinatorContainer,
                            getString(R.string.main_task_create_message))
                }
            }
        }
    }

    override fun onTaskClick(task: Task) {
        CreateTaskActivity.startActivityForResult(this, task, Constants.REQUEST_EDIT_TASK)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        tasksViewModel.onTaskCheckedChanged(task, isChecked)
    }
}