package com.sagarclientapp.ui.home.selectRide.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class SharedViewModelProvider {
    companion object {
        private var sharedViewModel: SharedViewModel? = null

        fun getSharedViewModel(activity: AppCompatActivity): SharedViewModel {
            if (sharedViewModel == null) {
                sharedViewModel = ViewModelProvider(activity).get(SharedViewModel::class.java)
            }
            return sharedViewModel!!
        }
    }
}