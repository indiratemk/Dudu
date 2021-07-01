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
    var doneTasks = mutableListOf<Task>()

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

    fun setTasks(tasks: List<Task>) {
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
            newItems = doneTasks + undoneTasks
        )

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun hideDoneTasks() {
        isDoneTasksHidden = true

        val callback = DiffCallbackImpl(
            oldItems = doneTasks + undoneTasks,
            newItems = undoneTasks
        )

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

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
            val oldItems = doneTasks + undoneTasks

            if (position < doneTasks.size) {
                doneTasks.removeAt(position)
                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
                )
            } else {
                undoneTasks.removeAt(position - doneTasks.size)

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
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
            val oldItems = doneTasks + undoneTasks
            val position = doneTasks.indexOfFirst { it.id == task.id }

            if (position != -1) {
                doneTasks.remove(task)

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
                )
            } else {
                undoneTasks.remove(task)

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
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

            val task = undoneTasks[position]
            undoneTasks[position] = task.copy(isDone = !task.isDone)

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks
            )
        } else {
            val oldItems = doneTasks + undoneTasks

            if (position < doneTasks.size) {
                val task = doneTasks.removeAt(position)
                undoneTasks.add(task.copy(isDone = !task.isDone))

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
                )
            } else {
                val task = undoneTasks[position - doneTasks.size]
                undoneTasks[position - doneTasks.size] = task.copy(isDone = !task.isDone)

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
                )
            }
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTaskStatus(task: Task) {
        val callback = if (isDoneTasksHidden) {
            val oldItems = mutableListOf<Task>()
            oldItems.addAll(undoneTasks)

            val position = undoneTasks.indexOfFirst { it.id == task.id }
            undoneTasks[position] = task

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = undoneTasks,
                areTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
                areContentsTheSame = { oldItem, newItem -> oldItem.isDone == newItem.isDone }
            )
        } else {
            val oldItems = doneTasks + undoneTasks
            var position = doneTasks.indexOfFirst { it.id == task.id }

            if (position != -1) {
                doneTasks.removeAt(position)
                undoneTasks.add(task)

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks
                )
            } else {
                position = undoneTasks.indexOfFirst { it.id == task.id }
                undoneTasks[position] = task

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks,
                    areTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
                    areContentsTheSame = { oldItem, newItem -> oldItem.isDone == newItem.isDone }
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
            val oldItems = doneTasks + undoneTasks
            var position = doneTasks.indexOfFirst { task.id == it.id }

            if (position != -1) {
                doneTasks[position] = task

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks,
                    areTheSame = { oldItem, newItem -> oldItem.id == newItem.id }
                )
            } else {
                position = undoneTasks.indexOfFirst { task.id == it.id }
                undoneTasks[position] = task

                DiffCallbackImpl(
                    oldItems = oldItems,
                    newItems = doneTasks + undoneTasks,
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
            val oldItems = doneTasks + undoneTasks
            undoneTasks.add(task)

            DiffCallbackImpl(
                oldItems = oldItems,
                newItems = doneTasks + undoneTasks
            )
        }

        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getDoneTasksSize() = doneTasks.size + undoneTasks.filter { it.isDone }.size
}