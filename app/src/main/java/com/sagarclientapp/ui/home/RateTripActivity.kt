package com.sagarclientapp.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityRateTripBinding
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.home.viewModels.RateTripViewModel
import com.sagarclientapp.ui.home.viewModels.ReportAnIssueViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RateTripActivity : BaseActivity() {
    lateinit var binding: ActivityRateTripBinding
    var ratingValue: String? = null
    var booking_id: String? = null
    var bundle: Bundle? = null
    private val viewModels: RateTripViewModel by viewModels<RateTripViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRateTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {
        bundle = intent.extras

        if (bundle != null) {
            booking_id = bundle?.getString("booking_id")
        }
        headerRate.headerTitle.text = getString(R.string.rate_your_trip)
        headerRate.ivBack.setOnClickListener {
            finish()
        }

        btRateTripSubmit.setOnClickListener {
            validations()

        }
        rating.setOnRatingBarChangeListener { _, rating, _ ->
            // Do something with the rating
            ratingValue = rating.toString()
            //showToast(rating.toString())
            //Toast.makeText(this, "Rating: $rating", Toast.LENGTH_SHORT).show()
        }
        apisObservers()


    }

    fun validations() {
        if (ratingValue.isNullOrEmpty()) {
            showToast(getString(R.string.please_tell_us_about_your_experience))
        } else if (binding.etRateText.text.toString().isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_description))
        } else {
            rateTripAPi()
        }
    }


    fun rateTripAPi() {
        showDialog()
        viewModels.rateTrip(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            booking_id.toString(), "", ratingValue.toString(), binding.etRateText.text.toString()
        )

    }

    fun apisObservers() {
        viewModels.rateTripResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                showToast(data.message)
                                launchActivity(
                                    HomeActivity::class.java,
                                    removeAllPrevActivities = true
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
                    if (it.errorBody?.status == 422) {
                        showToast(getString(R.string.the_mobile_has_already_been_taken))
                    } else if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }
                        else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        }else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
    }
}