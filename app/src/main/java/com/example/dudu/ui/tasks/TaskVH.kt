package com.example.dudu.ui.tasks

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.R
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.data.Priority
import com.example.dudu.data.local.TaskEntity
import com.example.dudu.util.DateFormatter

class TaskVH(
    private val binding: TaskItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: TaskEntity, listener: TaskClickListener) {
        with(binding) {
            cbStatus.isChecked = task.isDone
            cbStatus.setOnClickListener {
                listener.onCheckBoxClick(task, cbStatus.isChecked)
            }
            clTask.setOnClickListener { listener.onTaskClick(task) }
            tvDescription.text = task.description
            tvDeadline.visibility = if (task.deadline == 0L) View.GONE else View.VISIBLE
            tvDeadline.text = task.deadline.let { DateFormatter.formatDate(it, DateFormatter.DF1) }
            handleStatus(task)
        }
    }

    private fun handleStatus(task: TaskEntity) {
        with(binding) {
            if (task.isDone) {
                tvDescription.setTextColor(ContextCompat.getColor(itemView.context,
                    R.color.label_tertiary))
                tvDescription.paintFlags = tvDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                ivPriority.visibility = View.GONE
            } else {
                tvDescription.setTextColor(ContextCompat.getColor(itemView.context,
                    R.color.label_primary))
                tvDescription.paintFlags = tvDescription.paintFlags.rem(Paint.STRIKE_THRU_TEXT_FLAG)
                handlePriority(task.priority)
                handleDeadline(task.deadline)
            }
        }
    }

    private fun handlePriority(priority: Int) {
        with(binding.ivPriority) {
            when (priority) {
                Priority.HIGH.value -> {
                    setImageDrawable(ContextCompat.getDrawable(itemView.context,
                        R.drawable.ic_high_priority
                    ))
                    visibility = View.VISIBLE
                }
                Priority.LOW.value -> {
                    setImageDrawable(ContextCompat.getDrawable(itemView.context,
                        R.drawable.ic_low_priority
                    ))
                    visibility = View.VISIBLE
                }
                else -> {
                    visibility = View.GONE
                }
            }
        }
    }

    private fun handleDeadline(deadline: Long) {
        with(binding) {
            if (deadline == 0L) {
                cbStatus.buttonDrawable =
                    ContextCompat.getDrawable(itemView.context, R.drawable.cb_normal_selector)
                return
            }
            val currentDateInMillis = DateFormatter.getCurrentDateWithoutTime().time
            if (deadline >= currentDateInMillis) {
                cbStatus.buttonDrawable =
                    ContextCompat.getDrawable(itemView.context, R.drawable.cb_normal_selector)
            } else {
                cbStatus.buttonDrawable =
                    ContextCompat.getDrawable(itemView.context, R.drawable.cb_expired_selector)
            }
        }
    }
}