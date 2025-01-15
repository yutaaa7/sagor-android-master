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
import com.sagarclientapp.databinding.FragmentHistoryBinding
import com.sagarclientapp.model.DataItem
import com.sagarclientapp.model.HistoryResponse
import com.sagarclientapp.ui.home.adapters.HistoryAdapter
import com.sagarclientapp.ui.home.viewModels.TripsViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment() {

    lateinit var binding: FragmentHistoryBinding
    lateinit var adapter: HistoryAdapter
    var historyList = mutableListOf<DataItem>()
    private val viewModels: TripsViewModel by viewModels<TripsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUis()
    }

    private fun initUis() = binding.apply {
        rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        adapter = HistoryAdapter(requireActivity(), historyList)
        rvHistory.adapter = adapter


        bookingHistoryApi()
        apiObserver()
    }

    fun bookingHistoryApi() {
        showDialog()
        viewModels.historyApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            "4"
        )
    }

    fun apiObserver() {
        viewModels.historyResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is HistoryResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                historyList.clear()
                                if (data.data.isNullOrEmpty()) {
                                    binding.tvNoData.visibility = View.VISIBLE
                                } else {
                                    historyList.addAll(data.data)
                                    binding.tvNoData.visibility = View.GONE
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
                        }
                        else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        }else {
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