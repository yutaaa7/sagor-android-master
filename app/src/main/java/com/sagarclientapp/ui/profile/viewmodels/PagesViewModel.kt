package com.sagarclientapp.ui.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.GetNotificationStatusResponse
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.repository.PageRepository
import com.sagarclientapp.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagesViewModel @Inject constructor(private val pageRepository: PageRepository) :
    ViewModel() {

    var pageResponse = MutableLiveData<Response<PagesResponse>>()

    fun pagesData(
        page: String,

    ) {
        viewModelScope.launch {
            var response = pageRepository.getPageText(
                page = page
            )
            pageResponse.postValue(Response.Loading(null))
            pageResponse.postValue(response)
        }
    }


}