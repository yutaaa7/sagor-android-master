package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class ChildListingResponse(

	@field:SerializedName("data")
	val data: List<ChildListing>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ChildListing(

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("grade_id")
	val gradeId: Int? = null,

	@field:SerializedName("school")
	val school: SchoolChildListing? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("grades")
	val grades: GradesHere? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("student_detail")
	val studentDetail: StudentDetailChildListing? = null
)

data class SchoolChildListing(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class VehicleChildListing(

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("school_trips")
	val schoolTrips: List<SchoolTripsItem?>? = null
)

data class SchoolTripsItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("bus_id")
	val busId: Int? = null,

	@field:SerializedName("ride_status")
	val rideStatus: String? = null
)

data class StudentDetailChildListing(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("vehicle_id")
	val vehicleId: Int? = null,

	@field:SerializedName("vehicle")
	val vehicle: VehicleChildListing? = null
)

data class GradesHere(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
