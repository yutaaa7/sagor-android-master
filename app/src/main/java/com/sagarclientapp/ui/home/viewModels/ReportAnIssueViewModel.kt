package com.sagarclientapp.ui.home.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.LoginRepository
import com.sagarclientapp.repository.ReportAnIssueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ReportAnIssueViewModel @Inject constructor(private val reportAnIssueRepository: ReportAnIssueRepository) :
    ViewModel() {

    var reportAnIssueResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()
    var countryListResponse = MutableLiveData<Response<CountryListResponse>>()

    fun reportAnIssue(
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
    ) {
        viewModelScope.launch {
            var response = reportAnIssueRepository.reportAnIssue(
                authorization = authorization,
                user_id = user_id,
                booking_id = booking_id,
                country_code = country_code,
                mobile = mobile,
                payment_issue = payment_issue,
                trip_issue = trip_issue,
                find_lost_item = find_lost_item,
                other = other,
                comment = comment,
                image = image
            )
            reportAnIssueResponse.postValue(Response.Loading(null))
            reportAnIssueResponse.postValue(response)
        }
    }

    fun countryListApi() {
        viewModelScope.launch {
            var response = reportAnIssueRepository.countryList()
            countryListResponse.postValue(Response.Loading(null))
            countryListResponse.postValue(response)
        }
    }


}