package com.yjp.mediaplatformandroid.entities

import com.google.gson.annotations.SerializedName


data class Contact(val id: String,
                   @SerializedName(value = "user_id") val userId: String,
                   val name: String,
                   @SerializedName(value = "phone_number") val phoneNumber: String)