package com.example.dudu.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.dudu.R
import com.google.android.material.snackbar.Snackbar

object UIUtil {

    fun showSnackbar(
        view: View,
        text: String,
        actionText: String,
        action: () -> Unit
    ) {
        val snackbar = Snackbar.make(view, text,
            Snackbar.LENGTH_LONG).setAction(actionText) { action() }
        snackbar.setActionTextColor(ContextCompat.getColor(view.context, R.color.blue))
        val actionTextView: TextView =
            snackbar.view.findViewById(com.google.android.material.R.id.snackbar_action)
        actionTextView.isAllCaps = false
        snackbar.show()
    }
}