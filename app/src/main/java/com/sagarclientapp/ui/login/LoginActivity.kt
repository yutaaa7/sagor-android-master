package com.sagarclientapp.ui.login

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ActivityLoginBinding
import com.sagarclientapp.ui.login.adapters.CountryListAdapter
import com.sagarclientapp.ui.login.viewmodels.LoginViewModel
import com.sagarclientapp.utils.Util.launchActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.model.CountryList
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GetPhoneNumberResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.ui.profile.PolicyTextScreenActivity
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    var bundle: Bundle? = null
    var where: String? = null
    var token: String?=null
    var pre_country_code: String? = null
    var pre_phone_no: String? = null
    private val viewModels: LoginViewModel by viewModels<LoginViewModel>()
    var device_id: String = ""
    lateinit var countryListAdapter: CountryListAdapter
    var countryList = mutableListOf<CountryList>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        bundle = intent.extras

        if (bundle != null) {

            where = bundle?.getString("where")

        }

        if (where.equals("boarding")) {
            loginBackHeader.rlBackHeader.visibility = View.GONE
            rltTerms.visibility = View.VISIBLE
            loginHeader.llTop.visibility = View.VISIBLE
        }
        if (where.equals("profile")) {
            loginBackHeader.rlBackHeader.visibility = View.VISIBLE
            loginHeader.llTop.visibility = View.GONE
            rltTerms.visibility = View.GONE
            /*btSaveProfile.visibility=View.VISIBLE
            btCreateProfile.visibility=View.GONE*/

            loginBackHeader.headerTitle.text = getString(R.string.edit_mobile_number)

            getPhoneNumberAPi()
        }

        chkTermCondition.setButtonDrawable(R.drawable.checkbox_selector)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            chkTermCondition.setButtonTintList(null);  // Remove any tint that might be applied
        }

        loginHeader.tvTitle.text = getString(R.string.enter_your_mobile_number)
        loginHeader.tvSubTitle.text =
            getString(R.string.this_number_will_used_for_all_sms_related_communication_you_will_receive_an_sms_verification_otp)

        tvTermsAndConditions1.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "terms")
            launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )
        }
        tvTermsAndConditions.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "terms")
            launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )
        }

        tvMobile.setOnClickListener {
            tvMobile.visibility = View.GONE
            etMobileNumber.visibility = View.VISIBLE
            etMobileNumber.requestFocus()
            mobileLable.visibility = View.VISIBLE
        }
        ivCountryShow.setOnClickListener {
            showCountryDialog()
        }
        tvCountryCode.setOnClickListener {
            showCountryDialog()
        }
        ivCountryFlag.setOnClickListener {
            showCountryDialog()
        }
        llCountry.setOnClickListener {
            showCountryDialog()
        }
        btGetOtp.setOnClickListener {
            /*val bundle = Bundle()
            bundle.putString("where", where)
            launchActivity(
                OtpScreenActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )*/
            if (where.equals("boarding")) {
                validations()
            }
            if (where.equals("profile")) {
                validationsForGetOtp()
            }
        }
        loginBackHeader.ivBack.setOnClickListener {
            finish()
        }

        // Fetching Android ID and storing it into a constant
        device_id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token

            token = task.result
            Log.d("FCM", "Token: $token")
        }
        SharedPref.writeString(
            SharedPref.DEVICE_ID,
            device_id
        )

        countryListApi()
    }

    private fun showCountryDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_country_list)

        val recyclerView = dialog.findViewById(R.id.rvCountriesList) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(this)
        countryListAdapter = CountryListAdapter(
            this@LoginActivity,
            countryList,
            object : CountryListAdapter.BtnClickListener {
                override fun onClick(countryList: CountryList) {
                    binding.tvCountryCode.text = countryList.phoneCode
                    Glide.with(this@LoginActivity)
                        .load(countryList.flag).placeholder(R.drawable.flag)
                        .into(binding.ivCountryFlag);
                    dialog.dismiss()
                }

            })
        recyclerView.adapter = countryListAdapter


        val tvCancelCountryDialog = dialog.findViewById(R.id.tvCancelCountryDialog) as TextView
        tvCancelCountryDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun validations() {
        val phoneNumber = binding.etMobileNumber.text.toString().trim()
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.please_enter_the_mobile_number), Toast.LENGTH_SHORT
            ).show()
        } else if (phoneNumber.length < 8) {
            showToast(getString(R.string.phone_number_must_be_at_least_8_digits))
            // If the phone number is less than 8 digits

        } else if (phoneNumber.length > 11) {
            showToast(getString(R.string.phone_number_must_be_no_more_than_11_digits))
            // If the phone number exceeds 11 digits

        } else if (!binding.chkTermCondition.isChecked) {

            showToast(getString(R.string.please_verify_terms_and_conditions))
        } else {
            loginApi()
        }
    }

    fun validationsForGetOtp() {

        val phoneNumber = binding.etMobileNumber.text.toString().trim()
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.please_enter_the_mobile_number), Toast.LENGTH_SHORT
            ).show()
        } else if (phoneNumber.length < 8) {
            showToast(getString(R.string.phone_number_must_be_at_least_8_digits))
            // If the phone number is less than 8 digits

        } else if (phoneNumber.length > 11) {
            showToast(getString(R.string.phone_number_must_be_no_more_than_11_digits))
            // If the phone number exceeds 11 digits
        } else {
            editPhoneNumberAPi()
        }
    }


    fun loginApi() {
        showDialog()
        viewModels.login(
            country_code = binding.tvCountryCode.text.toString(),
            mobile = binding.etMobileNumber.text.toString(),
            type = "5",
            device_token = device_id
        )

    }

    fun countryListApi() {
        showDialog()
        viewModels.countryListApi()

    }

    fun getPhoneNumberAPi() {
        showDialog()
        viewModels.getPhoneNumber(

            SharedPref.readString(SharedPref.USER_ID),
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
        )

    }

    fun editPhoneNumberAPi() {
        showDialog()
        viewModels.editPhoneNumber(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            pre_country_code.toString(), pre_phone_no.toString(),
            "android", SharedPref.readString(SharedPref.DEVICE_ID),
            "5", "", binding.tvCountryCode.text.toString(), binding.etMobileNumber.text.toString()
        )

    }

    fun apisObservers() {
        viewModels.loginResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LoginResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                showToast(getString(R.string.otp_send))
                                val bundle = Bundle()
                                bundle.putString("where", where)
                                bundle.putString("otp", data.otp.toString())
                                bundle.putString("number", binding.etMobileNumber.text.toString())
                                bundle.putString("is_already_register", data.is_already_registered)
                                bundle.putString(
                                    "countryCode",
                                    binding.tvCountryCode.text.toString()
                                )
                                launchActivity(
                                    OtpScreenActivity::class.java, removeAllPrevActivities = false,
                                    bundle = bundle
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
        viewModels.editPhoneNumberResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LoginResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                showToast(getString(R.string.otp_send))
                                val bundle = Bundle()
                                bundle.putString("where", where)
                                bundle.putString("otp", data.otp.toString())
                                bundle.putString("number", binding.etMobileNumber.text.toString())
                                bundle.putString("is_already_register", data.is_already_registered)
                                bundle.putString(
                                    "countryCode",
                                    binding.tvCountryCode.text.toString()
                                )
                                launchActivity(
                                    OtpScreenActivity::class.java, removeAllPrevActivities = false,
                                    bundle = bundle
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
        viewModels.getPhoneNumberResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GetPhoneNumberResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.tvMobile.visibility = View.GONE
                                binding.etMobileNumber.visibility = View.VISIBLE
                                binding.etMobileNumber.requestFocus()
                                binding.mobileLable.visibility = View.VISIBLE

                                pre_country_code = data.data?.countryCode
                                pre_phone_no = data.data?.mobile
                                binding.etMobileNumber.setText(data.data?.mobile)
                                // Move the cursor to the end of the text
                                binding.etMobileNumber.text?.let { it1 ->
                                    binding.etMobileNumber.setSelection(
                                        it1.length
                                    )
                                }
                                binding.tvCountryCode.text = data.data?.countryCode
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
        viewModels.countryListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CountryListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                countryList.clear()
                                data.data?.let { it1 -> countryList.addAll(it1) }
                                if (countryList.size == 1) {
                                    binding.tvCountryCode.text = countryList[0].phoneCode
                                    Glide.with(this)
                                        .load(countryList[0].flag).placeholder(R.drawable.flag)
                                        .into(binding.ivCountryFlag);
                                } else {

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