package com.example.dudu.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dudu.*
import com.example.dudu.databinding.MainActivityBinding
import com.example.dudu.models.Priority
import com.example.dudu.models.Task
import com.example.dudu.ui.tasks.SwipeHelper
import com.example.dudu.ui.tasks.TaskClickListener
import com.example.dudu.ui.tasks.TasksAdapter
import com.google.android.material.appbar.AppBarLayout
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity(), TaskClickListener {

    companion object {
        const val PERCENTAGE_TO_HIDE_HEADER = 0.3f
        const val PERCENTAGE_TO_SHOW_FIXED_HEADER = 0.9f
    }

    private lateinit var binding: MainActivityBinding
    private val tasksAdapter = TasksAdapter(this)
    private var isHeaderVisible = true
    private var isFixedHeaderVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                handleHeadersVisibility(verticalOffset)
            }
        )
        initRV()
        updateHeader()
    }

    private fun initRV() {
        tasksAdapter.tasks = getMockTasks()
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
                    tasksAdapter.changeTaskStatus(it)
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

    private fun getMockTasks(): MutableList<Task> {
        val tasks = mutableListOf<Task>()
        for (i in 1..25) {
            val task = when {
                i % 2 == 0 -> {
                    Task(i, getString(R.string.task_short), Date(), 1, true)
                }
                i % 3 == 0 -> {
                    Task(i, getString(R.string.task_normal), Date(), 0, false)
                }
                else -> {
                    Task(i, getString(R.string.task_long), Date(), 2, false)
                }
            }
            tasks.add(task)
        }
        return tasks
    }

    private fun handleHeadersVisibility(offset: Int) {
        val maxScroll = binding.appBarLayout.totalScrollRange
        val percentage = abs(offset) / maxScroll.toFloat()
        with(binding) {
            when {
                percentage >= PERCENTAGE_TO_SHOW_FIXED_HEADER -> {
                    if (!isFixedHeaderVisible) {
                        tvToolbarTitle.startAlphaAnimation(100, View.VISIBLE)
                        ibMenuVisibility.startAlphaAnimation(100, View.VISIBLE)
                        tbFixedHeader.elevation =
                            this@MainActivity.resources.getDimensionPixelSize(R.dimen.fixed_toolbar_elevation)
                                .toFloat()
                        isFixedHeaderVisible = true
                    }
                }
                percentage >= PERCENTAGE_TO_HIDE_HEADER -> {
                    if (isHeaderVisible) {
                        clHeader.startAlphaAnimation(200, View.INVISIBLE)
                        isHeaderVisible = false
                    }
                }
                else -> {
                    if (isFixedHeaderVisible) {
                        tvToolbarTitle.startAlphaAnimation(100, View.INVISIBLE)
                        ibMenuVisibility.startAlphaAnimation(100, View.INVISIBLE)
                        tbFixedHeader.elevation = 0f
                        isFixedHeaderVisible = false
                    }
                    if (!isHeaderVisible) {
                        clHeader.startAlphaAnimation(200, View.VISIBLE)
                        isHeaderVisible = true
                    }
                }
            }
        }
    }

    private fun updateHeader() {
        binding.tvCompleted.text = getString(
            R.string.main_completed_label,
            tasksAdapter.getDoneTasksSize()
        )
    }

    override fun onTaskCheckedClick(isChecked: Boolean) {
        updateHeader()
    }
}