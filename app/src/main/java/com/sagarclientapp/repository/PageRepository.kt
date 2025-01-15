package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class PageRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun getPageText(
        page: String
    ): Response<PagesResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.getPageText(
                    page = page
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}