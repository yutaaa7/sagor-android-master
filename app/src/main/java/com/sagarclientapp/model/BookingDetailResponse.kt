package com.sagarclientapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookingDetailResponse(

	@field:SerializedName("data")
	val data: List<BookingDetail>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
): Parcelable

@Parcelize
data class Triplocations(

	@field:SerializedName("drop_location_title")
	val dropLocationTitle: String? = null,

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

	@field:SerializedName("drop_longitude")
	val dropLongitude: String? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null
): Parcelable

@Parcelize
data class BookingDetail(

	@field:SerializedName("coupon_code")
	val couponCode: String? = null,

	@field:SerializedName("approx_total_time")
	val approx_total_time: String? = null,

	@field:SerializedName("total_price")
	val totalPrice: String? = null,

	@field:SerializedName("discount_price")
	val discountPrice: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("booking_id")
	val bookingId: String? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,

	@field:SerializedName("triplocations")
	val triplocations: List<Triplocations?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("vehicle_cat_id")
	val vehicleCatId: Int? = null,

	@field:SerializedName("vehiclecategories")
	val vehiclecategories: VehiclecategoriesBookings? = null,

	@field:SerializedName("payment")
	val payment: PaymentBooking? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("trip_mode")
	val tripMode: String? = null,

	@field:SerializedName("status")
	val status: String? = null
): Parcelable

@Parcelize
data class VehiclecategoriesBookings(

	@field:SerializedName("image")
	val image: String? = null,

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
): Parcelable

@Parcelize
data class PaymentBooking(

	@field:SerializedName("trackId")
	val trackId: String? = null,

	@field:SerializedName("payzahRefrenceCode")
	val payzahRefrenceCode: String? = null,

	@field:SerializedName("transactionNumber")
	val transactionNumber: String? = null,

	@field:SerializedName("udf5")
	val udf5: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("udf3")
	val udf3: String? = null,

	@field:SerializedName("knetPaymentId")
	val knetPaymentId: String? = null,

	@field:SerializedName("udf4")
	val udf4: String? = null,

	@field:SerializedName("udf1")
	val udf1: String? = null,

	@field:SerializedName("udf2")
	val udf2: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: String? = null,

	@field:SerializedName("booking_id")
	val bookingId: Int? = null,

	@field:SerializedName("payment_type")
	val paymentType: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("paymentDate")
	val paymentDate: String? = null,

	@field:SerializedName("trackingNumber")
	val trackingNumber: String? = null,

	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null
): Parcelable
