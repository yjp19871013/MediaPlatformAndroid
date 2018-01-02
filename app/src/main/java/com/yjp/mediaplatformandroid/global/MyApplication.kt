package com.yjp.mediaplatformandroid.global

import android.app.Application
import com.google.gson.Gson

class MyApplication : Application() {
    companion object {
        val GSON = Gson()
    }
}