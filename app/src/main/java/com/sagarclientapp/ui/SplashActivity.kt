/*
package com.sagarclientapp.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.sagarclientapp.R
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util
import com.sagarclientapp.utils.Util.launchActivity

class SplashActivity : AppCompatActivity() {
    */
/*private val REQUEST_CHECK_SETTINGS = 1001
    private var isLocationDialogHandled = false*//*


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() {

        proceedToNextScreen()
       */
/* val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location services are already enabled; proceed to the next screen
            proceedToNextScreen()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the system dialog to enable location services
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Handle the error
                    proceedToNextScreen() // Proceed even if the dialog fails
                }
            } else {
                proceedToNextScreen() // Proceed if the failure is not resolvable
            }
        }*//*

    }

    private fun proceedToNextScreen() {
       */
/* if (isLocationDialogHandled) return // Prevent multiple navigations
        isLocationDialogHandled = true
*//*

        if (SharedPref.readString(SharedPref.LANGUAGE_ID) == "0") {
            Util.setLocale(this, "en")
        } else if (SharedPref.readString(SharedPref.LANGUAGE_ID) == "1") {
            Util.setLocale(this, "ar")
        }

        if (SharedPref.readBoolean(SharedPref.IS_LOGIN)) {
            launchActivity(HomeActivity::class.java, removeAllPrevActivities = true)
        } else {
            launchActivity(LanguageSelectActivity::class.java, removeAllPrevActivities = true)
        }
    }

    */
/*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // User enabled location services
                proceedToNextScreen()
            } else {
                // User declined location services
                proceedToNextScreen()
            }
        }
    }*//*

}
*/



package com.sagarclientapp.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.sagarclientapp.R
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util
import com.sagarclientapp.utils.Util.launchActivity

class SplashActivity : AppCompatActivity() {
    private  val REQUEST_CHECK_SETTINGS = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()

    }

    private fun initUis() {

       /* val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // Location services are already enabled
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the system dialog to enable location services
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Handle the error
                }
            }
        }
*/
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0"))
            {
                Util.setLocale(this@SplashActivity, "en")

            }
            if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1"))
            {
                Util.setLocale(this@SplashActivity, "ar")
                // Util.changeLanguageAndCheck(this, "ar", R.string.start_booking)

            }
            if (SharedPref.readBoolean(SharedPref.IS_LOGIN)) {

                launchActivity(
                    HomeActivity::class.java,
                    removeAllPrevActivities = true
                )
            }
            else{
                launchActivity(LanguageSelectActivity::class.java, removeAllPrevActivities = true)

            }
        }, 2000)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // Location services enabled
            } else {
                // User declined to enable location services
            }
        }
    }*/

}
