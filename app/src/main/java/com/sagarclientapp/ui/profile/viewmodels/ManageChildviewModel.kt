package com.sagarclientapp.ui.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.BusListingResponse
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.ManageChildRepository
import com.sagarclientapp.repository.PageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageChildviewModel @Inject constructor(private val manageChildRepository: ManageChildRepository) :
    ViewModel() {

    var childListResponse = MutableLiveData<Response<ChildListingResponse>>()
    var deleteChildResponse = MutableLiveData<Response<UpdateNotificationStatusResponse>>()

    fun getChildrenList(

        authorization: String,
        parent_id: String,
    ) {
        viewModelScope.launch {
            var response = manageChildRepository.getChildListing(
                authorization = authorization,
                parent_id = parent_id
            )
            childListResponse.postValue(Response.Loading(null))
            childListResponse.postValue(response)
        }
    }

    fun deleteChild(

        authorization: String,
        child_id: String,
        parent_id: String
    ) {
        viewModelScope.launch {
            var response = manageChildRepository.deleteChild(
                authorization = authorization,
                child_id = child_id,
                parent_id = parent_id
            )
            deleteChildResponse.postValue(Response.Loading(null))
            deleteChildResponse.postValue(response)
        }
    }


}