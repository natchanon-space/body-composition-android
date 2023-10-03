package com.example.bodycomposition.dao.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class FloatArrayConverter {
    @TypeConverter
    fun floatArrayToString(floatArray: FloatArray?): String? {
        if (floatArray == null) return null
        return floatArray.joinToString(separator = ";") { it.toString() }
    }

    @TypeConverter
    fun stringToFloatArray(string: String?): FloatArray? {
        if (string == null) return null
        return string.split(";").map { it.toFloat() }?.toFloatArray()
    }
}