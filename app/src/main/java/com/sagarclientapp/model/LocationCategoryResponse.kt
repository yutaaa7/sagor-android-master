package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class LocationCategoryResponse(

	@field:SerializedName("data")
	val data: List<LocationCategory> = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class LocationCategory(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("sort_order")
	val sortOrder: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("name_en")
	val nameEn: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
