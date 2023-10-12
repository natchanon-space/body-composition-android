package com.example.bodycomposition.utils

import cn.icomon.icdevicemanager.model.other.ICConstant
import java.time.LocalDate

data class UserInfo(
    var dateOfBirth: LocalDate,
    var height: Int,
    var name: String,
    var sex: ICConstant.ICSexType
) {
    companion object {

    }
}