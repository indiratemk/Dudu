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

    private var undoneTasks = mutableListOf<Task>()
    private var doneTasks = mutableListOf<Task>()
    private var isDoneTasksHidden = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val inflater = LayoutInflater.from(parent.context)
        return TaskVH(TaskItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        if (isDoneTasksHidden) {
            holder.bind(undoneTasks[position], listener)
        } else {
            if (position < doneTasks.size) {
                holder.bind(doneTasks[position], listener)
            } else {
                holder.bind(undoneTasks[position - doneTasks.size], listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isDoneTasksHidden)
            undoneTasks.size
        else
            undoneTasks.size + doneTasks.size
    }

    fun setNewTasks(tasks: List<Task>) {
        val callback = DiffCallbackImpl(
            oldItems = emptyList(),
            newItems = undoneTasks
        )
        doneTasks = tasks.filter { it.isDone }.toMutableList()
        undoneTasks = tasks.filter { !it.isDone }.toMutableList()
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun showDoneTasks() {
        isDoneTasksHidden = false
        val callback = DiffCallbackImpl(
            oldItems = undoneTasks,
            newItems = listOf(doneTasks, undoneTasks).flatten()
        )
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun hideDoneTasks() {
        isDoneTasksHidden = true
        val callback = DiffCallbackImpl(
            oldItems = listOf(doneTasks, undoneTasks).flatten(),
            newItems = undoneTasks
        )
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getDoneTasksSize() = doneTasks.size

    fun removeTask(position: Int) {
        val callback = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)
            undoneTasks.removeAt(position)
            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = listOf(doneTasks, undoneTasks).flatten()
            if (position < doneTasks.size) {
                doneTasks.removeAt(position)
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten()
                )
            } else {
                undoneTasks.removeAt(position - doneTasks.size)
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten()
                )
            }
        }
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeTask(task: Task) {
        val callback = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)
            undoneTasks.remove(task)
            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = listOf(doneTasks, undoneTasks).flatten()
            if (task.isDone) {
                doneTasks.remove(task)
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten()
                )
            } else {
                undoneTasks.remove(task)
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten()
                )
            }
        }
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTaskStatus(position: Int) {
        val callback = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)
            val task = undoneTasks.removeAt(position)
            task.isDone = !task.isDone
            doneTasks.add(task)
            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            if (position < doneTasks.size) {
                val oldItems = listOf(doneTasks, undoneTasks).flatten()
                val task = doneTasks.removeAt(position)
                undoneTasks.add(task.copy(isDone = !task.isDone))
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten()
                )
            } else {
                val oldItems = listOf(doneTasks, undoneTasks).flatten()
                val task = undoneTasks.removeAt(position - doneTasks.size)
                doneTasks.add(task.copy(isDone = !task.isDone))
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten()
                )
            }
        }
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTask(task: Task) {
        val callback = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)
            val position = undoneTasks.indexOfFirst { task.id == it.id }
            undoneTasks[position] = task
            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks,
                areTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
            )
        } else {
            if (task.isDone) {
                val oldItems = listOf(doneTasks, undoneTasks).flatten()
                val position = doneTasks.indexOfFirst { task.id == it.id }
                doneTasks[position] = task
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten(),
                    areTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
                )
            } else {
                val oldItems = listOf(doneTasks, undoneTasks).flatten()
                val position = undoneTasks.indexOfFirst { task.id == it.id }
                undoneTasks[position] = task
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = listOf(doneTasks, undoneTasks).flatten(),
                    areTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
                )
            }
        }
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addTask(task: Task) {
        val callback = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)
            undoneTasks.add(task)
            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = listOf(doneTasks, undoneTasks).flatten()
            undoneTasks.add(task)
            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = listOf(doneTasks, undoneTasks).flatten()
            )
        }
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }
}