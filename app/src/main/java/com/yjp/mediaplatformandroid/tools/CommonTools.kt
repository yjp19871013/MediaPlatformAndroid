package com.yjp.mediaplatformandroid.tools

import android.content.Context
import android.net.NetworkInfo
import android.net.ConnectivityManager
import com.google.gson.Gson


object CommonTools {

    //判断网络是否可用
    fun isNetworkReachable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetworkInfo = manager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.state == NetworkInfo.State.CONNECTED
    }
}
