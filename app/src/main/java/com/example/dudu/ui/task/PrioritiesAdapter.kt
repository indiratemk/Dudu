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


class PrioritiesAdapter(
    context: Context,
    items: Array<CharSequence>,
    @LayoutRes val viewResId: Int,
    @LayoutRes val dropDownViewResId: Int
) : ArrayAdapter<CharSequence>(
    context, viewResId, items
) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: layoutInflater.inflate(viewResId, parent, false)
        val tvPriority = view.findViewById<TextView>(R.id.tvPriority)
        tvPriority.text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater.inflate(dropDownViewResId, parent, false)
        val tvPriority = view.findViewById<TextView>(R.id.tvPriority)
        tvPriority.text = getItem(position)
        if (getItem(position) == context.getString(R.string.create_task_priority_high)) {
            tvPriority.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        return view
    }
}