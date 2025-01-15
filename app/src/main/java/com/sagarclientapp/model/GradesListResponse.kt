package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class GradesListResponse(

	@field:SerializedName("data")
	val data: List<GradesList>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class GradesList(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
