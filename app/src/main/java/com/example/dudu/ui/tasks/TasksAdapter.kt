package com.example.dudu.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.models.Task

class TasksAdapter(
    private val listener: TaskClickListener
) : RecyclerView.Adapter<TaskVH>() {

    var tasks = mutableListOf<Task>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val inflater = LayoutInflater.from(parent.context)
        return TaskVH(TaskItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bind(tasks[position], listener)
    }

    override fun getItemCount() = tasks.size

    fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeTask(task: Task) {
        val position = tasks.indexOfFirst { task.id == it.id }
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun changeTaskStatus(position: Int) {
        tasks[position].isDone = !tasks[position].isDone
        notifyItemChanged(position)
    }

    fun updateTask(task: Task) {
        val position = tasks.indexOfFirst { task.id == it.id }
        tasks[position] = task
        notifyItemChanged(position)
    }

    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    fun getDoneTasksSize() = tasks.filter { it.isDone }.size
}