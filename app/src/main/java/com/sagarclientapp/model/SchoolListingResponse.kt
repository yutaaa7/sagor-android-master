package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class SchoolListingResponse(

	@field:SerializedName("data")
	val data: List<SchoolList>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SchoolList(

	@field:SerializedName("name")
	val name: String? = null,
	@field:SerializedName("name_ar")
	val name_ar: String? = null,
	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
