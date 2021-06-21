package com.example.dudu

import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.models.Task

class TaskVH(
    private val binding: TaskItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task) {
        with(binding) {
            cbStatus.isChecked = task.isDone
            cbStatus.setOnClickListener {
                task.isDone = !task.isDone
                handleTaskStatus(task.isDone)
            }
            tvDescription.text = task.description
            handleTaskStatus(task.isDone)
        }
    }

    private fun handleTaskStatus(isDone: Boolean) {
        binding.tvDescription.apply {
            paintFlags = if (isDone) {
                setTextColor(ContextCompat.getColor(itemView.context, R.color.label_tertiary))
                paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                setTextColor(ContextCompat.getColor(itemView.context, R.color.label_primary))
                paintFlags.rem(Paint.STRIKE_THRU_TEXT_FLAG)
            }
        }
    }
}