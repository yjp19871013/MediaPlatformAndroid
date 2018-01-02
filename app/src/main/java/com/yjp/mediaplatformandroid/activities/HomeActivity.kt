package com.yjp.mediaplatformandroid.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.adapter.HomeAdapter
import com.yjp.mediaplatformandroid.communicator.HttpCommunicator
import com.yjp.mediaplatformandroid.dto.LogoutForm
import com.yjp.mediaplatformandroid.global.MyApplication
import com.yjp.mediaplatformandroid.global.URLTable
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    companion object {
        private val ITEM_TITLES = arrayListOf("通讯录", "敬请期待", "敬请期待", "敬请期待")
    }

    private var communicator: HttpCommunicator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        communicator = HttpCommunicator(this)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView?.adapter = HomeAdapter(this, ITEM_TITLES)
    }

    override fun onDestroy() {
        super.onDestroy()

        MyApplication.loginFormResponse?.let {
            val logoutForm = LogoutForm()
            logoutForm.id = it.id
            logoutForm.username = it.username

            val jsonLogoutForm = MyApplication.GSON.toJson(logoutForm)
            communicator!!.postAsync(URLTable.LOGOUT,
                    jsonLogoutForm, HttpCommunicator.MEDIA_TYPE_JSON)
        }
    }
}
