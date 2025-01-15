package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.GoogleMapsService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.DirectionsResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject
import javax.inject.Named

class SelectRideRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("directionsService") private val googleMapsService: GoogleMapsService
) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun getLocationCategory(

        authorization: String
    ): Response<LocationCategoryResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getLocationCategory(
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getLocationListByCategoryId(

        authorization: String,
        category_id: String
    ): Response<LastSearchLocationsResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getLocationListByCategoryId(
                    authorization = authorization,
                    category_id = category_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getLastSearchedLocation(

        authorization: String
    ): Response<LastSearchLocationsResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getLastSearchedLocationList(
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun addToWishList(

        authorization: String,
        locationId: String,
        wishlistFlag: String

    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.addToWishList(

                    authorization = authorization,
                    location_id = locationId,
                    wishlist_flag = wishlistFlag
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getDirections(
        origin: String,
        destination: String,
        apiKey: String
    ): Response<DirectionsResponse> {
        return try {
            responseHandler.handleSuccess(
                googleMapsService.getDirections(origin, destination, apiKey),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}