package com.yjp.mediaplatformandroid.activities

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yjp.mediaplatformandroid.R
import com.yjp.mediaplatformandroid.databinding.ActivityLoginBinding
import com.yjp.mediaplatformandroid.dto.LoginForm
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mDataBinding: ActivityLoginBinding? = null
    private var loginForm =  LoginForm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mDataBinding!!.loginForm = loginForm

        loginButton.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        Toast.makeText(this,
                "username:" + loginForm.username + " password:" + loginForm.password,
                Toast.LENGTH_SHORT).show()
    }
}
