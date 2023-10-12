package com.example.bodycomposition.utils

import java.time.LocalDate

data class UserInfo(
    var dateOfBirth: LocalDate,
    var height: Int,
    var name: String,
    var sex: Int
)