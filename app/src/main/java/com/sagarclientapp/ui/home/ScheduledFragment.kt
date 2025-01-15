package com.sagarclientapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseFragment
import com.sagarclientapp.databinding.FragmentScheduledBinding
import com.sagarclientapp.model.DataItem
import com.sagarclientapp.model.HistoryResponse
import com.sagarclientapp.ui.checkout.MyRideActivity
import com.sagarclientapp.ui.home.adapters.ScheduledAdapter
import com.sagarclientapp.ui.home.viewModels.TripsViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduledFragment : BaseFragment() {

    lateinit var binding: FragmentScheduledBinding
    private val viewModels: TripsViewModel by viewModels<TripsViewModel>()
    var historyList= mutableListOf<DataItem>()
    lateinit var adapter: ScheduledAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduledBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUis()
    }

    private fun initUis() = binding.apply {
        rvScheduledTrips.layoutManager = LinearLayoutManager(requireActivity())
        adapter= ScheduledAdapter(requireActivity(),historyList,object: ScheduledAdapter.onItemClickListener{
            override fun onClick(id: Int?) {
                val bundle=Bundle()
                bundle.putString("where","schedule")
                bundle.putString("booking_id", id.toString())
                requireActivity().launchActivity(
                    MyRideActivity::class.java,
                    removeAllPrevActivities = false,
                    bundle = bundle)

            }

        })
        rvScheduledTrips.adapter = adapter


        apiObserver()
    }

    override fun onResume() {
        super.onResume()
        bookingHistoryApi()
    }


    fun bookingHistoryApi() {
        showDialog()
        viewModels.scheduledTripList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID)
        )
    }

    fun apiObserver() {
        viewModels.historyResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is HistoryResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                //showToast("Size is "+data.data?.size)
                                historyList.clear()
                                if (data.data.isNullOrEmpty())
                                {
                                   // showToast(" if Size is "+data.data?.size)

                                    binding.tvNoData.visibility=View.VISIBLE
                                }
                                else{
                                    //showToast("else Size is "+data.data?.size)

                                    binding.tvNoData.visibility=View.GONE
                                    data.data?.let { it1 -> historyList.addAll(it1) }
                                    adapter.notifyDataSetChanged()
                                }

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
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }


                }
            }
        })
    }
    fun getFirstErrorMessageInside(errors: String): String {
        return if (errors.contains(",")) {
            // If there is a comma, get the first part before the comma
            errors.substringBefore(",")
        } else {
            // If no comma, return the whole error string
            errors
        }
    }


}