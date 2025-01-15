package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("otp")
	val otp: Int? = null,

	@field:SerializedName("is_already_register")
	val is_already_registered: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
