package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.content.Intent
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
import com.sagarclientapp.databinding.ActivityCheckOutPaymentMethodsBinding
import com.sagarclientapp.model.PaymentrModesDataItem
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.ui.checkout.viewModels.PaymentMethodsViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckOutPaymentMethodsActivity : BaseActivity() {
    lateinit var binding: ActivityCheckOutPaymentMethodsBinding
    var payment_type: String = ""
    var paymentMethodName: String = ""
    var paymentMethodImage: String = ""
    private val paymentModesList = mutableListOf<PaymentrModesDataItem>()
    lateinit var adapter: PaymentModesAdapter
    var bundle: Bundle? = null
    private val viewModels: PaymentMethodsViewModel by viewModels<PaymentMethodsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckOutPaymentMethodsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    fun initUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {

            // page = bundle?.getString("promo")
            var price = bundle?.getString("price")
            payment_type = bundle?.getString("payment_type").toString()
            tvPrice.text = price



        }
//Option 1 for Knet
        //Option 2 for Credit card
        paymentMethodHeader.headerTitle.text = getString(R.string.payment_method)
        paymentMethodHeader.ivBack.setOnClickListener {
            finish()
        }
        rvPaymentModes.layoutManager = LinearLayoutManager(this@CheckOutPaymentMethodsActivity)
        adapter = PaymentModesAdapter(
            this@CheckOutPaymentMethodsActivity,
            paymentModesList,
            object : PaymentModesAdapter.onItemClickListenre {
                override fun onClick(id: String?, nameEn: String?, image: String?) {
                    payment_type = id.toString()
                    paymentMethodName = nameEn.toString()
                    paymentMethodImage = image.toString()
                    rlPayments.visibility= View.VISIBLE
                    tvPaymentText.text=getString(R.string.will_be_paid_with_apple_pay)+" "+nameEn
                }

            })
        rvPaymentModes.adapter=adapter
        /*if (!payment_type.isNullOrEmpty()) {
            // Pass the payment type to the adapter
            adapter.selectPaymentType(payment_type)
        }*/
        /*tvKnet.setOnClickListener {
            ivCreditRadioButton.isChecked = false
            ivKnetRadioButton.isChecked = true
            payment_type = "1"
            rlPayments.visibility= View.VISIBLE
            tvPaymentText.text=getString(R.string.will_be_paid_with_apple_pay)+" "+getString(R.string.k_net)
        }
        tvCreditDebit.setOnClickListener {
            ivCreditRadioButton.isChecked = true
            ivKnetRadioButton.isChecked = false
            payment_type = "2"
            rlPayments.visibility= View.VISIBLE

            tvPaymentText.text=getString(R.string.will_be_paid_with_apple_pay)+" "+getString(R.string.credit_debit_card)

        }
        ivCreditRadioButton.setOnClickListener {
            ivCreditRadioButton.isChecked = true
            ivKnetRadioButton.isChecked = false
            payment_type = "2"
            rlPayments.visibility= View.VISIBLE

            tvPaymentText.text=getString(R.string.will_be_paid_with_apple_pay)+" "+getString(R.string.credit_debit_card)

        }
        ivKnetRadioButton.setOnClickListener {
            rlPayments.visibility= View.VISIBLE

            tvPaymentText.text=getString(R.string.will_be_paid_with_apple_pay)+" "+getString(R.string.k_net)

            ivCreditRadioButton.isChecked = false
            ivKnetRadioButton.isChecked = true
            payment_type = "1"
        }*/

        btSavePaymentMethod.setOnClickListener {
            validation()
        }
        paymentModesApi()
        apiobserver()


    }

    fun validation() {
        if (payment_type == null) {
            showToast(getString(R.string.please_select_atleast_one_payment_method))
        } else {
            val resultIntent = Intent()
            resultIntent.putExtra("payment_type", payment_type)
            resultIntent.putExtra("payment_method_name", paymentMethodName)
            resultIntent.putExtra("payment_type_image", paymentMethodImage)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    fun paymentModesApi() {
        showDialog()
        viewModels.paymentModesApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            "android"
        )
    }

    fun apiobserver() {
        viewModels.paymentMethodsResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is PaymentrModesResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                paymentModesList.clear()


                                if (data.data?.size!! > 0) {
                                    paymentModesList.addAll(data.data)
                                }
                                /*if (paymentModesList.isNotEmpty()) {

                                    val firstItem = paymentModesList[0]
                                    adapter.itemClickListener.onClick(
                                        firstItem.payment_type,
                                        firstItem.nameEn,
                                        firstItem.image
                                    )
                                }*/
                                adapter.notifyDataSetChanged()

                                // Now that we have the list, select the matching payment type
                                if (!payment_type.isNullOrEmpty()) {
                                    adapter.selectPaymentType(payment_type)
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