package com.loopcat.gmd

import android.util.Log

class Token {
    @Volatile
    private var token: String = ""

    fun getToken(): String {
        Log.d("token", "getToken : $token")
        return token
    }
    fun setToken(token: String) {
        synchronized(this) {
            this.token = token
            Log.d("token", "setToken : ${this.token}")
        }
    }
}