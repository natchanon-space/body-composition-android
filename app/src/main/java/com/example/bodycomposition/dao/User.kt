package com.example.bodycomposition.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class User(
    // Insert methods treat 0 as not set while inserting item
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "height") val height: Int?,
    @ColumnInfo(name = "date_of_birth") val date: LocalDate?,
    @ColumnInfo(name = "sex") val sex: String?,
    @ColumnInfo(name = "faceVector") val faceVector: FloatArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (height != other.height) return false
        if (date != other.date) return false
        if (sex != other.sex) return false
        if (faceVector != null) {
            if (other.faceVector == null) return false
            if (!faceVector.contentEquals(other.faceVector)) return false
        } else if (other.faceVector != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (height ?: 0)
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + (sex?.hashCode() ?: 0)
        result = 31 * result + (faceVector?.contentHashCode() ?: 0)
        return result
    }
}
