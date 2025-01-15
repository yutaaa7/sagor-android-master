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
import com.sagarclientapp.databinding.ActivityManageYourChildBinding
import com.sagarclientapp.model.ChildListing
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.child.AddSchoolBusActivity
import com.sagarclientapp.ui.profile.viewmodels.ManageChildviewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageYourChildActivity : BaseActivity() {

    private lateinit var binding: ActivityManageYourChildBinding
    private val viewModels: ManageChildviewModel by viewModels<ManageChildviewModel>()
    private lateinit var adapter: ManageChildAdapter
    var childList = mutableListOf<ChildListing>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityManageYourChildBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUis()
    }

    private fun initUis() = binding.apply {
        manageChildHeader.headerTitle.text = getString(R.string.manage_your_child)
        rvMangeChild.layoutManager = LinearLayoutManager(this@ManageYourChildActivity)
        adapter = ManageChildAdapter(
            this@ManageYourChildActivity,
            childList,
            object : ManageChildAdapter.OnItemClickListener {
                override fun onItemClick(id: Int?) {
                    deleteChildApi(id)
                }

            })
        rvMangeChild.adapter = adapter

        manageChildHeader.ivBack.setOnClickListener {
            finish()
        }
        btAddChild.setOnClickListener {
            launchActivity(AddSchoolBusActivity::class.java, removeAllPrevActivities = false)

        }

        getChildListApi()
        apisObservers()
    }

    fun getChildListApi() {
        showDialog()
        viewModels.getChildrenList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID)
        )
    }

    fun deleteChildApi(id: Int?) {
        showDialog()
        viewModels.deleteChild(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            id.toString(),
            SharedPref.readString(SharedPref.USER_ID)
        )
    }

    fun apisObservers() {
        viewModels.childListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is ChildListingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                childList.clear()
                                if (data.data?.size!! > 0) {
                                    binding.tvNodata.visibility = View.GONE
                                    childList.addAll(data.data)
                                } else {
                                    binding.tvNodata.visibility = View.VISIBLE

                                }
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
        viewModels.deleteChildResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                getChildListApi()

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
}