package com.sagarclientapp.ui.checkout

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityCancelRideBinding
import com.sagarclientapp.model.CancelBookingReasons
import com.sagarclientapp.model.CancelBookingReasonsResponse
import com.sagarclientapp.ui.checkout.viewModels.CancelBookingViewModels
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.sagarclientapp.model.BookingDetail
import com.sagarclientapp.model.CreateBookingResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelRideActivity : BaseActivity() {
    lateinit var adapter: CancelRideAdapter
    var bundle: Bundle? = null
    var reason_id: String? = null
    var booking_id = ""
    private val viewModels: CancelBookingViewModels by viewModels<CancelBookingViewModels>()
    var cancelRideReasonsList = mutableListOf<CancelBookingReasons>()

    lateinit var binding: ActivityCancelRideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCancelRideBinding.inflate(layoutInflater)
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
            /// var bookingData: ArrayList<CreateBooking>? = null
            // page = bundle?.getString("promo")
            var bookingData =
                bundle?.getParcelableArrayList<BookingDetail>(AppConstants.BOOKING_DETAILS)
            if (bookingData?.get(0)?.status.equals("1")) {
                binding.btBooked.text = getString(R.string.booked)
            }
            booking_id = bookingData?.get(0)?.id.toString()
            Glide.with(this@CancelRideActivity)
                .load(AppConstants.BASE_URL + "" + bookingData?.get(0)?.vehiclecategories?.image)
                .placeholder(R.drawable.mask)
                .into(binding.ivBus)
            binding.tvbusNam.text =
                bookingData?.get(0)?.vehiclecategories?.name
            binding.tvBookingId.text =
                getString(R.string.booking_id_sag12345) + " " + bookingData?.get(0)?.bookingId
            binding.tvPickupTime.text =
                bookingData!![0].triplocations?.get(0)?.startTime + " " + bookingData!![0].triplocations?.get(
                    0
                )?.startTimeUnit
            binding.tvPickUpDate.text = bookingData!![0].triplocations?.get(0)?.startDate
            binding.tvCurrentLocation.text =
                bookingData!![0].triplocations?.get(0)?.pickupLocationTitle
            binding.tvDestination.text = bookingData!![0].triplocations?.get(0)?.dropLocationTitle
            binding.tvPickUpAddress.text =
                bookingData!![0].triplocations?.get(0)?.pickupLocation
            binding.tvDropAddress.text = bookingData!![0].triplocations?.get(0)?.dropLocation
        }
        cancelRideHeader.headerTitle.text = getString(R.string.cancel_ride)

        rvCancelRide.layoutManager = LinearLayoutManager(this@CancelRideActivity)
        adapter = CancelRideAdapter(this@CancelRideActivity, cancelRideReasonsList,
            object : CancelRideAdapter.onItemClickListenre {
                override fun onClick(id: Int?) {
                    reason_id = id.toString()
                    btCancelRide.setBackgroundResource(R.drawable.solid_red)
                    btCancelRide.isEnabled = true
                    btCancelRide.isClickable = true
                }

            })
        rvCancelRide.adapter = adapter

        btCancelRide.setOnClickListener {
            // launchActivity(HomeActivity::class.java, removeAllPrevActivities = true)
            cancelBookingApi()
        }
        cancelRideHeader.ivBack.setOnClickListener {
            finish()
        }

        cancelBookingReasonsApi()
        apiObserver()
    }

    fun cancelBookingReasonsApi() {
        showDialog()
        viewModels.cancelBookingReasonsApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),

            )
    }

    fun cancelBookingApi() {
        if (reason_id == null) {
            showToast(getString(R.string.please_select_cancellation_reason))
        } else {
            showDialog()
            viewModels.cancelBookingApi(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                booking_id,
                SharedPref.readString(SharedPref.USER_ID),
                reason_id.toString()
            )
        }
    }

    fun apiObserver() {
        viewModels.cancelBookingReasonsResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CancelBookingReasonsResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                cancelRideReasonsList.clear()
                                data.data?.let { it1 -> cancelRideReasonsList.addAll(it1) }
                                adapter.notifyDataSetChanged()
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
                        }
                        else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        }
                        else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }
                }
            }
        })
        viewModels.cancelBookingResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CreateBookingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
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

                        }
                        else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }
                }
            }
        })
    }
}