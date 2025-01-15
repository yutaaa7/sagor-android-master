package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class GetPhoneNumberResponse(

	@field:SerializedName("data")
	val data: GetPhoneNumber? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class GetPhoneNumber(

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null
)
