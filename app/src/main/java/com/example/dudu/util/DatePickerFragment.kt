package com.example.dudu.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(
    private val onDateSelected: (Date) -> Unit
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val pickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        pickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        return pickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, day, 0, 0, 0)
        c.set(Calendar.MILLISECOND, 0)
        onDateSelected(c.time)
    }
}