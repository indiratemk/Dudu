package com.example.dudu.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.data.models.Task
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.util.DiffCallbackImpl

class TaskAdapter(
    private val listener: TaskClickListener
) : RecyclerView.Adapter<TaskVH>() {

    var tasks = emptyList<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val inflater = LayoutInflater.from(parent.context)
        return TaskVH(TaskItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bind(tasks[position], listener)
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(tasks: List<Task>) {
        val callback = DiffCallbackImpl(
            oldItems = this.tasks,
            newItems = tasks,
            areTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )

        this.tasks = tasks

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun isEmpty() = itemCount == 0
}