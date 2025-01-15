package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class BusCategoryResponse(

    @field:SerializedName("data")
    val data: List<BusCategory>? = mutableListOf(),

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class BusCategory(

    @field:SerializedName("per_km_price")
    val perKmPrice: String? = null,

    @field:SerializedName("calculated_price")
    val calculated_price: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("name_ar")
    val name_ar: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("waiting_price")
    val waitingPrice: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("capacity")
    val capacity: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
