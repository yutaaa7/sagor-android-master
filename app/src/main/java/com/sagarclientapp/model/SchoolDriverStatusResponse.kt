package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class SchoolDriverStatusResponse(

	@field:SerializedName("data")
	val data: List<DataItemSchoolDriverStatus>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SchoolDriverStatus(

	@field:SerializedName("school_emergency")
	val schoolEmergency: SchoolEmergency? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class VehicleOFVehicleDriverStatus(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("vehicle_model")
	val vehicleModel: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: UserOfSchoolDriver? = null
)

data class SchoolEmergency(

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("contact_no")
	val contactNo: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class SchoolTripChildrensItem(

	@field:SerializedName("trip_id")
	val tripId: Int? = null,

	@field:SerializedName("student_id")
	val studentId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: List<UserItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItemSchoolDriverStatus(

	@field:SerializedName("current_location_longitude")
	val currentLocationLongitude: String? = null,

	@field:SerializedName("driver_id")
	val driverId: Int? = null,

	@field:SerializedName("start_location_address_ar")
	val startLocationAddressAr: String? = null,

	@field:SerializedName("distance")
	val distance: Any? = null,

	@field:SerializedName("end_longitude")
	val endLongitude: String? = null,

	@field:SerializedName("end_location_address_ar")
	val endLocationAddressAr: String? = null,

	@field:SerializedName("start_location_title_ar")
	val startLocationTitleAr: String? = null,

	@field:SerializedName("speed_km_p_h")
	val speedKmPH: String? = null,

	@field:SerializedName("deleted_by")
	val deletedBy: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("end_latitude")
	val endLatitude: String? = null,

	@field:SerializedName("vehicle")
	val vehicle: VehicleOFVehicleDriverStatus? = null,

	@field:SerializedName("booking_id")
	val bookingId: Any? = null,

	@field:SerializedName("end_location_address")
	val endLocationAddress: String? = null,

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("school")
	val school: SchoolDriverStatus? = null,

	@field:SerializedName("start_latitude")
	val startLatitude: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("bus_id")
	val busId: Int? = null,

	@field:SerializedName("distance_unit")
	val distanceUnit: Any? = null,

	@field:SerializedName("end_location_title_ar")
	val endLocationTitleAr: String? = null,

	@field:SerializedName("current_location_address")
	val currentLocationAddress: Any? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("end_time_unit")
	val endTimeUnit: Any? = null,

	@field:SerializedName("ride_status")
	val rideStatus: String? = null,

	@field:SerializedName("current_location_title")
	val currentLocationTitle: Any? = null,

	@field:SerializedName("current_location_latitude")
	val currentLocationLatitude: String? = null,

	@field:SerializedName("school_trip_childrens")
	val schoolTripChildrens: List<SchoolTripChildrensItem?>? = null,

	@field:SerializedName("start_location_address")
	val startLocationAddress: String? = null,

	@field:SerializedName("distance_covered_unit")
	val distanceCoveredUnit: Any? = null,

	@field:SerializedName("start_location_title")
	val startLocationTitle: String? = null,

	@field:SerializedName("end_time")
	val endTime: Any? = null,

	@field:SerializedName("distance_covered")
	val distanceCovered: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Any? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("expected_end_time")
	val expectedEndTime: String? = null,

	@field:SerializedName("expected_start_time")
	val expectedStartTime: String? = null,

	@field:SerializedName("start_time")
	val startTime: Any? = null,

	@field:SerializedName("start_longitude")
	val startLongitude: String? = null,

	@field:SerializedName("end_location_title")
	val endLocationTitle: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Any? = null,

	@field:SerializedName("start_time_unit")
	val startTimeUnit: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UserOfSchoolDriver(

	@field:SerializedName("country_code")
	val countryCode: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("last_name")
	val lastName: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("gender")
	val gender: Any? = null,

	@field:SerializedName("otp_expired_at")
	val otpExpiredAt: Any? = null,

	@field:SerializedName("deleted_by")
	val deletedBy: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("grade_id")
	val gradeId: Any? = null,

	@field:SerializedName("two_factor_confirmed_at")
	val twoFactorConfirmedAt: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("unique_id")
	val uniqueId: Any? = null,

	@field:SerializedName("otp")
	val otp: Any? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any? = null,

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("dob")
	val dob: Any? = null,

	@field:SerializedName("two_factor_recovery_codes")
	val twoFactorRecoveryCodes: Any? = null,

	@field:SerializedName("parent_id")
	val parentId: Any? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Any? = null,

	@field:SerializedName("two_factor_secret")
	val twoFactorSecret: Any? = null,

	@field:SerializedName("otp_created_at")
	val otpCreatedAt: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class UserItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("avatar")
	val avatar: Any? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null
)
