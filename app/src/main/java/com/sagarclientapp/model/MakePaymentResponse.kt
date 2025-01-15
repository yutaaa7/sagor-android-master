package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class MakePaymentResponse(

	@field:SerializedName("payment_url")
	val paymentUrl: String? = null,

	@field:SerializedName("data")
	val data: MakePayment? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class MakePayment(

	@field:SerializedName("data")
	val data: PaymentData? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,


)

data class PaymentData(



	@field:SerializedName("direct_url")
	val directUrl: String? = null,

	@field:SerializedName("PaymentID")
	val paymentID: String? = null,

	@field:SerializedName("PaymentUrl")
	val paymentUrl: String? = null,

	@field:SerializedName("transit_url")
	val transitUrl: String? = null
)
