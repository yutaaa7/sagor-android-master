package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.BusListingResponse
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class ManageChildRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
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


    suspend fun deleteChild(
        authorization: String,
        child_id: String,
        parent_id: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.deleteChild(

                    authorization = authorization,
                    child_id = child_id,
                    parent_id = parent_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}