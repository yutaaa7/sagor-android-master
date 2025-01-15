package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityCheckoutBinding
import com.sagarclientapp.model.ConfirmBookingResponse
import com.sagarclientapp.model.CreateBooking
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.MakePaymentResponse
import com.sagarclientapp.model.PaymentSuccessResponse
import com.sagarclientapp.model.PaymentrModesResponse
import com.sagarclientapp.ui.checkout.viewModels.CheckoutViewModel
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.ArrayList

@AndroidEntryPoint
class CheckoutActivity : BaseActivity() {
    var bundle: Bundle? = null
    var promo: String? = null
    var payment_type: String? = null
    var booking_id: String = ""
    var payment_method_name: String = ""
    var payment_type_image: String = ""
    private val PAYMENT_REQUEST_CODE = 1001
    var price: String = ""
    var price_unit: String = ""
    var price_after_discount: String? = null
    var price_after_discount_without_unit: String? = null

    var bookingData: ArrayList<CreateBooking>? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentRequestCode: Int = 0
    private val viewModels: CheckoutViewModel by viewModels<CheckoutViewModel>()

    companion object {
        const val REQUEST_CODE_FIRST = 1
        const val REQUEST_CODE_SECOND = 2
        const val REQUEST_CODE_PAYMENT = 3
    }

    lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    when (currentRequestCode) {
                        REQUEST_CODE_FIRST -> {
                            payment_type = data?.getStringExtra("payment_type")
                            payment_method_name =
                                data?.getStringExtra("payment_method_name").toString()
                            payment_type_image =
                                data?.getStringExtra("payment_type_image").toString()
                            binding.payment.tvPaymentName.text = payment_method_name
                            binding.payment.ivPaymentType.visibility = View.VISIBLE
                            binding.btPayment.text =
                                getString(R.string.payout_with) + " " + payment_method_name
                            Glide.with(this)
                                .load(AppConstants.BASE_URL_IMAGE + "" + payment_type_image)

                                .into(binding.payment.ivPaymentType)

                            /*  if (payment_type.equals("1")) {
                                  binding.payment.tvPaymentName.text = getString(R.string.k_net)
                                  binding.payment.ivPaymentType.visibility = View.VISIBLE
                                  binding.btPayment.text =
                                      getString(R.string.payout_with) + " " + getString(R.string.k_net)
                              }
                              if (payment_type.equals("2")) {
                                  binding.payment.tvPaymentName.text =
                                      getString(R.string.credit_debit_card)
                                  binding.payment.ivPaymentType.visibility = View.VISIBLE
                                  binding.btPayment.text =
                                      getString(R.string.payout_with) + " " + getString(R.string.credit_debit_card)
                              }*/
                            // showToast("payment_type is " + payment_type)
                            // Handle the data from the first screen
                        }

                        REQUEST_CODE_SECOND -> {
                            promo = data?.getStringExtra("promo")
                            var discount_price = data?.getStringExtra("discount_price")
                            price_after_discount = data?.getStringExtra("price_after_discount")
                            price_after_discount_without_unit =
                                data?.getStringExtra("price_after_discount_without_unit")
                            binding.tvPromoCode.text = promo

                            binding.tvRemoveCode.visibility = View.VISIBLE
                            binding.llDiscountView.visibility = View.VISIBLE
                            binding.tvDisCountedPrice.text = discount_price
                            binding.tvPriceAfterDiscount.text = price_after_discount
                            binding.tvTotalPrice.text = price_after_discount

                            // Handle the data from the second screen
                        }

                        REQUEST_CODE_PAYMENT -> {

                            val jsonString = data?.getStringExtra("paymentResponse")
                            if (!jsonString.isNullOrEmpty()) {
                                val paymentResponse =
                                    Gson().fromJson(jsonString, PaymentSuccessResponse::class.java)
                                Log.d("WebView", "Received JSON String: $jsonString")
                                Log.d("WebView", "Deserialized Payment Response: $paymentResponse")


                                confirmBookingApi(
                                    paymentResponse.data?.payzahRefrenceCode.toString(),
                                    paymentResponse.data?.trackId.toString(),
                                    paymentResponse.data?.knetPaymentId.toString(),
                                    paymentResponse.data?.transactionNumber.toString(),
                                    paymentResponse.data?.trackingNumber.toString(),
                                    paymentResponse.data?.paymentDate.toString(),
                                    paymentResponse.data?.paymentStatus.toString(),
                                    paymentResponse.data?.uDF1.toString(),
                                    paymentResponse.data?.uDF2.toString(),
                                    paymentResponse.data?.uDF3.toString(),
                                    paymentResponse.data?.uDF4.toString(),

                                    )
                            } else {
                                Log.e("WebView", "Received JSON String is null or empty")
                            }
                            // Inside the onCreate method of the next activity
                            /* val jsonString = intent.getStringExtra("paymentResponse")
                             val paymentResponse = Gson().fromJson(jsonString, PaymentSuccessResponse::class.java)
                             Log.d("WebView", "jsonString in checkout "+jsonString)
                             Log.d("WebView", "paymentResponse in checkout "+paymentResponse)*/
                            /* Log.d("WebView", "Payment Status: ${paymentResponse.status}")
                             Log.d("WebView", "Payment Message: ${paymentResponse.message}")
                             Log.d("WebView", "Transaction Number: ${paymentResponse.data?.transactionNumber}")
 */

                        }


                    }
                }
            }

        initUis()
    }

    private fun initUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {

            // page = bundle?.getString("promo")
            bookingData = bundle?.getParcelableArrayList<CreateBooking>(AppConstants.BOOKING_DATA)

        }
        /*  if (page?.isNotEmpty() == true) {
              tvPromoCode.text = page
          }*/

        if (bookingData != null) {
            booking_id = bookingData!![0].id.toString()
            tvCurrentLocationAddress.text = bookingData!![0].triplocations?.get(0)?.pickupLocation
            tvDestinationAddress.text = bookingData!![0].triplocations?.get(0)?.dropLocation
            tvCurrentLocationTitle.text =
                bookingData!![0].triplocations?.get(0)?.pickup_location_title
            tvDestination.text = bookingData!![0].triplocations?.get(0)?.drop_location_title
            if (bookingData!![0].tripMode.equals("0")) {
                llReturnView.visibility = View.GONE
                tvTripType.text = getString(R.string.one_way)
                tvDistance.text =
                    bookingData!![0].triplocations?.get(0)?.distance + " " + bookingData!![0].triplocations?.get(
                        0
                    )?.distanceUnit
            }
            if (bookingData!![0].tripMode.equals("1")) {
                llReturnView.visibility = View.VISIBLE
                tvTripType.text = getString(R.string.round_trip)
                val distanceString = bookingData!![0].triplocations?.get(0)?.distance
                if (!distanceString.isNullOrEmpty()) {
                    val distance = distanceString.toFloatOrNull() ?: 0f // Safely parse to Float
                    var roundTripDistance = distance * 2
                    // Use roundTripDistance as needed
                    println("Round trip distance: $roundTripDistance")
                    tvDistance.text =
                       roundTripDistance.toString() + " " + bookingData!![0].triplocations?.get(
                            0
                        )?.distanceUnit
                } else {
                    println("Distance is null or empty")
                }


                if (bookingData!![0].triplocations?.size == 2) {
                    tvReturnDate.text = bookingData!![0].triplocations?.get(1)?.startDate
                    tvReturnTime.text = bookingData!![0].triplocations?.get(1)?.startTime
                }

            }
            tvPickUpdate.text = bookingData!![0].triplocations?.get(0)?.startDate
            tvBusName.text = bookingData!![0].vehiclecategories?.name
            if (bookingData!![0].discountPrice == null) {
                price = bookingData!![0].total_price.toString()
                price_unit = bookingData!![0].priceUnit.toString()

                tvTotalPrice.text = bookingData!![0].total_price + " " + bookingData!![0].priceUnit
            } else {
                price = bookingData!![0].price.toString()
                price_unit = bookingData!![0].priceUnit.toString()

                tvTotalPrice.text = bookingData!![0].total_price + " " + bookingData!![0].price

            }
            /*tvDistance.text =
                bookingData!![0].triplocations?.get(0)?.distance + " " + bookingData!![0].triplocations?.get(
                    0
                )?.distanceUnit*/
            tvPickUpTime.text =
                bookingData!![0].triplocations?.get(0)?.startTime + " " + bookingData!![0].triplocations?.get(
                    0
                )?.startTimeUnit
        }

        checkoutHeader.headerTitle.text = getString(R.string.checkout)

        // rvTripDetails.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        // rvTripDetails.adapter = TripDetailCheckoutAdapter(this@CheckoutActivity)

        /*  rvPaymentMethods.layoutManager = LinearLayoutManager(this@CheckoutActivity)
          rvPaymentMethods.adapter = CheckoutpaymentsMothodsAdapter(this@CheckoutActivity)
  */

        cardPromo.setOnClickListener {
            currentRequestCode = REQUEST_CODE_SECOND
            val intent = Intent(this@CheckoutActivity, PromoCodeActivity::class.java)
            intent.putExtra("price", price)
            intent.putExtra("booking_id", booking_id)
            resultLauncher.launch(intent)
            //launchActivity(PromoCodeActivity::class.java, removeAllPrevActivities = false)

        }
        checkoutHeader.ivBack.setOnClickListener {
            finish()
        }

        payment.cardChild.setOnClickListener {
            currentRequestCode = REQUEST_CODE_FIRST
            val intent = Intent(this@CheckoutActivity, CheckOutPaymentMethodsActivity::class.java)
            intent.putExtra("price", tvTotalPrice.text.toString())
            intent.putExtra("payment_type", payment_type)
            resultLauncher.launch(intent)
        }

        btPayment.setOnClickListener {
            //confirmBookingApi()
            makePaymentApi()
            /* val bundle = Bundle()
             bundle.putString("where", "checkout")
             launchActivity(
                 MyRideActivity::class.java, removeAllPrevActivities = false,
                 bundle = bundle
             )*/
        }
        tvRemoveCode.setOnClickListener {
            removePromoCodeApi()
        }
        paymentModesApi()
        apiObserver()
    }

    fun removePromoCodeApi() {
        showDialog()
        viewModels.removeCouponApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            booking_id.toString(), promo.toString(), price.toString()
        )
    }

    fun makePaymentApi() {

        var amount = ""
        if (price_after_discount_without_unit != null) {
            amount = price_after_discount_without_unit.toString()
        } else {
            amount = price
        }
        // showToast(price_after_discount_without_unit)

        //confirmBookingApi()
        if (payment_type.isNullOrEmpty()) {
            showToast(getString(R.string.please_select_a_payment_method))
        } else {
            showDialog()
            viewModels.makePaymentApi(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                amount, SharedPref.readString(SharedPref.USER_ID),
                booking_id, "", "", "", payment_type.toString()
            )
        }
    }

    fun paymentModesApi() {
        showDialog()
        viewModels.paymentModesApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            "android"
        )
    }

    fun confirmBookingApi(
        payzahRefrenceCode: String,
        track_id: String,
        keyNetPaymentId: String,
        transaction_no: String,
        tracking_no: String,
        payment_date: String,
        payment_status: String,
        udf1: String,
        udf2: String,
        udf3: String,
        udf4: String
    ) {

        // required 0 = COD, 1 = KNET 2 for credit card
        showDialog()
        viewModels.confirmBookingApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID), booking_id, "1", payment_type.toString(),
            payzahRefrenceCode, track_id, keyNetPaymentId, transaction_no, tracking_no,
            payment_date, payment_status, udf1, udf2, udf3, udf4, promo.toString()
        )

    }

    fun apiObserver() {
        viewModels.paymentMethodsResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is PaymentrModesResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {


                                if (data.data?.size!! > 0) {
                                    binding.payment.ivPaymentType.visibility = View.VISIBLE
                                    Glide.with(this)
                                        .load(AppConstants.BASE_URL_IMAGE + "" + data.data.get(0).image)

                                        .into(binding.payment.ivPaymentType)
                                    payment_type = data.data.get(0).payment_type
                                    if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
                                        binding.payment.tvPaymentName.text = data.data.get(0).nameEn
                                        binding.btPayment.text =
                                            getString(R.string.payout_with) + " " + data.data.get(0).nameEn
                                    }
                                    if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                        binding.payment.tvPaymentName.text = data.data.get(0).nameAr
                                        binding.btPayment.text =
                                            getString(R.string.payout_with) + " " + data.data.get(0).nameAr
                                    }

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
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }
                }

                else -> {}
            }
        })

        viewModels.removeCouponResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CreateBookingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.llDiscountView.visibility = View.GONE
                                binding.tvPromoCode.text = ""
                                binding.tvPromoCode.hint = getString(R.string.promo)
                                binding.tvRemoveCode.visibility = View.GONE
                                price_after_discount = null
                                price_after_discount_without_unit = null
                                /*if (price_after_discount_without_unit != null) {
                                    amount = price_after_discount_without_unit.toString()
                                } else {
                                    amount = price
                                }*/
                                binding.tvTotalPrice.text=price+" "+price_unit
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
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }
                }
            }
        })
        viewModels.makePaymentResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is MakePaymentResponse -> {
                            if (data.status.equals("success")) {
                                //showToast(data.data?.data?.transitUrl)
                                currentRequestCode = REQUEST_CODE_PAYMENT
                                val intent =
                                    Intent(this@CheckoutActivity, WebViewActivity::class.java)
                                intent.putExtra("url", data.data?.data?.transitUrl)

                                resultLauncher.launch(intent)
                                // openDeepLink(data.data?.data?.transitUrl)

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
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }
                }
            }
        })

        viewModels.confirmBookingResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is ConfirmBookingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0"))
                                {
                                    Util.setLocale(this@CheckoutActivity, "en")

                                }
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1"))
                                {
                                    Util.setLocale(this@CheckoutActivity, "ar")
                                    // Util.changeLanguageAndCheck(this, "ar", R.string.start_booking)

                                }
                                if (data.data?.get(0)?.payment != null && data.data?.get(0)?.payment?.paymentStatus?.equals("CAPTURED") == true) {

                                    val bundle = Bundle()
                                    bundle.putString("where", "checkout")
                                    bundle.putString("booking_id", booking_id)
                                    launchActivity(
                                        MyRideActivity::class.java, removeAllPrevActivities = false,
                                        bundle = bundle
                                    )
                                }
                                else{
                                    showToast(getString(R.string.payment_has_been_cancelled_or_failed))
                                    launchActivity(
                                        HomeActivity::class.java, removeAllPrevActivities = false
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let {
            val uri = it.data
            uri?.let { data ->
                val paymentStatus = data.getQueryParameter("paymentStatus")
                handlePaymentResponse(paymentStatus)
            }
        }
    }

    private fun openDeepLink(transitUrl: String?) {
        // showToast(transitUrl)
        /*currentRequestCode = REQUEST_CODE_PAYMENT
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(transitUrl))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //startActivityForResult(browserIntent, PAYMENT_REQUEST_CODE)
        resultLauncher.launch(browserIntent)*/

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        showToast("onActivityResult")

        if (requestCode == PAYMENT_REQUEST_CODE) {
            // Check if result is OK (you can modify based on your requirements)
            if (resultCode == Activity.RESULT_OK) {
                showToast("Activity.RESULT_OK")

                // Process the payment result
                data?.data?.let { uri ->
                    val paymentStatus = uri.getQueryParameter("paymentStatus")
                    handlePaymentResponse(paymentStatus)
                } ?: run {
                    showToast("No payment status found.")
                }
            } else {
                showToast("Payment not completed.")
            }
        }
    }
*/
    private fun handlePaymentResponse(paymentStatus: String?) {
        when (paymentStatus) {
            "CAPTURED" -> {
                //handleSuccessfulPayment()
                // Payment successful, handle accordingly
                showToast(getString(R.string.payement_success))

            }

            "NOT CAPTURED", "CANCELED", "VOIDED", "DENIED BY RISK", "HOST TIMEOUT" -> {
                // Handle different failure states
            }

            else -> {
                showToast(getString(R.string.unknown_payment_status, paymentStatus))
            }
        }
    }

    private fun handleSuccessfulPayment(jsonObject: JSONObject) {
        // Extract data from the JSON object
        val dataObject = jsonObject.getJSONObject("data")
        val payzahReferenceCode = dataObject.getString("payzahRefrenceCode")
        val trackId = dataObject.getString("trackId")
        val knetPaymentId = dataObject.getString("knetPaymentId")
        val paymentId = dataObject.getString("paymentId")
        val transactionNumber = dataObject.getString("transactionNumber")
        val trackingNumber = dataObject.getString("trackingNumber")
        val paymentDate = dataObject.getString("paymentDate")

        // Handle the successful payment data (e.g., update UI, save data, etc.)
        showToast("Payment successful! Reference: $payzahReferenceCode")
        // You can also use the extracted data as needed
    }

    override fun onResume() {
        super.onResume()
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.ivForwardPromo.setImageResource(R.drawable.right)
            binding.payment.ivForward.setImageResource(R.drawable.right)

        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.ivForwardPromo.setImageResource(R.drawable.only_arrow_arabic)
            binding.payment.ivForward.setImageResource(R.drawable.only_arrow_arabic)
        }
    }
}