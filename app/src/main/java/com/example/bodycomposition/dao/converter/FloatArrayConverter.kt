package com.example.bodycomposition.dao.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class FloatArrayConverter {
    @TypeConverter
    fun FloatArrayToString(floatArray: FloatArray?): String? {
        return floatArray?.joinToString(separator = ";") { it.toString() } ?: ""
    }

    @TypeConverter
    fun StringToFloatArray(string: String?): FloatArray? {
        return string?.split(";")?.map { it.toFloat() }?.toFloatArray() ?: FloatArray(0)
    }
}