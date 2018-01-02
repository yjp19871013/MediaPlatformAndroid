package com.yjp.mediaplatformandroid.activities

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.communicator.HttpCommunicator
import com.yjp.mediaplatformandroid.databinding.ActivityLoginBinding
import com.yjp.mediaplatformandroid.dialogs.WaitDialog
import com.yjp.mediaplatformandroid.entities.LoginForm
import com.yjp.mediaplatformandroid.entities.LoginFormResponse
import com.yjp.mediaplatformandroid.global.MyApplication
import com.yjp.mediaplatformandroid.global.URLTable
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : AppCompatActivity() {

    private var mDataBinding: ActivityLoginBinding? = null
    private var loginForm = LoginForm()
    private var communicator: HttpCommunicator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mDataBinding!!.loginForm = loginForm

        loginButton.setOnClickListener {
            doLogin()
        }

        communicator = HttpCommunicator(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onPause() {
        super.onPause()
        WaitDialog.dismissWaitDialog()
    }

    private fun doLogin() {
        WaitDialog.showWaitDialog(this, "正在登录")
        val jsonLoginForm = MyApplication.GSON.toJson(loginForm, LoginForm::class.java)
        communicator!!.postAsync(URLTable.LOGIN, jsonLoginForm, HttpCommunicator.MEDIA_TYPE_JSON)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginComplete(event: HttpCommunicator.HttpEvent) {
        WaitDialog.dismissWaitDialog()

        if (URLTable.LOGIN != event.url) {
            return
        }

        if (event.success == false) {
            Toast.makeText(this, event.data, Toast.LENGTH_SHORT).show()
            return
        }

        val loginFormResponse = MyApplication.GSON.fromJson(event.data, LoginFormResponse::class.java)
        if (!loginFormResponse.error.isEmpty()) {
            Toast.makeText(this, loginFormResponse.error, Toast.LENGTH_SHORT).show()
            return
        }

        MyApplication.loginFormResponse = loginFormResponse
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
