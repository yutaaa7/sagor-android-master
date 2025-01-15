package com.sagarclientapp.repository

import com.sagarclientapp.api.ApiService
import com.sagarclientapp.api.Response
import com.sagarclientapp.api.ResponseHandler
import com.sagarclientapp.model.BusListingResponse
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GradesListResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.SchoolListingResponse
import com.sagarclientapp.model.SchoolStudentListResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.utils.AppConstants
import javax.inject.Inject

class SchoolRepository @Inject constructor(private val apiService: ApiService) {
    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    suspend fun getSchoolList(
        authorization: String,
        search_keyword: String
    ): Response<SchoolListingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.schoolListing(
                    authorization = authorization,
                    search_keyword = search_keyword
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getGradesList(
        authorization: String,
        school_id: String
    ): Response<GradesListResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.GradesListing(
                    authorization = authorization,
                    school_id = school_id
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }

    suspend fun getSchoolBusesList(
        authorization: String,
        school_id: String
    ): Response<BusListingResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.schoolBusesListing(
                    authorization = authorization,
                    school_id = school_id
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

    suspend fun schoolStudentList(
        authorization: String,
        search_keyword: String,
        school_id: String,
        grade_id: String,
        bus_id: String,
        country_code: String,
        phone: String,
    ): Response<SchoolStudentListResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.schoolStudentListing(
                    authorization = authorization,
                    search_keyword = search_keyword,
                    school_id = school_id,
                    grade_id = grade_id,
                    bus_id = bus_id,
                    country_code = country_code,
                    phone = phone
                ),
                AppConstants.USER_LOGIN
            )
        } catch (e: Exception) {
            e.printStackTrace()
            responseHandler.handleException(e, AppConstants.USER_LOGIN)
        }
    }


    suspend fun addChild(
        authorization: String,
        child_id: String,
        parent_id: String
    ): Response<UpdateNotificationStatusResponse> {

        return try {
            responseHandler.handleSuccess(
                apiService.addChild(
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