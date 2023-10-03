package com.example.bodycomposition.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    // Insert methods treat 0 as not set while inserting item
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "height") val height: Int?,
    @ColumnInfo(name = "faceVector") val faceVector: FloatArray?
)
