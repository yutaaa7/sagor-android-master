package com.sagarclientapp.ui.child.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BusListingResponse
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GradesListResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.SchoolListingResponse
import com.sagarclientapp.model.SchoolStudentListResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.PageRepository
import com.sagarclientapp.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolViewModel @Inject constructor(private val schoolRepository: SchoolRepository) :
    ViewModel() {

    var schoolListResponse = MutableLiveData<Response<SchoolListingResponse>>()
    var gradesListResponse = MutableLiveData<Response<GradesListResponse>>()
    var schoolBusesListResponse = MutableLiveData<Response<BusListingResponse>>()
    var countryListResponse = MutableLiveData<Response<CountryListResponse>>()
    var schoolStudentListResponse = MutableLiveData<Response<SchoolStudentListResponse>>()
    var addChildResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()


    fun getSchoolListing(
        authorization: String,
        search_keyword: String
    ) {
        viewModelScope.launch {
            var response = schoolRepository.getSchoolList(
                authorization = authorization, search_keyword = search_keyword
            )
            schoolListResponse.postValue(Response.Loading(null))
            schoolListResponse.postValue(response)
        }
    }

    fun getGradesListing(
        authorization: String,
        school_id: String
    ) {
        viewModelScope.launch {
            var response = schoolRepository.getGradesList(
                authorization = authorization, school_id = school_id
            )
            gradesListResponse.postValue(Response.Loading(null))
            gradesListResponse.postValue(response)
        }
    }

    fun getSchoolBusesListing(
        authorization: String,
        school_id: String
    ) {
        viewModelScope.launch {
            var response = schoolRepository.getSchoolBusesList(
                authorization = authorization, school_id = school_id
            )
            schoolBusesListResponse.postValue(Response.Loading(null))
            schoolBusesListResponse.postValue(response)
        }
    }

    fun countryListApi() {
        viewModelScope.launch {
            var response = schoolRepository.countryList()
            countryListResponse.postValue(Response.Loading(null))
            countryListResponse.postValue(response)
        }
    }

    fun schoolStudentListApi(
        authorization: String,
        search_keyword: String,
        school_id: String,
        grade_id: String,
        bus_id: String,
        country_code: String,
        phone: String,
    ) {
        viewModelScope.launch {
            var response = schoolRepository.schoolStudentList(
                authorization = authorization,
                search_keyword = search_keyword,
                school_id = school_id,
                grade_id = grade_id,
                bus_id = bus_id,
                country_code = country_code,
                phone = phone
            )
            schoolStudentListResponse.postValue(Response.Loading(null))
            schoolStudentListResponse.postValue(response)
        }
    }


    fun addChildApi(
        authorization: String,
        child_id: String,
        parent_id: String
    ) {
        viewModelScope.launch {
            var response = schoolRepository.addChild(
                authorization = authorization,
                child_id = child_id,
                parent_id = parent_id
            )
            addChildResponse.postValue(Response.Loading(null))
            addChildResponse.postValue(response)
        }
    }


}