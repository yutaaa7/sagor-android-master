package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.GoogleMapsService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.BannerResponse
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.DirectionsResponse
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject
import javax.inject.Named

class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun getProfile(
        user_id: String,
        authorization: String
    ): Response<GetProfileResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getUserProfile(
                    userId = user_id,
                    authorization = authorization

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

    suspend fun updateFcmToken(

        authorization: String,
        country_code: String,
        mobile: String,
        device_type: String,
        device_token: String,
        device_version: String,
        fcm_token: String,

    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.updateFCmToken(
                    authorization = authorization,
                    country_code = country_code,
                    mobile = mobile,
                    device_type = device_type,
                    device_token = device_token,
                    device_version = device_version,
                    fcm_token = fcm_token,

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getChildListing(
        authorization: String,
        parent_id: String
    ): Response<ChildListingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.childList(

                    authorization = authorization,
                    parent_id = parent_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


    suspend fun getBannerList(
        authorization: String
    ): Response<BannerResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.bannersListing(
                    authorization = authorization
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
    suspend fun onGoingTrips(
        authorization: String,
        user_id:String,
        booking_status:String,
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.onGoingTrips(
                    authorization = authorization,
                    user_id = user_id,
                    booking_status = booking_status,

                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }
}