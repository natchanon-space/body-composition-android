package com.example.bodycomposition.model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bodycomposition.utils.UserInfo
import com.example.bodycomposition.utils.UserType

class DataViewModel: ViewModel() {

    // TODO: Change all these data type to be more appropriate

    private val _faceBitmap = MutableLiveData<Bitmap>()
    val faceBitmap: LiveData<Bitmap> = _faceBitmap

    private val _faceVector = MutableLiveData<FloatArray>()
    val faceVector: LiveData<FloatArray> = _faceVector

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> = _userInfo

    private val _userType = MutableLiveData<UserType>()
    val userType: LiveData<UserType> = _userType

    fun setFaceBitmap(bitmap: Bitmap) {
        _faceBitmap.value = bitmap
    }

    fun setFaceVector(vector: FloatArray) {
        _faceVector.value = vector
    }

    fun setUserInfo(userInfo: UserInfo) {
//        _userInfo.value = userInfo
        _userInfo.postValue(userInfo)
    }

    fun setUserType(userType: UserType) {
//        _userType.value = userType
        _userType.postValue(userType)
    }

}