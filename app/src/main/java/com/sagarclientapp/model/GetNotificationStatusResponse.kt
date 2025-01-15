package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class GetNotificationStatusResponse(

	@field:SerializedName("data")
	val data: GetNotificationStatus? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class GetNotificationStatus(

	@field:SerializedName("notification_status")
	val notificationStatus: Int? = null
)
