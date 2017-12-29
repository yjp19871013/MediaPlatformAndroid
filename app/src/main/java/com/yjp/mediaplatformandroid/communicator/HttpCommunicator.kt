package com.yjp.mediaplatformandroid.communicator

import android.content.Context
import com.yjp.mediaplatformandroid.tools.CommonTools

import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Request
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus

class HttpCommunicator(private val mContext: Context) {

    /**
     * url: The url of request.
     * success: Request result(true or false).
     * data: If success is true, return body string.
     *       Then if success is false, return error message.
     */
    data class HttpEvent(val url: String, val success: Boolean, val data: String)

    private val mCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            val url = call.request().url().toString()
            if (e.cause == SocketTimeoutException::class.java) {
                EventBus.getDefault().post(HttpEvent(url, false, ERROR_TIMEOUT))
            } else {
                EventBus.getDefault().post(HttpEvent(url, false, ERROR_COMMON))
            }
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val url = call.request().url().toString()
            EventBus.getDefault().post(HttpEvent(url, true, response.body()!!.string()))
        }
    }

    //okHttp
    private val mOkHttpClient = OkHttpClient.Builder()
            .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .build()

    fun getAsync(url: String, params: Map<String, String>?) {

        if (!CommonTools.isNetworkReachable(mContext)) {
            EventBus.getDefault().post(HttpEvent(url, false, ERROR_NETWORK_NOT_REACHABLE))
            return
        }

        val sb = StringBuilder()
        sb.append(url)
        if (params != null && !params.isEmpty()) {
            sb.append("?")
            for ((key, value) in params) {
                sb.append(key)
                sb.append("=")
                sb.append(value)
                sb.append("&")
            }
        }

        val request = Request.Builder()
                .url(sb.substring(0, sb.length - 1))
                .build()

        val call = mOkHttpClient.newCall(request)
        call.enqueue(mCallback)
    }

    fun PostAsync(url: String, postBody: String, mediaType: MediaType) {

        if (!CommonTools.isNetworkReachable(mContext)) {
            EventBus.getDefault().post(HttpEvent(url, false, ERROR_NETWORK_NOT_REACHABLE))
            return
        }

        val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, postBody))
                .build()

        val call = mOkHttpClient.newCall(request)
        call.enqueue(mCallback)
    }

    companion object {
        val ERROR_COMMON = "请求失败"
        val ERROR_TIMEOUT = "请求超时"
        val ERROR_NETWORK_NOT_REACHABLE = "网络未连接"

        private val TIMEOUT_SECONDS = 10
    }
}
