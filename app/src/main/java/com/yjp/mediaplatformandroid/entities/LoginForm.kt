package com.yjp.mediaplatformandroid.entities

data class LoginForm(var username: String = "", var password: String = "")

data class LoginFormResponse(var id: String = "", var username: String = "", var error: String = "")