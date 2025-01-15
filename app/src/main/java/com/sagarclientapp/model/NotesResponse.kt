package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class NotesResponse(

	@field:SerializedName("data")
	val data: NotesList? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class NotesList(

	@field:SerializedName("title_ar")
	val titleAr: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("description_en")
	val descriptionEn: String? = null,

	@field:SerializedName("created_at")
	val createdAt: Any? = null,

	@field:SerializedName("title_en")
	val titleEn: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("key")
	val key: Int? = null,

	@field:SerializedName("description_ar")
	val descriptionAr: String? = null
)
