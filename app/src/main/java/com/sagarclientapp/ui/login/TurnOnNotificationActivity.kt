package com.sagarclientapp.ui.login

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityTurnOnNotificationBinding
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.model.VerifyOtpResponse
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.login.viewmodels.NotificationViewModel
import com.sagarclientapp.ui.login.viewmodels.OtpViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TurnOnNotificationActivity : BaseActivity() {

    lateinit var binding: ActivityTurnOnNotificationBinding

    private val viewModels: NotificationViewModel by viewModels<NotificationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTurnOnNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
        apisObservers()
    }

    private fun initUis() = binding.apply {
        btSendNotification.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this@TurnOnNotificationActivity,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
            else{
                updateNotificationStatus("1")
            }
            /* launchActivity(
                 TurnOnLocationActivity::class.java,
                 removeAllPrevActivities = false
             )*/
        }
        btNotSendNotification.setOnClickListener {
            updateNotificationStatus("0")

            /*launchActivity(
                TurnOnLocationActivity::class.java,
                removeAllPrevActivities = false
            )*/
        }
    }

    fun updateNotificationStatus(notificationStatus: String) {
        showDialog()
        viewModels.updateNotificationStatus(
            SharedPref.readString(SharedPref.USER_ID),
            SharedPref.readString(SharedPref.DEVICE_ID),
            notification_status = notificationStatus,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun apisObservers() {
        viewModels.notificationUpdateResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                launchActivity(
                                    TurnOnLocationActivity::class.java,
                                    removeAllPrevActivities = false
                                )
                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    hideDialog()

                }

                is Response.Loading -> {
                    try {
                        showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    hideDialog()
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        }  else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }


                }
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            updateNotificationStatus("1")

        }
    }
}