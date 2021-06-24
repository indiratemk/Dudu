package com.example.dudu.ui.task

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.dudu.R
import com.example.dudu.databinding.CreateTaskActivityBinding
import com.example.dudu.models.Priority
import com.example.dudu.util.DateFormatter
import java.util.*

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: CreateTaskActivityBinding
    private lateinit var prioritiesAdapter: PrioritiesAdapter
    private var priority = Priority.NONE.value
    private var date: Date? = null
    private val datePicker = DatePickerFragment(onDateSelected = {
        date = it
        with(binding) {
            btnRemove.isEnabled = true
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
        initUI()
    }

    private fun initUI() {
        with(binding) {
            toolbar.setNavigationIcon(R.drawable.ic_close)
            toolbar.inflateMenu(R.menu.create_task_menu)
            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.actionSave) {
                    if (etDescription.text.trim().isEmpty()) {
                        tvDescriptionError.visibility = View.VISIBLE
                    } else {
                        val description = etDescription.text.trim().toString()
                        createTask(
                            description,
                            priority,
                            if (binding.deadlineLayout.switchDeadline.isChecked) date else null
                        )
                    }
                }
                true
            }
            etDescription.doAfterTextChanged {
                tvDescriptionError.visibility = View.GONE
                btnRemove.isEnabled = it.toString().trim().isNotEmpty()
            }
            btnRemove.setOnClickListener {
                clearData()
                btnRemove.isEnabled = false
            }
            deadlineLayout.clDeadline.setOnClickListener {
                datePicker.show(supportFragmentManager, "datePicker")
            }
        }
        initPriorities()
    }

    private fun initPriorities() {
        prioritiesAdapter = PrioritiesAdapter(
            this,
            resources.getTextArray(R.array.priority_variants),
            R.layout.chose_priority_item,
            R.layout.priority_item
        )
        binding.spinnerPriority.apply {
            adapter = prioritiesAdapter
            setSelection(0, false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    priority = position
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

    private fun createTask(
        description: String,
        priority: Int,
        date: Date?
    ) {
    }
}