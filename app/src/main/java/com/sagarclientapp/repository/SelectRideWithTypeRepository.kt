package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.BusCategoryResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.NotesResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class SelectRideWithTypeRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun getVehicleCategories(
        authorization: String,
        calculated_distance: String,
        calculated_distance_unit: String,
        trip_mode: String,
        time_difference: String,

    ): Response<BusCategoryResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getVehicleCategories(
                    authorization = authorization,
                    calculated_distance=calculated_distance,
                    calculated_distance_unit=calculated_distance_unit,
                    trip_mode=trip_mode,
                    time_difference=time_difference,

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getNotesListing(
        authorization: String,
        note_id: String
    ): Response<NotesResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.notesListing(
                    authorization = authorization,
                    note_id = note_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun createBooking(
        authorization: String,
        userId: String,
        pickup_location: String,
        pickup_latitude: String,
        pickup_longitude: String,
        drop_location: String,
        drop_latitude: String,
        drop_longitude: String,
        distance: String,
        distance_unit: String,
        pickup_date: String,
        pickup_time: String,
        pickup_time_unit: String,
        trip_mode: String,
        vehicle_cat_id: String,
        pickup_location_title: String,
        drop_location_title: String,
        approx_total_time: String,

        ): Response<CreateBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.createBooking(
                    authorization = authorization,
                    user_id = userId,
                    pickup_location = pickup_location,
                    pickup_latitude = pickup_latitude,
                    pickup_longitude = pickup_longitude,
                    drop_location = drop_location,
                    drop_latitude = drop_latitude,
                    drop_longitude = drop_longitude,
                    distance = distance,
                    distance_unit = distance_unit,
                    pickup_date = pickup_date,
                    pickup_time = pickup_time,
                    pickup_time_unit = pickup_time_unit,
                    trip_mode = trip_mode,
                    vehicle_cat_id = vehicle_cat_id,
                    pickup_location_title = pickup_location_title,
                    drop_location_title = drop_location_title,
                    approx_total_time = approx_total_time,


                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun createBookingForRoundTrip(
        authorization: String,
        userId: String,
        pickup_location: String,
        pickup_latitude: String,
        pickup_longitude: String,
        drop_location: String,
        drop_latitude: String,
        drop_longitude: String,
        distance: String,
        distance_unit: String,
        pickup_date: String,
        pickup_time: String,
        pickup_time_unit: String,
        trip_mode: String,
        vehicle_cat_id: String,
        round_pickup_location: String,
        round_pickup_latitude: String,
        round_pickup_longitude: String,
        round_drop_location: String,
        round_drop_latitude: String,
        round_drop_longitude: String,
        round_pickup_date: String,
        round_pickup_time: String,
        round_pickup_time_unit: String,
        pickup_location_title: String,
        drop_location_title: String,
        approx_total_time: String,
        time_difference: String,

        ): Response<CreateBookingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.createBookingForRoundTrip(
                    authorization = authorization,
                    user_id = userId,
                    pickup_location = pickup_location,
                    pickup_latitude = pickup_latitude,
                    pickup_longitude = pickup_longitude,
                    drop_location = drop_location,
                    drop_latitude = drop_latitude,
                    drop_longitude = drop_longitude,
                    distance = distance,
                    distance_unit = distance_unit,
                    pickup_date = pickup_date,
                    pickup_time = pickup_time,
                    pickup_time_unit = pickup_time_unit,
                    trip_mode = trip_mode,
                    vehicle_cat_id = vehicle_cat_id,
                    round_pickup_location = round_pickup_location,
                    round_pickup_latitude = round_pickup_latitude,
                    round_pickup_longitude = round_pickup_longitude,
                    round_drop_location = round_drop_location,
                    round_drop_latitude = round_drop_latitude,
                    round_drop_longitude = round_drop_longitude,
                    round_pickup_date = round_pickup_date,
                    round_pickup_time = round_pickup_time,
                    round_pickup_time_unit = round_pickup_time_unit,
                    pickup_location_title = pickup_location_title,
                    drop_location_title = drop_location_title,
                    round_pickup_location_title = drop_location_title,
                    round_drop_location_title = pickup_location_title,
                    approx_total_time = approx_total_time,
                    time_difference = time_difference,

                    ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}