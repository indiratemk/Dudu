package com.example.dudu.ui.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.dudu.DuduApp
import com.example.dudu.R
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Priority
import com.example.dudu.data.models.Task
import com.example.dudu.databinding.CreateTaskActivityBinding
import com.example.dudu.util.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CreateTaskActivity : AppCompatActivity()  {

    @Inject
    lateinit var viewModelProvider: ViewModelProviderFactory

    private lateinit var binding: CreateTaskActivityBinding
    private lateinit var createTaskViewModel: CreateTaskViewModel
    private lateinit var prioritiesAdapter: PrioritiesAdapter
    private lateinit var task: Task
    private lateinit var loadingDialog: MaterialDialog
    private var priority = Priority.NONE.value
    private var isTaskCreation = true
    private var timestamp = 0L
    private val datePicker = DatePickerFragment(onDateSelected = {
        timestamp = TimeUnit.MILLISECONDS.toSeconds(it.time)
        with(binding) {
            deadlineLayout.switchDeadline.isChecked = true
            deadlineLayout.tvDate.text = DateFormatter.formatDate(timestamp, DateFormatter.DF2)
            deadlineLayout.tvDate.visibility = View.VISIBLE
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateTaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (applicationContext as DuduApp).appComponent
            .createTaskComponent()
            .inject(this)

        createTaskViewModel =
            ViewModelProvider(this, viewModelProvider)[CreateTaskViewModel::class.java]

        val task = intent.getParcelableExtra<Task>(Constants.EXTRA_TASK)
        isTaskCreation = task == null
        task?.let {
            this.task = it
            setTaskData()
        }
        initUI()
        subscribeObservers()
    }

    private fun initUI() {
        with(binding) {
            toolbar.setNavigationIcon(R.drawable.ic_close)
            toolbar.setNavigationOnClickListener {
                setResult(RESULT_CANCELED)
                finish()
            }
            toolbar.inflateMenu(R.menu.create_task_menu)
            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.actionSave) {
                    if (etDescription.text.trim().isEmpty()) {
                        tvDescriptionError.visibility = View.VISIBLE
                    } else {
                        val description = etDescription.text.trim().toString()
                        saveTask(description, priority, timestamp)
                    }
                }
                true
            }
            btnRemove.visibility = if (isTaskCreation) View.GONE else View.VISIBLE
            etDescription.doAfterTextChanged {
                tvDescriptionError.visibility = View.GONE
            }
            btnRemove.setOnClickListener {
                UIUtil.createDialogWithAction(
                    this@CreateTaskActivity,
                    R.string.task_removing_message,
                    onCancel = {},
                    onPositive = {
                        setResult(Constants.RESULT_TASK_REMOVED)
                        createTaskViewModel.removeTask(task)
                    },
                    onNegative = {}
                ).show()
            }
            deadlineLayout.clDeadline.setOnClickListener {
                datePicker.show(supportFragmentManager, "datePicker")
            }
            deadlineLayout.switchDeadline.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (timestamp == 0L) {
                        timestamp = DateFormatter.getCurrentDateInSeconds()
                        deadlineLayout.tvDate.text =
                            DateFormatter.formatDate(timestamp, DateFormatter.DF2)
                        deadlineLayout.tvDate.visibility = View.VISIBLE
                    }
                } else {
                    timestamp = 0L
                    deadlineLayout.tvDate.visibility = View.GONE
                }
            }
        }
        loadingDialog = MaterialDialog(this)
            .customView(R.layout.layout_loading)
            .cancelable(false)
        initPriorities()
    }

    private fun subscribeObservers() {
        createTaskViewModel.task.observe(this, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    loadingDialog.show()
                }
                is Resource.Loaded -> {
                    loadingDialog.dismiss()
                    finish()
                }
                is Resource.Error -> {
                    loadingDialog.dismiss()
                    setResult(RESULT_CANCELED)
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })
    }

    private fun setTaskData() {
        priority = task.priority
        with(binding) {
            etDescription.setText(task.description)
            if (task.deadline != 0L) {
                timestamp = task.deadline
                deadlineLayout.tvDate.text = DateFormatter.formatDate(timestamp, DateFormatter.DF2)
                deadlineLayout.tvDate.visibility = View.VISIBLE
                deadlineLayout.switchDeadline.isChecked = true
            }
        }
    }

    private fun initPriorities() {
        prioritiesAdapter = PrioritiesAdapter(
            this,
            R.layout.chose_priority_item,
            R.layout.priority_item
        )
        binding.spinnerPriority.apply {
            adapter = prioritiesAdapter
            val priorities = mutableListOf<String>()
            Priority.values().forEach { priorities.add(it.value) }
            setSelection(priorities.indexOf(priority), false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    priority = (adapter.getItem(position) as Priority).value
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun saveTask(
        description: String,
        priority: String,
        deadline: Long
    ) {
        if (isTaskCreation) {
            val task = Task(
                UUID.randomUUID().toString(),
                description,
                deadline,
                priority,
                false
            )
            createTaskViewModel.createTask(task)
            setResult(Constants.RESULT_TASK_CREATED)
        } else {
            val task = this.task.copy(
                description = description,
                priority = priority,
                deadline = deadline
            )
            createTaskViewModel.updateTask(task)
            setResult(Constants.RESULT_TASK_UPDATED)
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {
        fun startActivityForResult(activity: Activity, task: Task, requestCode: Int) {
            val intent = Intent(activity, CreateTaskActivity::class.java)
            intent.putExtra(Constants.EXTRA_TASK, task)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startActivityForResult(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, CreateTaskActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}