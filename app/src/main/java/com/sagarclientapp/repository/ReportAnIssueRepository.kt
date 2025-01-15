package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ReportAnIssueRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }



    suspend fun reportAnIssue(
        authorization: String,
        user_id: RequestBody,
        booking_id: RequestBody,
        country_code: RequestBody,
        mobile: RequestBody,
        payment_issue: RequestBody,
        trip_issue: RequestBody,
        find_lost_item: RequestBody,
        other: RequestBody,
        comment: RequestBody,
        image: MutableList<MultipartBody.Part>,
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.reportAnIssue(
                    authorization = authorization,
                    user_id=user_id,
                    booking_id=booking_id,
                    country_code=country_code,
                    mobile=mobile,
                    payment_issue=payment_issue,
                    trip_issue=trip_issue,
                    find_lost_item=find_lost_item,
                    other=other,
                    comment=comment,
                    files =image
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun countryList(): Response<CountryListResponse> {

        return try {
            responseHandler.handleSuccess(apiService.countryList(), AppConstants.USER_LOGIN)
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


}