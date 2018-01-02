package com.yjp.mediaplatformandroid.global

import android.app.Application
import com.google.gson.Gson
import com.yjp.mediaplatformandroid.dto.LoginFormResponse

class MyApplication : Application() {
    companion object {
        val GSON = Gson()
        var loginFormResponse: LoginFormResponse? = null
    }
}