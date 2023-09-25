package com.example.bodycomposition.dao

import com.example.bodycomposition.utils.UserInfo

interface BaseUserDao {
    /**
     * Search for user with lowest distance comparing giving vector
     */
    fun searchUser(faceVector: FloatArray, threshold: Float): User?

    fun getUserById(id: Int): User?

    fun addUser(userInfo: UserInfo, faceVector: FloatArray)

    data class User(val userInfo: UserInfo, val faceVector: FloatArray) {

        // TODO: Customize and overwrite equals and hashCode functions

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as User

            if (userInfo != other.userInfo) return false
            if (!faceVector.contentEquals(other.faceVector)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = userInfo.hashCode()
            result = 31 * result + faceVector.contentHashCode()
            return result
        }
    }
}