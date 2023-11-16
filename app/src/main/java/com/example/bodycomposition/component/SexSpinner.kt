package com.example.bodycomposition.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSpinner
import cn.icomon.icdevicemanager.model.other.ICConstant

@RequiresApi(Build.VERSION_CODES.O)
class SexSpinner(context: Context, attrs: AttributeSet?) : AppCompatSpinner(context, attrs) {

    private val items = arrayOf("Unspecified", "Male", "Female")
    private val itemValues = arrayOf(
        ICConstant.ICSexType.ICSexTypeUnknown,
        ICConstant.ICSexType.ICSexTypeMale,
        ICConstant.ICSexType.ICSexTypeFemal)

    init {
        adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items)
    }

    fun getSelectedItemValue(): ICConstant.ICSexType {
        return itemValues[selectedItemPosition]
    }
}