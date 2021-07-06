package com.example.dudu.ui.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.dudu.DuduApp
import com.example.dudu.R
import com.example.dudu.data.Priority
import com.example.dudu.data.local.TaskEntity
import com.example.dudu.databinding.CreateTaskActivityBinding
import com.example.dudu.util.Constants
import com.example.dudu.util.DateFormatter
import com.example.dudu.util.DatePickerFragment
import com.example.dudu.util.ViewModelFactory
import java.util.*
import javax.inject.Inject


class CreateTaskActivity : AppCompatActivity()  {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: CreateTaskActivityBinding
    private lateinit var createTaskViewModel: CreateTaskViewModel
    private lateinit var prioritiesAdapter: PrioritiesAdapter
    private lateinit var task: TaskEntity
    private var priority = Priority.NONE.value
    private var isTaskCreation = true
    private var timestamp = 0L
    private val datePicker = DatePickerFragment(onDateSelected = {
        timestamp = it.time
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

        DuduApp.appComponent.inject(this)
        createTaskViewModel =
            ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]

        val task = intent.getParcelableExtra<TaskEntity>(Constants.EXTRA_TASK)
        isTaskCreation = task == null
        task?.let {
            this.task = it
            setTaskData()
        }
        initUI()
    }

    private fun initUI() {
        with(binding) {
            toolbar.setNavigationIcon(R.drawable.ic_close)
            toolbar.setNavigationOnClickListener { finish() }
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
                val data = Intent()
                data.putExtra(Constants.EXTRA_TASK, task)
                setResult(Constants.RESULT_TASK_REMOVED, data)
                finish()
            }
            deadlineLayout.clDeadline.setOnClickListener {
                datePicker.show(supportFragmentManager, "datePicker")
            }
            deadlineLayout.switchDeadline.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (timestamp == 0L) {
                        timestamp = DateFormatter.getCurrentDateWithoutTime().time
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
        initPriorities()
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
            setSelection(priority, false)
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
        priority: Int,
        deadline: Long
    ) {
        if (isTaskCreation) {
            val task = TaskEntity(
                UUID.randomUUID().toString(),
                description,
                deadline,
                priority,
                false
            )
            createTaskViewModel.createTask(task)
        } else {
            val task = this.task.copy(
                description = description,
                priority = priority,
                deadline = deadline
            )
            createTaskViewModel.updateTask(task)
        }
        finish()
    }

    companion object {
        fun startActivityForResult(activity: Activity, task: TaskEntity, requestCode: Int) {
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