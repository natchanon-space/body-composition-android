package com.example.bodycomposition.dao.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate

@ProvidedTypeConverter
class LocalDateConverter {
    @TypeConverter
    fun localDateToString(localDate: LocalDate?): String? {
        TODO("implement this")
    }

    @TypeConverter
    fun stringToLocalDate(string: String?): LocalDate? {
        TODO("implement this")
    }
}