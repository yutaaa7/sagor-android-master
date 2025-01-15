package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class DriverStatusResponse(

	@field:SerializedName("data")
	val data: List<DataItemDriverStatus>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class User(

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
	val schoolId: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("grade_id")
	val gradeId: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("two_factor_confirmed_at")
	val twoFactorConfirmedAt: Any? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("unique_id")
	val uniqueId: Any? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("last_name")
	val lastName: Any? = null,

	@field:SerializedName("driver_rating")
	val driverRating: List<DriverRatingItem?>? = null,

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

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("dob")
	val dob: Any? = null,

	@field:SerializedName("two_factor_recovery_codes")
	val twoFactorRecoveryCodes: Any? = null,

	@field:SerializedName("parent_id")
	val parentId: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: Any? = null,

	@field:SerializedName("two_factor_secret")
	val twoFactorSecret: Any? = null,

	@field:SerializedName("otp_created_at")
	val otpCreatedAt: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class VehicleDriverStatus(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("vehicle_model")
	val vehicleModel: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null
)

data class DriverRatingItem(

	@field:SerializedName("driver_id")
	val driverId: Int? = null,

	@field:SerializedName("average_rating")
	val averageRating: String? = null
)

data class TriplocationsItemDriverStatus(

	@field:SerializedName("drop_location_title")
	val dropLocationTitle: String? = null,

	@field:SerializedName("current_distance_time")
	val currentDistanceTime: String? = null,

	@field:SerializedName("current_location_longitude")
	val currentLocationLongitude: String? = null,

	@field:SerializedName("current_location_title")
	val currentLocationTitle: String? = null,

	@field:SerializedName("current_location_latitude")
	val currentLocationLatitude: String? = null,

	@field:SerializedName("pickup_longitude")
	val pickupLongitude: String? = null,

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("trip_status")
	val tripStatus: String? = null,

	@field:SerializedName("pickup_location")
	val pickupLocation: String? = null,

	@field:SerializedName("drop_location")
	val dropLocation: String? = null,

	@field:SerializedName("trip_phase")
	val tripPhase: String? = null,

	@field:SerializedName("pickup_location_title")
	val pickupLocationTitle: String? = null,

	@field:SerializedName("booking_id")
	val bookingId: Int? = null,

	@field:SerializedName("start_time")
	val startTime: String? = null,

	@field:SerializedName("trip_type")
	val tripType: String? = null,

	@field:SerializedName("current_distance_time_unit")
	val currentDistanceTimeUnit: Any? = null,

	@field:SerializedName("pickup_latitude")
	val pickupLatitude: String? = null,

	@field:SerializedName("drop_latitude")
	val dropLatitude: String? = null,

	@field:SerializedName("start_time_unit")
	val startTimeUnit: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("distance_unit")
	val distanceUnit: String? = null,

	@field:SerializedName("current_location_address")
	val currentLocationAddress: Any? = null,

	@field:SerializedName("drop_longitude")
	val dropLongitude: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
)

data class VehiclecategoriesDriverStatus(

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("per_km_price")
	val perKmPrice: String? = null,

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waiting_price")
	val waitingPrice: String? = null,

	@field:SerializedName("capacity")
	val capacity: Int? = null
)

data class DataItemDriverStatus(

	@field:SerializedName("coupon_code")
	val couponCode: Any? = null,

	@field:SerializedName("total_price")
	val totalPrice: String? = null,

	@field:SerializedName("discount_price")
	val discountPrice: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("vehicle")
	val vehicle: VehicleDriverStatus? = null,

	@field:SerializedName("booking_id")
	val bookingId: String? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,

	@field:SerializedName("triplocations")
	val triplocations: List<TriplocationsItemDriverStatus?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("vehicle_cat_id")
	val vehicleCatId: Int? = null,

	@field:SerializedName("vehiclecategories")
	val vehiclecategories: VehiclecategoriesDriverStatus? = null,

	@field:SerializedName("payment")
	val payment: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("vehicle_id")
	val vehicleId: Int? = null,

	@field:SerializedName("trip_mode")
	val tripMode: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
