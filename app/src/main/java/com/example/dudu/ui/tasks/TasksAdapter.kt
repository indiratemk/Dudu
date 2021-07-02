package com.example.dudu.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dudu.databinding.TaskItemBinding
import com.example.dudu.models.Task
import com.example.dudu.util.DiffCallbackImpl

class TasksAdapter(
    private val listener: TaskClickListener
) : RecyclerView.Adapter<TaskVH>() {

    private var isDoneTasksHidden = true
    var undoneTasks = mutableListOf<Task>()
    var tasks = mutableListOf<Task>()
        set(value) {
            field = value
            undoneTasks = tasks.filter { !it.isDone }.toMutableList()

            val callback = DiffCallbackImpl(
                oldItems = emptyList(),
                newItems = undoneTasks
            )

            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
        }
    private var removedTask: Task? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val inflater = LayoutInflater.from(parent.context)
        return TaskVH(TaskItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        if (isDoneTasksHidden) {
            holder.bind(undoneTasks[position], listener)
        } else {
            holder.bind(tasks[position], listener)
        }
    }

    override fun getItemCount(): Int {
        return if (isDoneTasksHidden)
            undoneTasks.size
        else
            tasks.size
    }

    fun showDoneTasks() {
        isDoneTasksHidden = false

        val callback = DiffCallbackImpl(
            oldItems = undoneTasks,
            newItems = tasks
        )

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun hideDoneTasks() {
        isDoneTasksHidden = true

        undoneTasks.clear()
        undoneTasks.addAll(tasks.filter { !it.isDone })

        val callback = DiffCallbackImpl(
            oldItems = tasks,
            newItems = undoneTasks
        )

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun restoreTask() {
        removedTask?.let{
            addTask(it)
        }
    }

    fun removeTask(position: Int) {
        val callback: DiffCallbackImpl<Task> = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)

            val task = undoneTasks[position]
            undoneTasks.removeAt(position)
            tasks.remove(task)
            removedTask = task

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(tasks)

            removedTask = tasks[position]
            tasks.removeAt(position)

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = tasks
            )
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeTask(task: Task) {
        val position: Int = if (isDoneTasksHidden) {
            undoneTasks.indexOf(task)
        } else {
            tasks.indexOf(task)
        }
        removeTask(position)
    }

    fun updateTaskStatus(position: Int) {
        val callback: DiffCallbackImpl<Task> = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)

            val task = undoneTasks[position]
            undoneTasks[position] = task.copy(isDone = !task.isDone)

            val index = tasks.indexOf(task)
            tasks[index] = undoneTasks[position]

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(tasks)

            val task = tasks[position]
            tasks[position] = task.copy(isDone = !task.isDone)

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = tasks
            )
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTaskStatus(task: Task) {
        val callback: DiffCallbackImpl<Task> = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)

            val position = undoneTasks.indexOfFirst { it.id == task.id }
            undoneTasks[position] = task

            val index = tasks.indexOfFirst { it.id == task.id }
            tasks[index] = task

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks,
                areTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
                areContentsTheSame = { oldItem, newItem -> oldItem.isDone == newItem.isDone }
            )
        } else {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(tasks)

            val position = tasks.indexOfFirst { it.id == task.id }
            tasks[position] = task

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = tasks,
                areTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
                areContentsTheSame = { oldItem, newItem -> oldItem.isDone == newItem.isDone }
            )
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTask(task: Task) {
        val callback: DiffCallbackImpl<Task> = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)

            val position = undoneTasks.indexOfFirst { task.id == it.id }
            undoneTasks[position] = task

            val index = tasks.indexOfFirst { task.id == it.id }
            tasks[index] = task

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks,
                areTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
            )
        } else {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(tasks)

            val position = tasks.indexOfFirst { task.id == it.id }
            tasks[position] = task

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = tasks,
                areTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
            )
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addTask(task: Task) {
        val callback: DiffCallbackImpl<Task> = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)

            undoneTasks.add(task)
            tasks.add(task)

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(tasks)

            tasks.add(task)

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = tasks
            )
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getDoneTasksSize() = tasks.filter { it.isDone }.size
}