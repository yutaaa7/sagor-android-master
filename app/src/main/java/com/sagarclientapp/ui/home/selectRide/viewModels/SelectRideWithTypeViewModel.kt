package com.sagarclientapp.ui.home.selectRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BusCategoryResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.NotesResponse

import com.sagarclientapp.repository.SelectRideWithTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectRideWithTypeViewModel @Inject constructor(private val repository: SelectRideWithTypeRepository) :
    ViewModel() {

    var vehicleCategoryResponse = MutableLiveData<Response<BusCategoryResponse>>()
    var createBookingResponse = MutableLiveData<Response<CreateBookingResponse>>()
    var notesListingResponse = MutableLiveData<Response<NotesResponse>>()

    fun vehicleCategoryApi(
        authorization: String,
        calculated_distance: String,
        calculated_distance_unit: String,
        trip_mode: String,
        time_difference: String
    ) {
        viewModelScope.launch {
            var response = repository.getVehicleCategories(
                authorization = authorization,
                calculated_distance=calculated_distance,
                calculated_distance_unit=calculated_distance_unit,
                trip_mode=trip_mode,
                time_difference=time_difference,

            )
            vehicleCategoryResponse.postValue(Response.Loading(null))
            vehicleCategoryResponse.postValue(response)
        }
    }


    fun notesListingApi(

        authorization: String,
        note_id: String
    ) {
        viewModelScope.launch {
            var response = repository.getNotesListing(
                authorization = authorization,
                note_id = note_id
            )
            notesListingResponse.postValue(Response.Loading(null))
            notesListingResponse.postValue(response)
        }
    }

    fun createBookingApi(

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
    ) {
        viewModelScope.launch {
            var response = repository.createBooking(
                authorization = authorization,
                userId = userId,
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
            )
            createBookingResponse.postValue(Response.Loading(null))
            createBookingResponse.postValue(response)
        }
    }

    fun createBookingForRoundTripApi(

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
    ) {
        viewModelScope.launch {
            var response = repository.createBookingForRoundTrip(
                authorization = authorization,
                userId = userId,
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
                pickup_location_title=pickup_location_title,
                drop_location_title=drop_location_title,
                approx_total_time=approx_total_time,
                time_difference=time_difference,
            )
            createBookingResponse.postValue(Response.Loading(null))
            createBookingResponse.postValue(response)
        }
    }


}