package com.example.dudu.ui.tasks

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.R
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.models.Priority
import com.example.dudu.models.Task
import com.example.dudu.util.DateFormatter

class TaskVH(
    private val binding: TaskItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task, listener: TaskClickListener) {
        with(binding) {
            cbStatus.isChecked = task.isDone
            cbStatus.setOnClickListener {
                task.isDone = !task.isDone
                handleTaskStatus(task.isDone, task.priority)
                listener.onTaskCheckedClick(task.isDone)
            }
            tvDescription.text = task.description
            tvDeadline.visibility = if (task.deadline == null) View.GONE else View.VISIBLE
            tvDeadline.text = task.deadline?.let { DateFormatter.formatDate(it, DateFormatter.DF1) }
            handleTaskStatus(task.isDone, task.priority)
        }
    }

    private fun handleTaskStatus(isDone: Boolean, priority: Int) {
        binding.tvDescription.apply {
            paintFlags = if (isDone) {
                setTextColor(ContextCompat.getColor(itemView.context, R.color.label_tertiary))
                paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                setTextColor(ContextCompat.getColor(itemView.context, R.color.label_primary))
                paintFlags.rem(Paint.STRIKE_THRU_TEXT_FLAG)
            }
        }

        binding.ivPriority.apply {
            visibility = when {
                !isDone && priority == Priority.LOW.value -> {
                    setImageDrawable(ContextCompat.getDrawable(itemView.context,
                        R.drawable.ic_low_priority
                    ))
                    View.VISIBLE
                }
                !isDone && priority == Priority.HIGH.value -> {
                    setImageDrawable(ContextCompat.getDrawable(itemView.context,
                        R.drawable.ic_high_priority
                    ))
                    View.VISIBLE
                }
                else -> {
                    View.GONE
                }
            }
        }
    }
}