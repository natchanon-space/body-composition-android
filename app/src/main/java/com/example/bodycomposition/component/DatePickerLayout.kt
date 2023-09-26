package com.example.bodycomposition.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout

/**
 * `TextInputLayout` with date picker input
 */
class DatePickerLayout(context: Context, attrs: AttributeSet?) : TextInputLayout(context, attrs) {

    // TODO: Customize date picker here
    private val picker = MaterialDatePicker.Builder.datePicker().build()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpPicker(fragmentManager: FragmentManager) {
        // Update edit text when ok button is update
        picker.addOnPositiveButtonClickListener {
            editText?.setText(picker.headerText)
        }

        // Replace keyboard input with date picker
        editText?.focusable = View.NOT_FOCUSABLE
        editText?.setOnClickListener{
            picker.show(fragmentManager, TAG)
        }
    }

    companion object {
        const val TAG = "DatePicker"
    }
}