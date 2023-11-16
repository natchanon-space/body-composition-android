package com.example.bodycomposition.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.example.bodycomposition.utils.Constant.DATE_FORMAT
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone


/**
 * `TextInputLayout` with date picker input
 */
class DatePickerLayout(context: Context, attrs: AttributeSet?) : TextInputLayout(context, attrs) {

    // TODO: Customize date picker here
    private val picker = MaterialDatePicker.Builder.datePicker()
        .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpPicker(fragmentManager: FragmentManager) {
        // Update edit text when ok button is update
        picker.addOnPositiveButtonClickListener {selection ->

            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val format = SimpleDateFormat(DATE_FORMAT)
            val formattedDate: String = format.format(calendar.time)

            editText?.setText(formattedDate)
        }

        // Replace keyboard input with date picker
        editText!!.showSoftInputOnFocus = false // prevent editText to show input keyboard
        editText!!.setOnClickListener{
            picker.show(fragmentManager, TAG)
        }
        editText!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                picker.show(fragmentManager, TAG)
            }
        }

        // Losing focus
        picker.addOnPositiveButtonClickListener {
            editText!!.clearFocus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDate(): LocalDate? {
        var localDate: LocalDate? = null
        if (editText?.text != null) {
            localDate = LocalDate.parse(editText?.text, DateTimeFormatter.ofPattern(DATE_FORMAT))
        }
        return localDate
    }

    companion object {
        const val TAG = "DatePicker"
    }
}