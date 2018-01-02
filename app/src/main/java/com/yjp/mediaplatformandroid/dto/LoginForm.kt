package com.yjp.mediaplatformandroid.dto

data class LoginForm(var username: String = "", var password: String = "")

data class LoginFormResponse(var id: String = "", var username: String = "", var error: String = "")