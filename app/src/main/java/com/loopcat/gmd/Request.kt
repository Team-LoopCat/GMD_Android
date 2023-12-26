package com.loopcat.gmd

data class LoginRequest(val userName : String, val password : String)

data class ChangeStatusRequest(val phone: Boolean, val personalLaptop: Boolean, val schoolLaptop: Boolean, val tablet: Boolean)