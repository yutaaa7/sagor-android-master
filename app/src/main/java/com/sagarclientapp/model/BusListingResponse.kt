package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class BusListingResponse(

	@field:SerializedName("data")
	val data: List<BusListing>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class BusListing(

	@field:SerializedName("owner_name")
	val ownerName: String? = null,

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("registration_number")
	val registrationNumber: String? = null,

	@field:SerializedName("vehicle_model")
	val vehicleModel: String? = null,

	@field:SerializedName("deleted_by")
	val deletedBy: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("school_vehicle")
	val schoolVehicle: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("route_description_ar")
	val routeDescriptionAr: String? = null,

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("year_made")
	val yearMade: String? = null,

	@field:SerializedName("owner_contact")
	val ownerContact: String? = null,

	@field:SerializedName("owner_name_ar")
	val ownerNameAr: String? = null,

	@field:SerializedName("route_description")
	val routeDescription: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Any? = null,

	@field:SerializedName("chasis_number")
	val chasisNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("max_seating_capacity")
	val maxSeatingCapacity: Int? = null,

	@field:SerializedName("driver_license")
	val driverLicense: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
