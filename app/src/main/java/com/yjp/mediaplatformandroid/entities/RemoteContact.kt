package com.yjp.mediaplatformandroid.entities

import com.google.gson.annotations.SerializedName


data class RemoteContact(val id: String = "",
                         @SerializedName(value = "user_id") val userId: String = "",
                         val name: String = "",
                         @SerializedName(value = "phone_number") val phoneNumber: String = "")

data class RemoteContactQueryResponse(val data: List<RemoteContact>, val error: String = "")

data class RemoteContactUpdateResponse(val error: String = "")

data class RemoteContactOperation(val id: String = "",
                                  @SerializedName(value = "user_id") val userId: String = "",
                                  val operation: String = "",
                                  @SerializedName(value = "new_phone_number") val newPhoneNumber: String = "",
                                  @SerializedName(value = "new_name") val newName: String = "",
                                  val contacts: RemoteContact)

data class RemoteContactOperationResponse(val data: List<RemoteContactOperation>,
                                          val error: String = "")