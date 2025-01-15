package com.sagarclientapp.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UserApp : Application() {
    /*companion object {
        lateinit var sharedViewModel: SharedViewModel
    }*/
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        SharedPref.initialize(this)
        //sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
    }
}