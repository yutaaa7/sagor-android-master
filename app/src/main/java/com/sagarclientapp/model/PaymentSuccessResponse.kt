package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class PaymentSuccessResponse(

	@field:SerializedName("data")
	val data: PaymentDataHere? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PaymentDataHere(

	@field:SerializedName("trackId")
	val trackId: String? = null,

	@field:SerializedName("payzahRefrenceCode")
	val payzahRefrenceCode: String? = null,

	@field:SerializedName("transactionNumber")
	val transactionNumber: String? = null,

	@field:SerializedName("knetPaymentId")
	val knetPaymentId: String? = null,

	@field:SerializedName("paymentId")
	val paymentId: String? = null,

	@field:SerializedName("UDF5")
	val uDF5: String? = null,

	@field:SerializedName("UDF4")
	val uDF4: String? = null,

	@field:SerializedName("UDF3")
	val uDF3: String? = null,

	@field:SerializedName("UDF2")
	val uDF2: String? = null,

	@field:SerializedName("paymentDate")
	val paymentDate: String? = null,

	@field:SerializedName("UDF1")
	val uDF1: String? = null,

	@field:SerializedName("trackingNumber")
	val trackingNumber: String? = null,

	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null
)
