package com.sagarclientapp.ui.profile

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivitySavedPlacesBinding
import com.sagarclientapp.model.SavedPlaces
import com.sagarclientapp.model.SavedPlacesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.profile.viewmodels.SavedPlacesViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedPlacesActivity : BaseActivity() {

    lateinit var binding: ActivitySavedPlacesBinding
    private val viewModels: SavedPlacesViewModel by viewModels<SavedPlacesViewModel>()
    lateinit var adapter: SavedPlacesAdapter
    var list = mutableListOf<SavedPlaces>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySavedPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
        getSavedPlacesListApi()
        apisObservers()
    }

    private fun initUis() = binding.apply {

        headerSavedPlaces.headerTitle.text = getString(R.string.saved_places)
        rvSavedPlaces.layoutManager = LinearLayoutManager(this@SavedPlacesActivity)
        adapter = SavedPlacesAdapter(this@SavedPlacesActivity, list,
            object : SavedPlacesAdapter.OnItemClickListener {
                override fun onItemClick(id: Int?) {
                    addToWishListApi(id)
                }

            })
        rvSavedPlaces.adapter = adapter

        headerSavedPlaces.ivBack.setOnClickListener {
            finish()
        }
    }

    fun getSavedPlacesListApi() {
        showDialog()
        viewModels.getSavedPlacesList(

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )

    }
    fun addToWishListApi(id: Int?) {

        viewModels.addToWishList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            id.toString(),
            "0"
        )
    }

    fun apisObservers() {
        viewModels.savedPlacesListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SavedPlacesResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                list.clear()
                                if (data.data?.isNotEmpty() == true) {
                                    binding.tvNoLocation.visibility = View.GONE
                                    data.data?.let { it1 -> list.addAll(it1) }
                                    adapter.notifyDataSetChanged()
                                } else {

                                    adapter.notifyDataSetChanged()
                                    binding.tvNoLocation.visibility = View.VISIBLE

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
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.addToWishListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                getSavedPlacesListApi()
                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    //hideDialog()

                }

                is Response.Loading -> {
                    try {
                        // showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    // hideDialog()
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

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
    }

}