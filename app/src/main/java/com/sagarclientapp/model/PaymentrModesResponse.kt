package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class PaymentrModesResponse(

    @field:SerializedName("data")
    val data: List<PaymentrModesDataItem>? = mutableListOf(),

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class PaymentrModesDataItem(

    @field:SerializedName("image")
    val image: String? = null,
    @field:SerializedName("payment_type")
    val payment_type: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: Any? = null,

    @field:SerializedName("name_ar")
    val nameAr: String? = null,

    @field:SerializedName("created_at")
    val createdAt: Any? = null,

    @field:SerializedName("device_type")
    val deviceType: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("sort_order")
    val sortOrder: Int? = null,

    @field:SerializedName("created_by")
    val createdBy: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("name_en")
    val nameEn: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
