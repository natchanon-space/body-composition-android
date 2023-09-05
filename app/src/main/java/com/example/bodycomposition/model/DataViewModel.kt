package com.example.bodycomposition.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

enum class UserType {
    GUEST,
    AUTH
}

class DataViewModel : ViewModel() {

    // User date of birth
    private val _dateOfBirth = MutableLiveData<Date>()
    val dateOfBirth: LiveData<Date> = _dateOfBirth

    // User height in cm unit
    private val _height = MutableLiveData<Float>()
    val height: LiveData<Float> = _height

    // Auth method
    private val _userType = MutableLiveData<UserType>()
    val userType: LiveData<UserType> = _userType

    // Set data from user input
    fun setData(dateOfBirth: Date, height: Float) {
        _dateOfBirth.value = dateOfBirth
        _height.value = height
    }

    fun setUserType(userType: UserType) {
        _userType.value = userType
    }
}