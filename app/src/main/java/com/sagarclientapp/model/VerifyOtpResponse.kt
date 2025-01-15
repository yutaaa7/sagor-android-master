package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Data(

	@field:SerializedName("gender")
	val gender: Any? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("notification_status")
	val notificationStatus: Int? = null,

	@field:SerializedName("last_name")
	val lastName: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("lang_id")
	val langId: Int? = null,

	@field:SerializedName("device_type")
	val deviceType: String? = null,

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("dob")
	val dob: Any? = null,

	@field:SerializedName("device_token")
	val deviceToken: String? = null,

	@field:SerializedName("name")
	val name: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("first_name")
	val firstName: Any? = null,

	@field:SerializedName("email")
	val email: Any? = null,

	@field:SerializedName("location_status")
	val locationStatus: Int? = null
)
