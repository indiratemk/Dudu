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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.util.*


class CreateTaskActivity : AppCompatActivity()  {

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

    private lateinit var binding: CreateTaskActivityBinding
    private lateinit var prioritiesAdapter: PrioritiesAdapter
    private lateinit var task: Task
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
        val data = Intent()
        if (isTaskCreation) {
            val task = Task(UUID.randomUUID().toString(), description, date, priority, false)
            saveTask(task)
            data.putExtra(Constants.EXTRA_TASK, task)
            setResult(Constants.RESULT_TASK_CREATED, data)
        } else {
            val task = this.task.copy(
                description = description,
                priority = priority,
                deadline = date
            )
            data.putExtra(Constants.EXTRA_TASK, task)
            setResult(Constants.RESULT_TASK_EDITED, data)
        }
        finish()
    }

    private fun saveTask(task: Task) {
        val tasks = mutableListOf<Task>()
        try {
            tasks.addAll(readFromCache())
            tasks.add(task)
        } catch (e: IOException) {
            e.printStackTrace()
            if (e is FileNotFoundException) {
                tasks.add(task)
            }
        }
        writeToCache(tasks)
    }

    private fun writeToCache(tasks: List<Task>) {
        try {
            val data = Gson().toJson(tasks).toString()
            val outputStream =
                FileOutputStream(File("$cacheDir/${Constants.FILE_NAME}${Constants.FILE_EXTENSION}"))
            outputStream.use { it.write(data.toByteArray()) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readFromCache(): List<Task> {
        return try {
            val inputStream =
                FileInputStream(File("$cacheDir/${Constants.FILE_NAME}${Constants.FILE_EXTENSION}"))
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }
            val json = String(buffer, Charsets.UTF_8)
            val type = object : TypeToken<List<Task>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }
}