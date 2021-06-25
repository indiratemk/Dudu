package com.example.dudu.ui.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.dudu.R
import com.example.dudu.databinding.CreateTaskActivityBinding
import com.example.dudu.models.Priority
import com.example.dudu.models.Task
import com.example.dudu.util.Constants
import com.example.dudu.util.DateFormatter
import com.example.dudu.util.DatePickerFragment
import java.util.*

class CreateTaskActivity : AppCompatActivity()  {

    companion object {
        fun startActivityForResult(activity: Activity, task: Task, requestCode: Int) {
            val intent = Intent(activity, CreateTaskActivity::class.java)
            intent.putExtra(Constants.EXTRA_TASK, task)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startActivityForResult(activity:Activity, requestCode: Int) {
            val intent = Intent(activity, CreateTaskActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private lateinit var binding: CreateTaskActivityBinding
    private lateinit var prioritiesAdapter: PrioritiesAdapter
    private lateinit var task: Task
    private var priority = Priority.NONE.value
    private var isTaskCreation = true
    private var date: Date? = null
    private val datePicker = DatePickerFragment(onDateSelected = {
        date = it
        with(binding) {
            btnReset.isEnabled = true
            deadlineLayout.switchDeadline.isChecked = true
            date?.let { date ->
                deadlineLayout.tvDate.text = DateFormatter.formatDate(date, DateFormatter.DF2)
                deadlineLayout.tvDate.visibility = View.VISIBLE
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateTaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val task = intent.getParcelableExtra<Task>(Constants.EXTRA_TASK)
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
                        saveTask(
                            description,
                            priority,
                            if (binding.deadlineLayout.switchDeadline.isChecked) date else null
                        )
                    }
                }
                true
            }
            btnRemove.visibility = if (isTaskCreation) View.GONE else View.VISIBLE
            btnReset.visibility = if (isTaskCreation) View.VISIBLE else View.GONE
            etDescription.doAfterTextChanged {
                tvDescriptionError.visibility = View.GONE
                btnReset.isEnabled = it.toString().trim().isNotEmpty()
            }
            btnReset.setOnClickListener {
                clearData()
                btnReset.isEnabled = false
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
        }
        initPriorities()
    }

    private fun setTaskData() {
        priority = task.priority
        date = task.deadline
        with(binding) {
            etDescription.setText(task.description)
            task.deadline?.let {
                deadlineLayout.tvDate.text = DateFormatter.formatDate(it, DateFormatter.DF2)
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

    private fun clearData() {
        with(binding) {
            etDescription.setText("")
            etDescription.clearFocus()
            tvDescriptionError.visibility = View.GONE
            spinnerPriority.setSelection(0)
            deadlineLayout.tvDate.text = ""
            deadlineLayout.tvDate.visibility = View.GONE
            deadlineLayout.switchDeadline.isChecked = false
        }
    }

    private fun saveTask(
        description: String,
        priority: Int,
        date: Date?
    ) {
        val data = Intent()
        if (isTaskCreation) {
            // TODO: 6/25/21 create task in cache
            val task = Task(UUID.randomUUID().toString(), description, date, priority, false)
            data.putExtra(Constants.EXTRA_TASK, task)
            setResult(Constants.RESULT_TASK_CREATED, data)
        } else {
            val task = this.task.copy(description = description, priority = priority, deadline = date)
            data.putExtra(Constants.EXTRA_TASK, task)
            setResult(Constants.RESULT_TASK_EDITED, data)
        }
        finish()
    }
}