package com.example.bodycomposition.model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FaceRegistrationViewModel: ViewModel() {

    private val _faceBitmap = MutableLiveData<Bitmap>()
    val faceBitmap: LiveData<Bitmap> = _faceBitmap

    fun setFaceBitmap(bitmap: Bitmap?) {
        _faceBitmap.value = bitmap
    }
}