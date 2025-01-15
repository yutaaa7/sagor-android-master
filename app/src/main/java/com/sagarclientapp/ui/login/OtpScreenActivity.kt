package com.sagarclientapp.ui.login

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityOtpScreenBinding
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.model.VerifyOtpResponse
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.login.viewmodels.OtpViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpScreenActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityOtpScreenBinding
    var bundle: Bundle? = null
    var where: String? = null
    var number: String? = null
    var otp: String? = null
    var is_already_register: String? = null
    var countryCode: String? = null
    private val viewModels: OtpViewModel by viewModels<OtpViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpScreenBinding.inflate(layoutInflater)
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
            number = bundle?.getString("number")
            otp = bundle?.getString("otp")
            countryCode = bundle?.getString("countryCode")
            is_already_register = bundle?.getString("is_already_register")
            //showToast(getString(R.string.otp_is) + " " + otp)

        }

        if (where.equals("boarding")) {
            otpBackHeader.rlBackHeader.visibility = View.GONE
            otpHeader.llTop.visibility = View.VISIBLE
            btGoToNotification.visibility = View.VISIBLE
            btSaved.visibility = View.GONE

        }
        if (where.equals("profile")) {
            otpBackHeader.rlBackHeader.visibility = View.VISIBLE
            otpHeader.llTop.visibility = View.GONE
            btGoToNotification.visibility = View.GONE
            btSaved.visibility = View.VISIBLE

            /*btSaveProfile.visibility=View.VISIBLE
            btCreateProfile.visibility=View.GONE*/

            otpBackHeader.headerTitle.text = getString(R.string.enter_otp)
        }

        otpHeader.tvTitle.text = getString(R.string.otp_verification)
        otpHeader.tvSubTitle.text =
            getString(R.string.enter_the_4_digit_code_sent_to_you_at_9876_5432) + " " + number

        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        et1.addTextChangedListener(GenericTextWatcher(et1, et2))
        et2.addTextChangedListener(GenericTextWatcher(et2, et3))
        et3.addTextChangedListener(GenericTextWatcher(et3, et4))
        et4.addTextChangedListener(GenericTextWatcher(et4, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        et1.setOnKeyListener(GenericKeyListener(et1, null))
        et2.setOnKeyListener(GenericKeyListener(et2, et1))
        et3.setOnKeyListener(GenericKeyListener(et3, et2))
        et4.setOnKeyListener(GenericKeyListener(et4, et3))

        llResendCode.setOnClickListener(this@OtpScreenActivity)
        btGoToNotification.setOnClickListener(this@OtpScreenActivity)
        btResendOtp.setOnClickListener(this@OtpScreenActivity)
        btSaved.setOnClickListener(this@OtpScreenActivity)
        tvChangeMobileNo.setOnClickListener(this@OtpScreenActivity)
        otpBackHeader.ivBack.setOnClickListener(this@OtpScreenActivity)

        startCountdown()
    }

    private inner class GenericTextWatcher(
        private val currentView: View,
        private val nextView: EditText?
    ) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if (text.length == 1) {
                nextView?.requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private inner class GenericKeyListener(
        private val currentView: EditText,
        private val previousView: EditText?
    ) :
        View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            /* if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN && currentView.text.isEmpty()) {
                 previousView!!.text = null
                 previousView?.requestFocus()
                 return true
             }
             return false*/

            if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN && currentView?.text?.isEmpty() == true) {
                previousView?.let {
                    it.text = null
                    it.requestFocus()
                }
                return true
            }
            return false

            /* if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.editText1 && currentView.text.isEmpty()) {
                 //If current is empty then previous EditText's number will also be deleted
                 previousView!!.text = null
                 previousView.requestFocus()
                 return true
             }
             return false*/
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.llResendCode -> {
                binding.llResendCode.visibility = View.GONE
                binding.btResendOtp.visibility = View.VISIBLE
            }

            R.id.btResendOtp -> {
                resendOtpApi()
            }

            R.id.ivBack -> {
                finish()
            }

            R.id.tvChangeMobileNo -> {
                finish()
            }

            R.id.btGoToNotification -> {
                verifyOtpApi()
                /*if (where.equals("boarding")) {
                    launchActivity(
                        TurnOnNotificationActivity::class.java,
                        removeAllPrevActivities = false
                    )
                }
                if (where.equals("profile")) {
                    bundle?.putString("page", "3")
                    launchActivity(
                        HomeActivity::class.java,
                        removeAllPrevActivities = false, bundle = bundle
                    )
                }*/

            }

            R.id.btSaved -> {
                verifyOtpForEditMobileNo()


            }
        }
    }

    private fun startCountdown() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                binding.tvCountDown.text = String.format("%02d:%02d", minutes, seconds)
                // binding.tvCountDown.text = ""+(millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.tvCountDown.text = "00:00"

                binding.llResendCode.visibility = View.GONE
                binding.btResendOtp.visibility = View.VISIBLE
            }
        }.start()
    }

    fun resendOtpApi() {
        showDialog()
        viewModels.resendOtp(
            countryCode.toString(),
            number.toString(),
            SharedPref.readString(SharedPref.DEVICE_ID)
        )

    }

    fun languageUpdateApi(userId: String, auth: String) {
        showDialog()
        viewModels.languageUpdate(
            userId,
            SharedPref.readString(SharedPref.DEVICE_ID),
            SharedPref.readString(SharedPref.LANGUAGE_ID),
            auth

        )

    }

    fun verifyOtpApi() {
        var otpText =
            binding.et1.text.toString() + binding.et2.text.toString() + binding.et3.text.toString() + binding.et4.text.toString()
        if (TextUtils.isEmpty(binding.et1.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))
        } else if (TextUtils.isEmpty(binding.et2.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))

        } else if (TextUtils.isEmpty(binding.et3.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))

        } else if (TextUtils.isEmpty(binding.et4.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))

        } else {
            showDialog()
            viewModels.verifyOtp(
                countryCode.toString(),
                number.toString(),
                otpText,
                AppConstants.DEVICE_TYPE,
                SharedPref.readString(SharedPref.DEVICE_ID)
            )
        }

    }

    fun verifyOtpForEditMobileNo() {
        var otpText =
            binding.et1.text.toString() + binding.et2.text.toString() + binding.et3.text.toString() + binding.et4.text.toString()
        if (TextUtils.isEmpty(binding.et1.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))
        } else if (TextUtils.isEmpty(binding.et2.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))

        } else if (TextUtils.isEmpty(binding.et3.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))

        } else if (TextUtils.isEmpty(binding.et4.text.toString())) {
            showToast(getString(R.string.please_enter_the_valid_otp))

        } else {
            showDialog()
            viewModels.verifyOtpForEditPhoneNo(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                countryCode.toString(),
                number.toString(),
                otpText,
                AppConstants.DEVICE_TYPE,
                SharedPref.readString(SharedPref.DEVICE_ID),
                ""
            )
        }
    }

    fun apisObservers() {
        viewModels.loginResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LoginResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                               // showToast(getString(R.string.otp_is) + " " + data.otp)
                                binding.llResendCode.visibility = View.VISIBLE
                                binding.btResendOtp.visibility = View.GONE
                                startCountdown()
                                //otp = data.otp.toString()
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
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.verifyOtpResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is VerifyOtpResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                SharedPref.writeString(
                                    SharedPref.ACCESS_TOKEN,
                                    data.data?.token.toString()
                                )

                                SharedPref.writeString(
                                    SharedPref.USER_ID,
                                    data.data?.id.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.MOBILE,
                                    data.data?.mobile.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.COUNTRY_CODE,
                                    data.data?.countryCode.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.USERNAME,
                                    data.data?.name.toString()
                                )
                                SharedPref.writeBoolean(SharedPref.IS_LOGIN, true)
                                Log.d("TOKEN", data.data?.token.toString())

                                if (where.equals("profile")) {
                                    showToast(getString(R.string.phone_number_updated_successfully))
                                    val bundle = Bundle()
                                    bundle?.putString("page", "3")
                                    launchActivity(
                                        HomeActivity::class.java,
                                        removeAllPrevActivities = false, bundle = bundle
                                    )


                                } else {
                                    //showToast(data.message)
                                    showToast(getString(R.string.you_have_logged_in_successfully))
                                    languageUpdateApi(
                                        data.data?.id.toString(),
                                        "Bearer " + data.data?.token.toString()

                                    )

                                }
                                /*SharedPref.writeString(
                                    SharedPref.ACCESS_TOKEN,
                                    data.data?.token.toString()
                                )

                                SharedPref.writeString(
                                    SharedPref.USER_ID,
                                    data.data?.id.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.MOBILE,
                                    data.data?.mobile.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.COUNTRY_CODE,
                                    data.data?.countryCode.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.USERNAME,
                                    data.data?.name.toString()
                                )
                                SharedPref.writeBoolean(SharedPref.IS_LOGIN, true)
                                Log.d("TOKEN", data.data?.token.toString())
*/

                                /* if (where.equals("boarding")) {
                                     launchActivity(
                                         TurnOnNotificationActivity::class.java,
                                         removeAllPrevActivities = false
                                     )
                                 }
                                 if (where.equals("profile")) {
                                     bundle?.putString("page", "3")
                                     launchActivity(
                                         HomeActivity::class.java,
                                         removeAllPrevActivities = false, bundle = bundle
                                     )
                                 }*/

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
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.verifyOtpForEditPhoneResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is VerifyOtpResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                showToast(getString(R.string.phone_no_updated_successfully))
                                SharedPref.writeString(
                                    SharedPref.ACCESS_TOKEN,
                                    data.data?.token.toString()
                                )
                                val bundle = Bundle()
                                bundle?.putString("page", "3")
                                launchActivity(
                                    HomeActivity::class.java,
                                    removeAllPrevActivities = false, bundle = bundle
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
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.languageUpdateResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                if (where.equals("boarding")) {
                                    //  showToast(is_already_register)
                                    if (is_already_register.equals("true")) {
                                        bundle?.putString("page", "1")
                                        launchActivity(
                                            HomeActivity::class.java,
                                            removeAllPrevActivities = false, bundle = bundle
                                        )
                                    } else {
                                        launchActivity(
                                            TurnOnNotificationActivity::class.java,
                                            removeAllPrevActivities = false
                                        )
                                    }

                                }
                                if (where.equals("profile")) {
                                    bundle?.putString("page", "3")
                                    launchActivity(
                                        HomeActivity::class.java,
                                        removeAllPrevActivities = false, bundle = bundle
                                    )
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
                    if (it.errorBody?.status == 422) {
                        showToast(getString(R.string.the_mobile_has_already_been_taken))
                    } else if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
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
