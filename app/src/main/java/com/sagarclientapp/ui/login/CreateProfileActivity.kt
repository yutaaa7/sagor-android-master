package com.sagarclientapp.ui.login

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityCreateProfileBinding
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.login.viewmodels.CreateProfileViewModel
import com.sagarclientapp.ui.login.viewmodels.LoginViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProfileActivity : BaseActivity() {
    lateinit var binding: ActivityCreateProfileBinding
    var bundle: Bundle? = null
    var where: String? = null
    var gender: String = "1"
    private val viewModels: CreateProfileViewModel by viewModels<CreateProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        iniUis()
        apisObservers()
    }

    private fun iniUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {

            where = bundle?.getString("where")

        }

        if (where.equals("location")) {
            profileBackHeader.rlBackHeader.visibility = View.GONE
            profileHeader.llTop.visibility = View.VISIBLE
            btSaveProfile.visibility = View.GONE
            btCreateProfile.visibility = View.VISIBLE

        }
        if (where.equals("profile")) {
            profileBackHeader.rlBackHeader.visibility = View.VISIBLE
            profileHeader.llTop.visibility = View.GONE
            btSaveProfile.visibility = View.VISIBLE
            btCreateProfile.visibility = View.GONE

            profileBackHeader.headerTitle.text = getString(R.string.edit_profile)
            getProfile()
        }

        /*  rb1.setButtonDrawable(R.drawable.checkbox_selector)
          val paddingInPixels = resources.getDimensionPixelSize(R.dimen._10dp)
          rb1.setCompoundDrawablePadding(paddingInPixels)
          rb2.setButtonDrawable(R.drawable.checkbox_selector)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              rb1.setButtonTintList(null);  // Remove any tint that might be applied
              rb2.setButtonTintList(null);  // Remove any tint that might be applied
          }*/

        profileHeader.tvTitle.text = getString(R.string.create_your_profile)
        profileHeader.tvSubTitle.text =
            getString(R.string.to_serve_you_better_we_need_few_of_your_basic_information)

        tvFullName.setOnClickListener {
            tvFullName.visibility = View.GONE
            fullNameLable.visibility = View.VISIBLE
            etFullName.visibility = View.VISIBLE
            etFullName.requestFocus()
        }
        tvEmail.setOnClickListener {
            tvEmail.visibility = View.GONE
            emailLable.visibility = View.VISIBLE
            etEmail.visibility = View.VISIBLE
            etEmail.requestFocus()
        }
        btCreateProfile.setOnClickListener {
            validations()

        }
        profileBackHeader.ivBack.setOnClickListener {
            finish()
        }
        btSaveProfile.setOnClickListener {
            validations()
        }

        rb1.setOnClickListener {
            //1: Male, 2: Female, 3: Other
            gender = "1"
            rb1.setImageResource(R.drawable.checked_checkbox)
            rb2.setImageResource(R.drawable.default_checkbox)
        }

        rb2.setOnClickListener {
            gender = "2"
            rb1.setImageResource(R.drawable.default_checkbox)
            rb2.setImageResource(R.drawable.checked_checkbox)
        }
    }

    fun validations() = binding.apply {
        if (TextUtils.isEmpty(etFullName.text.toString())) {
            showToast(getString(R.string.please_enter_full_name))
        }
        /*else if (etEmail.text.toString().isNotEmpty() && !Util.isValidEmail(etEmail.text.toString())) {
            showToast(getString(R.string.please_enter_valid_email_address))
        }*/
        else if (TextUtils.isEmpty(gender)) {
            showToast(getString(R.string.please_select_your_gender))
        } else {
            //launchActivity(HomeActivity::class.java, removeAllPrevActivities = true)
            updateProfile()
        }
    }

    fun updateProfile() {
        showDialog()
        viewModels.updateProfile(
            user_id = SharedPref.readString(SharedPref.USER_ID),
            name = binding.etFullName.text.toString(),
            email = binding.etEmail.text.toString(),
            gender = gender,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun getProfile() {
        showDialog()
        binding.tvFullName.visibility = View.GONE
        binding.fullNameLable.visibility = View.VISIBLE
        binding.etFullName.visibility = View.VISIBLE
        binding.tvEmail.visibility = View.GONE
        binding.emailLable.visibility = View.VISIBLE
        binding.etEmail.visibility = View.VISIBLE
        viewModels.getProfile(
            user_id = SharedPref.readString(SharedPref.USER_ID),
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun apisObservers() {
        viewModels.updateProfileResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (where.equals("profile")) {
                                    var bundle= Bundle()
                                    bundle?.putString("page", "3")
                                    launchActivity(
                                        HomeActivity::class.java,
                                        removeAllPrevActivities = false, bundle = bundle
                                    )
                                    showToast(data.message)
                                }else{

                                    showToast(getString(R.string.profile_created_successfully))
                                    launchActivity(
                                        HomeActivity::class.java,
                                        removeAllPrevActivities = true
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
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    }
                    else if (it.errorBody?.status == 422)
                    {
                        showToast(getString(R.string.please_enter_valid_email_address))
                    }
                    else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast("Inside if "+it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast("it.msg "+it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        }  else {
                            showToast(getString(R.string.please_enter_valid_email_address))

                           // showToast("it.errorBody?.message "+it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.getProfileResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GetProfileResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (data?.data?.get(0)?.name != null) binding.etFullName.setText(
                                    data?.data?.get(0)?.name.toString()
                                )
                                SharedPref.writeString(
                                    SharedPref.USERNAME,
                                    data.data?.get(0)?.name.toString()
                                )


                                if (data?.data?.get(0)?.email != null) binding.etEmail.setText(
                                    data?.data?.get(
                                        0
                                    )?.email.toString()
                                )
                                if (data?.data?.get(0)?.gender != null) {
                                    if (data?.data?.get(0)?.gender.equals("1")) {
                                        gender = "1"
                                        binding.rb1.setImageResource(R.drawable.checked_checkbox)
                                        binding.rb2.setImageResource(R.drawable.default_checkbox)
                                    }
                                    if (data?.data?.get(0)?.gender.equals("2")) {
                                        gender = "2"
                                        binding.rb1.setImageResource(R.drawable.default_checkbox)
                                        binding.rb2.setImageResource(R.drawable.checked_checkbox)
                                    }
                                } else {
                                    gender = ""
                                    binding.rb1.setImageResource(R.drawable.default_checkbox)
                                    binding.rb2.setImageResource(R.drawable.default_checkbox)
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