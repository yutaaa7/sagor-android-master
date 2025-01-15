package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class CountryListResponse(

	@field:SerializedName("data")
	val data: List<CountryList>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class CountryList(

	@field:SerializedName("flag")
	val flag: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("phone_code")
	val phoneCode: String? = null
)
