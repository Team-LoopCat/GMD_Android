package com.loopcat.gmd

data class LoginResponse(val accessToken : String)
data class Box(val boxID: Int, val stuId: Int, val stuName: String, val status : Boolean)
data class BoxResponse(val boxs: List<Box>)

data class DeviceList(val phone: Boolean, val personalLabtob: Boolean, val schoolLabtob: Boolean, val tablet: Boolean)
data class InfoResponse(val status: DeviceList)