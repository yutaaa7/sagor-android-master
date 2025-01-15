package com.sagarclientapp.ui.home.selectRide.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel


class SharedViewModel : ViewModel() {
    val currentLocation = MutableLiveData<LatLng>()
    val destinationLocation = MutableLiveData<LatLng>()
    val currentAddress = MutableLiveData<String>()
    val currentName = MutableLiveData<String>()
    val destinationAddress = MutableLiveData<String>()
    val destinationName = MutableLiveData<String>()
}