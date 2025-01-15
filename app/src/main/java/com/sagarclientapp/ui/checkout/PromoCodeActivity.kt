package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.sagarclientapp.databinding.ActivityPromoCodeBinding
import com.sagarclientapp.model.BusCategoryResponse
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.ui.checkout.viewModels.PromoCodeViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SelectRideWithTypeViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoCodeActivity : BaseActivity() {

    lateinit var binding: ActivityPromoCodeBinding
    var bundle: Bundle? = null
    var booking_id: String? = null
    var price: String? = null


    private val viewModels: PromoCodeViewModel by viewModels<PromoCodeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPromoCodeBinding.inflate(layoutInflater)
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

            // page = bundle?.getString("promo")
            price = bundle?.getString("price")
            booking_id = bundle?.getString("booking_id")


        }

        /* btActivateCode.isEnabled = false
         btActivateCode.isClickable = false*/

        headerPromoCode.headerTitle.text = getString(R.string.add_promo)

        tvPromoCode.setOnClickListener {
            tvPromoCode.visibility = View.GONE
            etPromoCode.visibility = View.VISIBLE
            promoCodeLable.visibility = View.VISIBLE
        }

        btActivateCode.setOnClickListener {
            /* val bundle=Bundle()
             bundle.putString("promo",etPromoCode.text.toString())
             launchActivity(CheckoutActivity::class.java, removeAllPrevActivities = false,
                 bundle = bundle)*/


            applyPromoCodeApi()


        }

        etPromoCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                /*if (p0?.length!! > 0) {
                    btActivateCode.isEnabled = true
                    btActivateCode.isClickable = true
                    btActivateCode.setBackgroundResource(R.drawable.solid_theme_blue)
                }
                else{
                    btActivateCode.isEnabled = false
                    btActivateCode.isClickable = false
                    btActivateCode.setBackgroundResource(R.drawable.solid_gray)

                }*/
            }

            override fun afterTextChanged(p0: Editable?) {
                btActivateCode.setBackgroundResource(R.drawable.solid_theme_blue)
                btActivateCode.isEnabled = true
                btActivateCode.isClickable = true

            }

        })
        headerPromoCode.ivBack.setOnClickListener {
            finish()
        }

        apiObserver()
    }

    fun applyPromoCodeApi() {
        showDialog()
        viewModels.applyCouponApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            booking_id.toString(), binding.etPromoCode.text.toString(), price.toString()
        )
    }

    fun apiObserver() {
        viewModels.applyCouponResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CreateBookingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                /* val resultIntent = Intent()
                                 resultIntent.putExtra("promo", etPromoCode.text.toString())
                                 setResult(Activity.RESULT_OK, resultIntent)
                                 finish()*/
                                val resultIntent = Intent()
                                resultIntent.putExtra("promo", binding.etPromoCode.text.toString())
                                resultIntent.putExtra("price_after_discount",data.data?.get(0)?.price+" " + data.data?.get(0)?.priceUnit )
                                resultIntent.putExtra("price_after_discount_without_unit",data.data?.get(0)?.price )
                                resultIntent.putExtra(
                                    "discount_price",
                                    data.data?.get(0)?.discountPrice + " " + data.data?.get(0)?.priceUnit
                                )
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
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
    }

}