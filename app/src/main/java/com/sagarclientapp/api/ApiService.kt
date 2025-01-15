package com.sagarclientapp.api

import com.sagarclientapp.model.BannerResponse
import com.sagarclientapp.model.BookingDetailResponse
import com.sagarclientapp.model.BusCategoryResponse
import com.sagarclientapp.model.BusListingResponse
import com.sagarclientapp.model.CancelBookingReasonsResponse
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.ConfirmBookingResponse
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.CreateBooking
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.model.GetLanguageStatus
import com.sagarclientapp.model.GetLanguageStatusResponse
import com.sagarclientapp.model.GetNotificationStatusResponse
import com.sagarclientapp.model.GetPhoneNumberResponse
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.GradesListResponse
import com.sagarclientapp.model.HistoryResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.MakePaymentResponse
import com.sagarclientapp.model.NotesResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.model.SavedPlacesResponse
import com.sagarclientapp.model.SchoolDriverStatusResponse
import com.sagarclientapp.model.SchoolListingResponse
import com.sagarclientapp.model.SchoolStudentListResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.model.VerifyOtpResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(COUNTRY_LIST)
    suspend fun countryList(
    ): CountryListResponse

    @FormUrlEncoded
    @POST(LOGIN_API)
    suspend fun login(
        @Field("country_code") country_code: String,
        @Field("mobile") mobile: String,
        @Field("type") type: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,

        ): LoginResponse

    @FormUrlEncoded
    @POST(RESEND_OTP_API)
    suspend fun resendOtp(
        @Field("country_code") country_code: String,
        @Field("mobile") mobile: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
    ): LoginResponse

    @FormUrlEncoded
    @POST(UPDATE_FCM_TOKEN)
    suspend fun updateFCmToken(
        @Header("Authorization") authorization: String,
        @Field("country_code") country_code: String,
        @Field("mobile") mobile: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("device_version") device_version: String,
        @Field("fcm_token") fcm_token: String,
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(VERIFY_OTP)
    suspend fun verifyOtp(
        @Field("country_code") country_code: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,

        ): VerifyOtpResponse

    @FormUrlEncoded
    @POST(NOTIFICATION_STATUS_UPDATE)
    suspend fun updateNotificationStatus(
        @Field("user_id") user_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("notification_status") notification_status: String,
        @Header("Authorization") authorization: String
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(UPDATE_PROFILE)
    suspend fun updateProfile(
        @Field("user_id") user_id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("gender") gender: String,
        @Header("Authorization") authorization: String
    ): UpdateNotificationStatusResponse

    @GET(GET_PROFILE)
    suspend fun getUserProfile(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String
    ): GetProfileResponse

    @FormUrlEncoded
    @POST(GET_VEHICLE_CATEGORIES)
    suspend fun getVehicleCategories(
        @Header("Authorization") authorization: String,
        @Field("calculated_distance") calculated_distance: String,
        @Field("calculated_distance_unit") calculated_distance_unit: String,
        @Field("trip_mode") trip_mode: String,
        @Field("time_difference") time_difference: String,


    ): BusCategoryResponse

    @GET(LAST_SEARCH_LOCATION_LIST)
    suspend fun getLastSearchedLocationList(
        @Header("Authorization") authorization: String
    ): LastSearchLocationsResponse

    @GET(GET_PAGE_TEXT)
    suspend fun getPageText(
        @Path("page") page: String
    ): PagesResponse

    @DELETE(DELETE_ACCOUNT)
    suspend fun deleteAccount(
        @Header("Authorization") authorization: String
    ): UpdateNotificationStatusResponse

    @POST(LOGOUT)
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(DRIVER_STATUS)
    suspend fun driverStatus(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
    ): DriverStatusResponse

    @FormUrlEncoded
    @POST(CHILD_DRIVER_STATUS)
    suspend fun childDriverStatus(
        @Header("Authorization") authorization: String,
        @Field("ride_id") ride_id: String,
        @Field("child_id") child_id: String,
    ): SchoolDriverStatusResponse

    @FormUrlEncoded
    @POST(GET_NOTIFICATION_STATUS)
    suspend fun getNotificationStatus(
        @Field("user_id") user_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Header("Authorization") authorization: String
    ): GetNotificationStatusResponse

    @FormUrlEncoded
    @POST(GET_PHONE_NUMBER)
    suspend fun getPhoneNumber(
        @Field("user_id") user_id: String,
        @Header("Authorization") authorization: String
    ): GetPhoneNumberResponse

    @FormUrlEncoded
    @POST(EDIT_PHONE_NUMBER)
    suspend fun editPhoneNumber(

        @Header("Authorization") authorization: String,
        @Field("pre_country_code") pre_country_code: String,
        @Field("pre_mobile") pre_mobile: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("type") type: String,
        @Field("device_version") device_version: String,
        @Field("new_country_code") new_country_code: String,
        @Field("new_mobile") new_mobile: String,
    ): LoginResponse

    @FormUrlEncoded
    @POST(VERIFY_OTP_FOR_UPDATE_PHONE_NUMBER)
    suspend fun verifyOtpForUpdatePhoneNumber(
        @Header("Authorization") authorization: String,
        @Field("country_code") country_code: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("device_version") device_version: String,
    ): VerifyOtpResponse

    @Multipart
    @POST(REPORT_AN_ISSUE)
    suspend fun reportAnIssue(
        @Header("Authorization") authorization: String,
        @Part("user_id") user_id: RequestBody,
        @Part("booking_id") booking_id: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("payment_issue") payment_issue: RequestBody,
        @Part("trip_issue") trip_issue: RequestBody,
        @Part("find_lost_item") find_lost_item: RequestBody,
        @Part("other") other: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(RATING)
    suspend fun rating(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("booking_id") booking_id: String,
        @Field("driver_id") driver_id: String,
        @Field("rating") rating: String,
        @Field("comment") comment: String,
    ): UpdateNotificationStatusResponse


    @POST(LOCATION_CATEGORY)
    suspend fun getLocationCategory(
        @Header("Authorization") authorization: String
    ): LocationCategoryResponse

    @POST(CANCEL_RIDE_REASONS)
    suspend fun cancelRideReasons(
        @Header("Authorization") authorization: String
    ): CancelBookingReasonsResponse

    @FormUrlEncoded
    @POST(CANCEL_BOOKINGS)
    suspend fun cancelBooking(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("user_id") user_id: String,
        @Field("reason_id") reason_id: String,
    ): CreateBookingResponse

    @FormUrlEncoded
    @POST(LOCATION_LIST_BY_CATEGORY_ID)
    suspend fun getLocationListByCategoryId(
        @Header("Authorization") authorization: String,
        @Field("category_id") category_id: String
    ): LastSearchLocationsResponse

    @FormUrlEncoded
    @POST(LANGUAGE_UPDATE)
    suspend fun languageUpdate(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("lang_id") lang_id: String,
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(GET_LANGUAGE_STATUS)
    suspend fun getLanguageStatus(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,

        ): GetLanguageStatusResponse

    @FormUrlEncoded
    @POST(ADD_LAST_SEARCH_LOCATION)
    suspend fun addLastSearchLocation(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("address") address: String,
        @Field("lat") lat: String,
        @Field("long") long: String,
        @Field("title") title: String,
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(ADD_TO_WISHLIST)
    suspend fun addToWishList(
        @Header("Authorization") authorization: String,
        @Field("location_id") location_id: String,
        @Field("wishlist_flag") wishlist_flag: String,

        ): UpdateNotificationStatusResponse


    @POST(SAVED_LOCATIONS)
    suspend fun savedLocationsList(
        @Header("Authorization") authorization: String,
    ): SavedPlacesResponse


    @FormUrlEncoded
    @POST(CREATE_BOOKING)
    suspend fun createBookingForRoundTrip(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("pickup_location") pickup_location: String,
        @Field("pickup_latitude") pickup_latitude: String,
        @Field("pickup_longitude") pickup_longitude: String,
        @Field("drop_location") drop_location: String,
        @Field("drop_latitude") drop_latitude: String,
        @Field("drop_longitude") drop_longitude: String,
        @Field("distance") distance: String,
        @Field("distance_unit") distance_unit: String,
        @Field("pickup_date") pickup_date: String,
        @Field("pickup_time") pickup_time: String,
        @Field("pickup_time_unit") pickup_time_unit: String,
        @Field("trip_mode") trip_mode: String,
        @Field("vehicle_cat_id") vehicle_cat_id: String,
        @Field("round_pickup_location") round_pickup_location: String,
        @Field("round_pickup_latitude") round_pickup_latitude: String,
        @Field("round_pickup_longitude") round_pickup_longitude: String,
        @Field("round_drop_location") round_drop_location: String,
        @Field("round_drop_latitude") round_drop_latitude: String,
        @Field("round_drop_longitude") round_drop_longitude: String,
        @Field("round_pickup_date") round_pickup_date: String,
        @Field("round_pickup_time") round_pickup_time: String,
        @Field("round_pickup_time_unit") round_pickup_time_unit: String,
        @Field("pickup_location_title") pickup_location_title: String,
        @Field("drop_location_title") drop_location_title: String,
        @Field("round_pickup_location_title") round_pickup_location_title: String,
        @Field("round_drop_location_title") round_drop_location_title: String,
        @Field("approx_total_time") approx_total_time: String,
        @Field("time_difference") time_difference: String,


        ): CreateBookingResponse

    @FormUrlEncoded
    @POST(CREATE_BOOKING)
    suspend fun createBooking(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("pickup_location") pickup_location: String,
        @Field("pickup_latitude") pickup_latitude: String,
        @Field("pickup_longitude") pickup_longitude: String,
        @Field("drop_location") drop_location: String,
        @Field("drop_latitude") drop_latitude: String,
        @Field("drop_longitude") drop_longitude: String,
        @Field("distance") distance: String,
        @Field("distance_unit") distance_unit: String,
        @Field("pickup_date") pickup_date: String,
        @Field("pickup_time") pickup_time: String,
        @Field("pickup_time_unit") pickup_time_unit: String,
        @Field("trip_mode") trip_mode: String,
        @Field("vehicle_cat_id") vehicle_cat_id: String,
        @Field("pickup_location_title") pickup_location_title: String,
        @Field("drop_location_title") drop_location_title: String,
        @Field("approx_total_time") approx_total_time: String,

        ): CreateBookingResponse


    @FormUrlEncoded
    @POST(APPLY_COUPON)
    suspend fun applyCoupon(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("booking_id") booking_id: String,
        @Field("coupon") coupon: String,
        @Field("price") price: String,
    ): CreateBookingResponse

    @FormUrlEncoded
    @POST(REMOVE_COUPON)
    suspend fun removeCoupon(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("booking_id") booking_id: String,
        @Field("coupon") coupon: String,
        @Field("price") price: String,
    ): CreateBookingResponse

    @FormUrlEncoded
    @POST(PAYMENT_MODES)
    suspend fun paymentMethodsAPi(
        @Header("Authorization") authorization: String,
        @Field("device_type") device_type: String
    ): PaymentrModesResponse

    @FormUrlEncoded
    @POST(MAKE_PAYMENT)
    suspend fun makePayment(
        @Header("Authorization") authorization: String,
        @Field("amount") amount: String,
        @Field("udf1") udf1: String,
        @Field("udf2") udf2: String,
        @Field("udf3") udf3: String,
        @Field("udf4") udf4: String,
        @Field("udf5") udf5: String,
        @Field("payment_type") payment_type: String,
    ): MakePaymentResponse

    /* @FormUrlEncoded
     @POST(MAKE_PAYMENT)
     suspend fun makePayment(
         @Header("Authorization") authorization: String,
         @Query("amount") amount: String,
         @Query("udf1") udf1: String,
         @Query("udf2") udf2: String,
         @Query("udf3") udf3: String,
         @Query("udf4") udf4: String,
         @Query("udf5") udf5: String,
         @Query("payment_type") payment_type: String,
     ): MakePaymentResponse*/

    @FormUrlEncoded
    @POST(CONFIRM_PAYMENT)
    suspend fun confirmPayment(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("booking_id") booking_id: String,
        @Field("payment_method") payment_method: String,
        @Field("payment_type") payment_type: String,
        @Field("payzahRefrenceCode") payzahRefrenceCode: String,
        @Field("trackId") trackId: String,
        @Field("knetPaymentId") knetPaymentId: String,
        @Field("transactionNumber") transactionNumber: String,
        @Field("trackingNumber") trackingNumber: String,
        @Field("paymentDate") paymentDate: String,
        @Field("paymentStatus") paymentStatus: String,
        @Field("udf1") udf1: String,
        @Field("udf2") udf2: String,
        @Field("udf3") udf3: String,
        @Field("udf4") udf4: String,
        @Field("coupon_code") coupon_code: String,
    ): ConfirmBookingResponse


    @FormUrlEncoded
    @POST(SCHOOL_LIST)
    suspend fun schoolListing(
        @Header("Authorization") authorization: String,
        @Field("search_keyword") search_keyword: String
    ): SchoolListingResponse

    @FormUrlEncoded
    @POST(NOTES_LIST)
    suspend fun notesListing(
        @Header("Authorization") authorization: String,
        @Field("note_id") note_id: String
    ): NotesResponse


    @POST(BANNERS_LIST)
    suspend fun bannersListing(
        @Header("Authorization") authorization: String
    ): BannerResponse


    @FormUrlEncoded
    @POST(ONGOING_TRIPS)
    suspend fun onGoingTrips(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("booking_status") booking_status: String,
    ): UpdateNotificationStatusResponse


    @FormUrlEncoded
    @POST(BOOKING_DETAIL)
    suspend fun bookingDetails(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String
    ): BookingDetailResponse

    @FormUrlEncoded
    @POST(BOOKING_HISTORY)
    suspend fun bookingHistory(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String,
        @Field("booking_status") booking_status: String
    ): HistoryResponse

    @FormUrlEncoded
    @POST(SCHEDULED_TRIP_LIST)
    suspend fun ScheduledTripsList(
        @Header("Authorization") authorization: String,
        @Field("user_id") user_id: String
    ): HistoryResponse

    @FormUrlEncoded
    @POST(GRADES_LISTING)
    suspend fun GradesListing(
        @Header("Authorization") authorization: String,
        @Field("school_id") school_id: String
    ): GradesListResponse

    @FormUrlEncoded
    @POST(SCHOOL_BUSES_LISTING)
    suspend fun schoolBusesListing(
        @Header("Authorization") authorization: String,
        @Field("school_id") school_id: String
    ): BusListingResponse


    @FormUrlEncoded
    @POST(SCHOOL_STUDENT_LISTING)
    suspend fun schoolStudentListing(
        @Header("Authorization") authorization: String,
        @Field("search_keyword") search_keyword: String,
        @Field("school_id") school_id: String,
        @Field("grade_id") grade_id: String,
        @Field("bus_id") bus_id: String,
        @Field("country_code") country_code: String,
        @Field("phone") phone: String,

        ): SchoolStudentListResponse

    @FormUrlEncoded
    @POST(ADD_CHILD)
    suspend fun addChild(
        @Header("Authorization") authorization: String,
        @Field("child_id") child_id: String,
        @Field("parent_id") parent_id: String

    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(CHILD_LIST)
    suspend fun childList(
        @Header("Authorization") authorization: String,
        @Field("parent_id") parent_id: String

    ): ChildListingResponse

    @FormUrlEncoded
    @POST(RESCHEDULED_BOOKING)
    suspend fun RescheduledBookingForOneWay(
        @Header("Authorization") authorization: String,
        @Field("booking_id") booking_id: String,
        @Field("pickup_date") pickup_date: String,
        @Field("pickup_time") pickup_time: String,
        @Field("pickup_time_unit") pickup_time_unit: String,
        @Field("trip_mode") trip_mode: String,
    ): BookingDetailResponse

    @Multipart
    @POST(CONTACT_US)
    suspend fun contactUs(
        @Header("Authorization") authorization: String,
        @Part("user_id") user_id: RequestBody,
        @Part("message") message: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): UpdateNotificationStatusResponse

    @FormUrlEncoded
    @POST(DELETE_CHILD)
    suspend fun deleteChild(
        @Header("Authorization") authorization: String,
        @Field("child_id") child_id: String,
        @Field("parent_id") parent_id: String
    ): UpdateNotificationStatusResponse


}

