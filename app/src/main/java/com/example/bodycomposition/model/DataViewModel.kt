package com.example.bodycomposition.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bodycomposition.utils.UserType

class DataViewModel : ViewModel() {

    // TODO: Change all these data type to be more appropriate

    // User date of birth
    private val _dateOfBirth = MutableLiveData<String>()
    val dateOfBirth: LiveData<String> = _dateOfBirth

    // User height in cm unit
    private val _height = MutableLiveData<String>()
    val height: LiveData<String> = _height

    // Auth method
    private val _userType = MutableLiveData<UserType>()
    val userType: LiveData<UserType> = _userType

    // Set data from user input
    fun setData(dateOfBirth: String, height: String) {
        _dateOfBirth.value = dateOfBirth
        _height.value = height
    }

    fun setUserType(userType: UserType) {
        _userType.value = userType
    }
}