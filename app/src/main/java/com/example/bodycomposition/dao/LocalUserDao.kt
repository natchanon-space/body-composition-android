package com.example.bodycomposition.dao

import com.example.bodycomposition.dao.BaseUserDao.User
import com.example.bodycomposition.utils.UserInfo

class LocalUserDao: BaseUserDao {
    override fun searchUser(faceVector: FloatArray, threshold: Float): User? {
        TODO("Not yet implemented")
    }

    override fun getUserById(id: Int): User? {
        TODO("Not yet implemented")
    }

    override fun addUser(userInfo: UserInfo, faceVector: FloatArray) {
        TODO("Not yet implemented")
    }
}