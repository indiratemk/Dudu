package com.example.dudu.ui.task

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.example.dudu.R
import com.example.dudu.data.Priority


class PrioritiesAdapter(
    context: Context,
    @LayoutRes val viewResId: Int,
    @LayoutRes val dropDownViewResId: Int
) : ArrayAdapter<Priority>(
    context, viewResId, Priority.values()
) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: layoutInflater.inflate(viewResId, parent, false)
        val tvPriority = view.findViewById<TextView>(R.id.tvPriority)
        getItem(position)?.let {
            tvPriority.text = getPriorityTitle(it)
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater.inflate(dropDownViewResId, parent, false)
        val tvPriority = view.findViewById<TextView>(R.id.tvPriority)
        getItem(position)?.let {
            tvPriority.text = getPriorityTitle(it)
            if (it == Priority.HIGH) {
                tvPriority.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        }
        return view
    }

    private fun getPriorityTitle(priority: Priority): String {
        return when (priority) {
            Priority.NONE -> context.getString(R.string.create_task_priority_none)
            Priority.LOW -> context.getString(R.string.create_task_priority_low)
            Priority.HIGH -> context.getString(R.string.create_task_priority_high)
        }
    }
}