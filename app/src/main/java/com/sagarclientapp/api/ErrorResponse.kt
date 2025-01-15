package com.sagarclientapp.api

import com.google.gson.annotations.SerializedName

data class ErrorResponse(@field:SerializedName("status") val status: Int,
                         @field:SerializedName("message") val message: String,
                         @field:SerializedName("messageCode") val messageCode: String,
                         @field:SerializedName("success") val success: Boolean,
                         @field:SerializedName("errors") val error: String,
                         @field:SerializedName("timestamp") val timeStamp: Long,
                         @field:SerializedName("error") val errorMessages: ArrayList<String>)