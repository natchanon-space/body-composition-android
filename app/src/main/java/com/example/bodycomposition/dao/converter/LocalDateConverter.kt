package com.example.bodycomposition.dao.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.bodycomposition.utils.Constant.DATE_FORMAT
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@ProvidedTypeConverter
class LocalDateConverter {
    @TypeConverter
    fun localDateToString(localDate: LocalDate?): String? {
        if (localDate == null) return null
        return localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
    }

    @TypeConverter
    fun stringToLocalDate(string: String?): LocalDate? {
        if (string == null) return null
        return LocalDate.parse(string, DateTimeFormatter.ofPattern(DATE_FORMAT))
    }
}