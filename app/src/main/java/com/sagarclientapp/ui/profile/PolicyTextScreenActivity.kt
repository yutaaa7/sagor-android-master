package com.sagarclientapp.ui.profile

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityPolicyTextScreenBinding
import com.sagarclientapp.model.PagesResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.profile.viewmodels.PagesViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyTextScreenActivity() : BaseActivity(), Parcelable {
    var bundle: Bundle? = null
    var show: String? = null

    lateinit var binding: ActivityPolicyTextScreenBinding
    private val viewModels: PagesViewModel by viewModels<PagesViewModel>()


    constructor(parcel: Parcel) : this() {
        bundle = parcel.readBundle(Bundle::class.java.classLoader)
        show = parcel.readString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPolicyTextScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()

    }

    fun getPagesDataApi(page: String) {
        showDialog()
        viewModels.pagesData(
            page = page,
            /*"Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)*/
        )

    }

    private fun initUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {

            show = bundle?.getString("title")

        }
        if (show.equals("terms")) {
            headerTerms.headerTitle.text = getString(R.string.terms_condition)
            getPagesDataApi("3")
        }
        if (show.equals("refund")) {
            headerTerms.headerTitle.text = getString(R.string.refund_policy)
            getPagesDataApi("4")
        }
        if (show.equals("privacy")) {
            headerTerms.headerTitle.text =
                getString(R.string.privacy_policy)
            getPagesDataApi("2")
        }

        headerTerms.ivBack.setOnClickListener {
            finish()
        }

        apisObservers()


    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeBundle(bundle)
        parcel.writeString(show)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PolicyTextScreenActivity> {
        override fun createFromParcel(parcel: Parcel): PolicyTextScreenActivity {
            return PolicyTextScreenActivity(parcel)
        }

        override fun newArray(size: Int): Array<PolicyTextScreenActivity?> {
            return arrayOfNulls(size)
        }
    }

    fun apisObservers() {
        viewModels.pageResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is PagesResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

                                    binding.tvPagesText.text = data.data?.descriptionEn
                                }
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                    binding.tvPagesText.text = data.data?.descriptionAr

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

                        }  else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })
    }
}