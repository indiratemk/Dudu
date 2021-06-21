package com.example.dudu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.models.Task

class TasksAdapter : RecyclerView.Adapter<TaskVH>() {

    var tasks = emptyList<Task>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val inflater = LayoutInflater.from(parent.context)
        return TaskVH(TaskItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size
}