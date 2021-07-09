package com.example.dudu.util

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
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

    fun showSnackbar(
        view: View,
        text: String
    ) {
       Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }

    fun createDialogWithAction(
        context: Context,
        @StringRes messageRes: Int,
        onCancel: () -> Unit,
        onPositive: () -> Unit,
        onNegative: () -> Unit
    ) : MaterialDialog {
        return MaterialDialog(context)
            .message(messageRes)
            .cancelable(true)
            .onCancel {
                onCancel()
                it.dismiss()
            }
            .positiveButton(R.string.main_task_removing_positive) { dialog ->
                onPositive()
                dialog.dismiss()
            }
            .negativeButton(R.string.main_task_removing_negative) { dialog ->
                onNegative()
                dialog.dismiss()
            }
    }
}