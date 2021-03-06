package com.example.dudu.ui.tasks

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
import java.util.*
import javax.inject.Inject

class TasksActivity : AppCompatActivity(), TaskClickListener {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private lateinit var binding: MainActivityBinding
    private lateinit var loadingDialog: MaterialDialog
    private val tasksAdapter = TaskAdapter(this)
    private val tasksViewModel: TasksViewModel by viewModels {
        viewModelProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (applicationContext as DuduApp).appComponent
            .tasksComponent()
            .inject(this)

        initUI()
        subscribeObservers()
    }

    private fun initUI() {
        with(binding) {
            fabCreateTask.setOnClickListener {
                CreateTaskActivity.startActivityForResult(
                    this@TasksActivity,
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
            layoutManager = LinearLayoutManager(this@TasksActivity)
            adapter = tasksAdapter
            setHasFixedSize(true)
        }
        val swipeHelper = object : SwipeHelper() {
            override fun createLeftButton(): ControlButton {
                return ControlButton(
                    ContextCompat.getColor(this@TasksActivity, R.color.green),
                    ContextCompat.getDrawable(this@TasksActivity, R.drawable.ic_check)
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
                    ContextCompat.getColor(this@TasksActivity, R.color.red),
                    ContextCompat.getDrawable(this@TasksActivity, R.drawable.ic_remove)
                ) { position ->
                    UIUtil.createDialogWithAction(
                        this@TasksActivity,
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
                        ContextCompat.getColor(this@TasksActivity, R.color.label_tertiary)
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
                    UIUtil.showSnackbar(binding.coordinatorContainer, resource.message)
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

        tasksViewModel.updateTask.observe(this, { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Loaded -> {}
                is Resource.Error ->
                    UIUtil.showSnackbar(binding.coordinatorContainer, resource.message)
            }
        })

        tasksViewModel.removeTask.observe(this, { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Loaded -> {
                    loadingDialog.dismiss()
                    UIUtil.showSnackbar(
                        binding.coordinatorContainer,
                        getString(R.string.main_task_removed_message)
                    )
                }
                is Resource.Error -> {
                    loadingDialog.dismiss()
                    UIUtil.showSnackbar(binding.coordinatorContainer, resource.message)
                }
            }
        })

        tasksViewModel.syncTasks.observe(this, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvConnection.apply {
                        setBackgroundColor(
                            ContextCompat.getColor(this@TasksActivity, R.color.blue)
                        )
                        text = getString(R.string.main_synchronization)
                        visibility = View.VISIBLE
                    }
                    showLoading()
                }
                else -> {
                    binding.tvConnection.visibility = View.GONE
                    showData()
                    UIUtil.showSnackbar(
                        binding.coordinatorContainer,
                        if (resource is Resource.Error)
                            resource.message
                        else
                            getString(R.string.main_data_synchronized)
                    )
                }
            }
        })
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