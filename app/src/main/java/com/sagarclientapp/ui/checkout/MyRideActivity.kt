package com.sagarclientapp.ui.checkout

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityMyRideBinding
import com.sagarclientapp.model.BookingDetail
import com.sagarclientapp.model.BookingDetailResponse
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.ui.checkout.viewModels.MyRideViewModel
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.home.RateTripActivity
import com.sagarclientapp.ui.home.ReportIssueActivity
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.maps.android.PolyUtil

@AndroidEntryPoint
class MyRideActivity : BaseActivity(), OnMapReadyCallback {
    val kuwaitBounds = LatLngBounds(
        LatLng(28.5246, 46.5527), // Southwest corner
        LatLng(30.0935, 48.4326)  // Northeast corner
    )
    var driver_no: String? = null
    var country_code: String? = null
    private var polylineToPickup: Polyline? = null
    private var polylineToDrop: Polyline? = null
    lateinit var binding: ActivityMyRideBinding
    var bundle: Bundle? = null
    var where: String? = null
    var trip_mode: Int? = null
    var selectedDate: String? = null
    var round_pickup_date: String? = null
    var selectedTime: String = ""
    var roundTime: String = ""
    var is_Contunue_clicked = false
    var round_pickup_time: String? = null
    var round_pickup_time_unit: String? = null
    var booking_id: String? = null
    var time: String? = null
    private var lastKnownLocation: LatLng? = null
    var time_unit: String? = null
    var driverLatLng: LatLng? = null  // San Francisco
    var pickupLatLng: LatLng? = null  // San Francisco
    var dropLatLng: LatLng? = null  // San Francisco
    var nextLatLng: LatLng? = null  // San Francisco
    private lateinit var mMap: GoogleMap
    var isPathDrawn = false  // Track if the path is already drawn
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var userMarker: Marker
    private lateinit var driverMarker: Marker

    //private lateinit var polyline: Polyline
    private val driverPathPoints = mutableListOf<LatLng>()
    private lateinit var driverPolyline: Polyline
    private var currentMarker: Marker? = null
    private val client = OkHttpClient()
    private val viewModels: MyRideViewModel by viewModels<MyRideViewModel>()
    var bookingDetailsList: ArrayList<BookingDetail> = ArrayList<BookingDetail>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorForDriverOnWay: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetForRideIn: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetForRideC: BottomSheetBehavior<LinearLayout>

    // Boolean flags to track user interaction
    var isUserCollapsedDriverDataOnly = false
    var isUserCollapsedDriverOnWay = false
    var isUserCollapsedRideIn = false
    var isUserCollapsedRideC = false

    val updateInterval = 3000L  // 3 seconds


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyRideBinding.inflate(layoutInflater)

        setContentView(binding.root)

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
*/
        initUis()
        bookingDetailApi()
        //driverStatusApi()
        apiObserver()
    }

    private fun initUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {

            where = bundle?.getString("where")
            booking_id = bundle?.getString("booking_id")

        }

        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MyRideActivity)

        // Initialize the map fragment
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MyRideActivity)

        //  var bottomSheetForRideInProgress = findViewById<LinearLayout>(R.id.bottomSheetForRideInProgress)
        //  var bottomSheetForRideCompleted = findViewById<LinearLayout>(R.id.bottomSheetForRideCompleted)


        // headerMyRide.ivMenu.visibility = View.VISIBLE
        // headerMyRide.headerTitle.text = getString(R.string.my_ride)
        //val bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetRide)
        bottomSheetBehaviorForDriverOnWay = BottomSheetBehavior.from(bottomSheetForDriverOnWay)
        bottomSheetForRideIn = BottomSheetBehavior.from(bottomSheetForRideInProgress)
        bottomSheetForRideC = BottomSheetBehavior.from(bottomSheetForRideCompleted)


        if (where.equals("schedule")) {
            headerMyRide.ivMenu.visibility = View.VISIBLE
            headerMyRide.headerTitle.text = ""

            /*bottomSheetBehaviorForDriverOnWay.setHideable(false);
            bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideIn.setHideable(true);
            bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideC.setHideable(true);
            bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN*/

            // handler.post(runnable)

            // driverStatusApi()

            startTracking()


        }

        if (where.equals("checkout")) {
            headerMyRide.ivMenu.visibility = View.VISIBLE
            headerMyRide.headerTitle.text = getString(R.string.my_ride)
            bottomSheetBehavior.setHideable(false)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBehaviorForDriverOnWay.setHideable(true)
            bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideIn.setHideable(true)
            bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideC.setHideable(true)
            bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN

        }


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isUserCollapsedDriverDataOnly = true
                }
                /* BottomSheetBehavior.STATE_EXPANDED -> {
                     // When fully expanded, set a new peek height or keep it unchanged
                     bottomSheetBehavior.peekHeight = 300 // or any desired height
                 }
                 BottomSheetBehavior.STATE_COLLAPSED -> {
                     // When collapsed (slid to the bottom), set the peek height to minimum or desired value
                     bottomSheetBehavior.peekHeight = 100 // set the desired peek height when collapsed
                 }
                 BottomSheetBehavior.STATE_HIDDEN -> {
                     // Optional: if you want to hide the BottomSheet completely
                     bottomSheetBehavior.peekHeight = 0
                 }*/

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                bottomSheetBehavior.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })
        bottomSheetBehaviorForDriverOnWay.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isUserCollapsedDriverOnWay = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                bottomSheetBehaviorForDriverOnWay.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })
        bottomSheetForRideIn.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isUserCollapsedRideIn = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                bottomSheetForRideIn.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })
        bottomSheetForRideC.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isUserCollapsedRideC = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                bottomSheetForRideC.peekHeight =
                    screenHeight / 3 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })


        btBooked.setOnClickListener {
            /*viewConfirmed.visibility = View.VISIBLE
            tvRideConfirmed.visibility = View.VISIBLE
            llRequestSuccessful.visibility = View.GONE
            cardDriver.visibility = View.VISIBLE*/
        }
        headerMyRide.ivMenu.setOnClickListener {
            showScheduleCancelDialog()
        }
        headerMyRide.ivBack.setOnClickListener {
            finish()
        }
        btReportAnIssue.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("booking_id", booking_id)
            launchActivity(
                ReportIssueActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )
            /*bottomSheetForRideIn.setHideable(false);
            bottomSheetForRideIn.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetBehaviorForDriverOnWay.setHideable(true);
            bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideC.setHideable(true);
            bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN*/
        }

        btReportRideInProgress.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("booking_id", booking_id)
            launchActivity(
                ReportIssueActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )
            /*  bottomSheetForRideIn.setHideable(true);
              bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

              bottomSheetBehavior.setHideable(true);
              bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

              bottomSheetBehaviorForDriverOnWay.setHideable(true);
              bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_HIDDEN

              bottomSheetForRideC.setHideable(false);
              bottomSheetForRideC.state = BottomSheetBehavior.STATE_EXPANDED*/
        }
        btRateTrip.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("booking_id", booking_id)
            launchActivity(
                RateTripActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        btReportCompleted.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("booking_id", booking_id)
            launchActivity(
                ReportIssueActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )
        }
        btGoToHome.setOnClickListener {
            launchActivity(HomeActivity::class.java, removeAllPrevActivities = true)
        }
        ivCallDriver.setOnClickListener {
            if (driver_no?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$driver_no")
                startActivity(dialIntent)
            }
        }
        tvCallDriver.setOnClickListener {
            if (driver_no?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$driver_no")
                startActivity(dialIntent)
            }
        }
        ivCall.setOnClickListener {
            if (driver_no?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$driver_no")
                startActivity(dialIntent)
            }
        }
        tvDriverCall.setOnClickListener {
            if (driver_no?.isNotEmpty() == true) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$driver_no")
                startActivity(dialIntent)
            }
        }
        ivWhatsapp.setOnClickListener {
            openWhatsAppChat(country_code + driver_no)
        }
        tvWhatsapphere.setOnClickListener {
            openWhatsAppChat(country_code + driver_no)

        }
        whatsapp.setOnClickListener {
            openWhatsAppChat(country_code + driver_no)

        }
        tvDriverChat.setOnClickListener {
            openWhatsAppChat(country_code + driver_no)

        }

    }

    fun openWhatsAppChat(toNumber: String) {
        // Remove '+' sign from the phone number if present
        val formattedNumber = toNumber.replace("+", "")

        // WhatsApp API URL with the formatted phone number
        val url = "https://api.whatsapp.com/send?phone=$formattedNumber"

        // Check if WhatsApp is installed
        val isWhatsAppInstalled: Boolean = try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (isWhatsAppInstalled) {
            // WhatsApp is installed, so launch it
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
        } else {
            // WhatsApp is not installed, show a toast
            Toast.makeText(
                this,
                getString(R.string.whatsapp_is_not_installed_on_your_device), Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun showScheduleCancelDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cancel_reschedule)
        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.TOP) // Set the dialog to appear at the top
            //window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Optional: Remove any default padding or margin
            window.decorView.setPadding(0, 0, 0, 0)

        }

        val ivClose: ImageView = dialog.findViewById(R.id.ivCancelDialog)
        val btCancel: Button = dialog.findViewById(R.id.btCancel)
        val btReschedule: Button = dialog.findViewById(R.id.btReschedule)
        /*val btSubmitDate: Button = dialog.findViewById(R.id.btSubmitDate)

        calenderView = dialog.findViewById(R.id.calenderView)*/
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        btReschedule.setOnClickListener {
            dialog.dismiss()
            showCalenderDialog()
        }
        btCancel.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                AppConstants.BOOKING_DETAILS,
                bookingDetailsList
            )
            launchActivity(
                CancelRideActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        /* btSubmitDate.setOnClickListener {
             dialog.dismiss()
             binding.btSelectTimeDate.visibility = View.GONE
             binding.cardPickupData.visibility = View.VISIBLE
             binding.tvChangeDate.visibility = View.VISIBLE
         }*/

        dialog.show()
    }

    fun bookingDetailApi() {
        showDialog()
        viewModels.bookingDetailApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString()
        )
    }

    fun driverStatusApi() {
        viewModels.driverStatus(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString()
        )
    }

    fun rescheduledBookingForOneWayApi() {
        showDialog()
        viewModels.rescheduledBokkingOneWayApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            booking_id.toString(),
            selectedDate.toString(),
            time.toString(), time_unit.toString(), "0"
        )
    }

    fun apiObserver() {
        viewModels.bookingDetailResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is BookingDetailResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                var mode = data.data?.get(0)?.tripMode
                                trip_mode = mode?.toIntOrNull()
                                bookingDetailsList.clear()
                                data.data?.let { it1 -> bookingDetailsList.addAll(it1) }
                                if (data.data?.get(0)?.status.equals("1")) {
                                    binding.btBooked.text = getString(R.string.booked)
                                }
                                binding.tvApproxTime.text = data.data?.get(0)?.approx_total_time
                                Glide.with(this)
                                    .load(AppConstants.BASE_URL_IMAGE + "" + data.data?.get(0)?.vehiclecategories?.image)
                                    .into(binding.ivBus)
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
                                    binding.tvVehicleName.text =
                                        data.data?.get(0)?.vehiclecategories?.name
                                }
                                if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                    binding.tvVehicleName.text =
                                        data.data?.get(0)?.vehiclecategories?.nameAr
                                }

                                binding.tvbusSeats.text =
                                    data.data?.get(0)?.vehiclecategories?.capacity.toString()


                                if (data.data?.get(0)?.tripMode?.equals("0") == true) {
                                    binding.tvTripMode.text = getString(R.string.one_way)

                                    binding.tvDistance.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.distance + " " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.distanceUnit
                                    binding.tvBookingId.text =
                                        getString(R.string.booking_id_sag12345) + " " + data.data?.get(
                                            0
                                        )?.bookingId
                                    binding.tvTime.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.startTimeUnit
                                    binding.tvstartDate.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.startDate
                                    binding.tvCurrentLocation.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                    binding.tvPickupLocationAddress.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocation
                                    binding.tvDestination.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle

                                    binding.tvDropLocationAddress.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocation
                                }
                                if (data.data?.get(0)?.tripMode?.equals("1") == true) {
                                    binding.tvTripMode.text = getString(R.string.round_trip)
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        binding.tvDistance.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.distance + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(1)?.distanceUnit
                                        binding.tvBookingId.text =
                                            getString(R.string.booking_id_sag12345) + " " + data.data?.get(
                                                0
                                            )?.bookingId
                                        binding.tvTime.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.startTime + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(1)?.startTimeUnit
                                        binding.tvstartDate.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.startDate
                                        binding.tvCurrentLocation.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocationTitle
                                        binding.tvPickupLocationAddress.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocation
                                        binding.tvDestination.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocationTitle
                                        binding.tvDropLocationAddress.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocation
                                    } else {
                                        binding.tvDistance.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.distance + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvBookingId.text =
                                            getString(R.string.booking_id_sag12345) + " " + data.data?.get(
                                                0
                                            )?.bookingId
                                        binding.tvTime.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.startTimeUnit
                                        binding.tvstartDate.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.startDate
                                        binding.tvCurrentLocation.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                        binding.tvPickupLocationAddress.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocation
                                        binding.tvDestination.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle
                                        binding.tvDropLocationAddress.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocation
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
            }
        })
        viewModels.driverStatusResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is DriverStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                /*   binding.tvBusReady.text =
                                       getString(R.string.hello) + " " + SharedPref.readString(
                                           SharedPref.USERNAME
                                       ) + getString(
                                           R.string.your_bus_is_ready
                                       )*/

                                if (!(SharedPref.readString(SharedPref.USERNAME)).equals("null")) {
                                    binding.tvBusReady.text =
                                        getString(R.string.hello) + " " + SharedPref.readString(
                                            SharedPref.USERNAME
                                        ) + getString(
                                            R.string.your_bus_is_ready
                                        )
                                    binding.tvHelloUser.text =
                                        getString(R.string.hello) + " " + SharedPref.readString(
                                            SharedPref.USERNAME
                                        )
                                    binding.tvUserName.text = SharedPref.readString(
                                        SharedPref.USERNAME
                                    )
                                } else {
                                    binding.tvBusReady.text =
                                        getString(R.string.hello) + getString(R.string.customer) + getString(
                                            R.string.your_bus_is_ready
                                        )
                                    binding.tvHelloUser.text =
                                        getString(R.string.hello) + " " + getString(R.string.customer)
                                    binding.tvUserName.text = getString(R.string.customer)

                                }



                                binding.tvBusModel.text = data.data?.get(0)?.vehicle?.vehicleModel
                                binding.tvPlateNo.text = data.data?.get(0)?.vehicle?.vehicleNumber



                                if (data.data?.get(0)?.tripMode.equals("0")) {
                                    binding.tripType.text =
                                        getString(R.string.one_way)
                                    binding.tripModeForComplete.text =
                                        getString(R.string.one_way)
                                    binding.tvDriverLocation.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.currentLocationTitle

                                    if (!(data.data?.get(0)?.triplocations?.get(0)?.currentDistanceTime).isNullOrEmpty()) {
                                        binding.tvTimeAway.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.currentDistanceTime + " " + getString(
                                                R.string.away
                                            )
                                    }

                                    binding.tvMeetText.text =
                                        getString(R.string.meet) + " " + data.data?.get(0)?.vehicle?.user?.name + " on " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.pickupLocationTitle
                                    binding.tvCurrentLocation1.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                    binding.tvDestination1.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle

                                    binding.tvDistanace.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.distance + " " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.distanceUnit
                                    binding.tvDistanceForComplete.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.distance + " " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.distanceUnit
                                    binding.tvTripDate.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.startDate

                                    binding.tvTimePik.text =
                                        data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                            0
                                        )?.triplocations?.get(0)?.startTimeUnit
                                }
                                if (data.data?.get(0)?.tripMode.equals("1")) {
                                    binding.tvDriverLocation.text =
                                        data.data?.get(0)?.triplocations?.get(1)?.currentLocationTitle

                                    if (!(data.data?.get(0)?.triplocations?.get(1)?.currentDistanceTime).isNullOrEmpty()) {
                                        binding.tvTimeAway.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.currentDistanceTime + " " + getString(
                                                R.string.away
                                            )
                                    }
                                    binding.tripType.text =
                                        getString(R.string.round_trip)
                                    binding.tripModeForComplete.text =
                                        getString(R.string.round_trip)

                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {


                                        binding.tvMeetText.text =
                                            getString(R.string.meet) + data.data?.get(0)?.vehicle?.user?.name + " on " + data.data?.get(
                                                0
                                            )?.triplocations?.get(1)?.pickupLocationTitle
                                        binding.tvCurrentLocation1.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLocationTitle
                                        binding.tvDestination1.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLocationTitle

                                        binding.tvDistanace.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.distance + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvDistanceForComplete.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.distance + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvTripDate.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.startDate

                                        binding.tvTimePik.text =
                                            data.data?.get(0)?.triplocations?.get(1)?.startTime + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.startTimeUnit
                                    } else {
                                        binding.tvDriverLocation.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.currentLocationTitle
                                        if (!(data.data?.get(0)?.triplocations?.get(0)?.currentDistanceTime).isNullOrEmpty()) {
                                            binding.tvTimeAway.text =
                                                data.data?.get(0)?.triplocations?.get(0)?.currentDistanceTime + " " + getString(
                                                    R.string.away
                                                )
                                        } else {

                                        }
                                        binding.tvMeetText.text =
                                            getString(R.string.meet) + " " + data.data?.get(0)?.vehicle?.user?.name + " on " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.pickupLocationTitle
                                        binding.tvCurrentLocation1.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLocationTitle
                                        binding.tvDestination1.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLocationTitle

                                        binding.tvDistanace.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.distance + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvDistanceForComplete.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.distance + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.distanceUnit
                                        binding.tvTripDate.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.startDate

                                        binding.tvTimePik.text =
                                            data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                                0
                                            )?.triplocations?.get(0)?.startTimeUnit
                                    }
                                }
                                binding.tvPayment.text = ""
                                if (data.data?.get(0)?.vehicle != null) {


                                    binding.tvDriverName.text =
                                        data.data?.get(0)?.vehicle?.user?.name
                                    binding.tvBusNoDriver.text =
                                        data.data?.get(0)?.vehicle?.vehicleNumber
                                    if (data.data?.get(0)?.vehicle?.user?.driverRating?.size!! > 0) {
                                        binding.rating1.rating =
                                            data.data?.get(0)?.vehicle?.user?.driverRating?.get(0)?.averageRating!!.toFloat()
                                    }
                                    driver_no = data.data?.get(0)?.vehicle?.user?.mobile
                                    country_code = data.data?.get(0)?.vehicle?.user?.countryCode

                                    Glide.with(this)
                                        .load(AppConstants.BASE_URL + "" + data.data?.get(0)?.vehicle?.user?.avatar)
                                        .placeholder(R.drawable.mask)
                                        .into(binding.ivDriverImage)

                                }

                                var driverLat: Double? = null
                                var driverLng: Double? = null
                                var pickupLat: Double? = null
                                var pickupLng: Double? = null
                                var dropLat: Double? = null
                                var dropLng: Double? = null
                                if (data.data?.get(0)?.tripMode.equals("0")) {
                                    driverLat =
                                        data.data?.get(0)?.triplocations?.get(0)?.currentLocationLatitude?.toDouble()
                                    driverLng =
                                        data.data?.get(0)?.triplocations?.get(0)?.currentLocationLongitude?.toDouble()
                                    pickupLat =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLatitude?.toDouble()
                                    pickupLng =
                                        data.data?.get(0)?.triplocations?.get(0)?.pickupLongitude?.toDouble()
                                    dropLat =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLatitude?.toDouble()
                                    dropLng =
                                        data.data?.get(0)?.triplocations?.get(0)?.dropLongitude?.toDouble()
                                }
                                if (data.data?.get(0)?.tripMode.equals("1")) {
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        driverLat =
                                            data.data?.get(0)?.triplocations?.get(1)?.currentLocationLatitude?.toDouble()
                                        driverLng =
                                            data.data?.get(0)?.triplocations?.get(1)?.currentLocationLongitude?.toDouble()
                                        pickupLat =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLatitude?.toDouble()
                                        pickupLng =
                                            data.data?.get(0)?.triplocations?.get(1)?.pickupLongitude?.toDouble()
                                        dropLat =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLatitude?.toDouble()
                                        dropLng =
                                            data.data?.get(0)?.triplocations?.get(1)?.dropLongitude?.toDouble()
                                    } else {
                                        driverLat =
                                            data.data?.get(0)?.triplocations?.get(0)?.currentLocationLatitude?.toDouble()
                                        driverLng =
                                            data.data?.get(0)?.triplocations?.get(0)?.currentLocationLongitude?.toDouble()
                                        pickupLat =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLatitude?.toDouble()
                                        pickupLng =
                                            data.data?.get(0)?.triplocations?.get(0)?.pickupLongitude?.toDouble()
                                        dropLat =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLatitude?.toDouble()
                                        dropLng =
                                            data.data?.get(0)?.triplocations?.get(0)?.dropLongitude?.toDouble()
                                    }

                                }

                                driverLatLng = driverLat?.let { it1 ->
                                    driverLng?.let { it2 ->
                                        LatLng(
                                            it1,
                                            it2
                                        )
                                    }
                                }
                                pickupLatLng = pickupLat?.let { it1 ->
                                    pickupLng?.let { it2 ->
                                        LatLng(
                                            it1,
                                            it2
                                        )
                                    }
                                }
                                dropLatLng = dropLat?.let { it1 ->
                                    dropLng?.let { it2 ->
                                        LatLng(
                                            it1,
                                            it2
                                        )
                                    }
                                }

                                // updateDriverMarker(driverLatLng!!)
                                // animateCamera(driverLatLng!!) //
                                var status = ""
                                var trip_status_from_api = ""
                                /*if (data.data?.get(0)?.status.equals("7"))
                                {*/
                                if (data.data?.get(0)?.tripMode.equals("0")) {
                                    trip_status_from_api =
                                        data.data?.get(0)?.triplocations?.get(0)?.tripStatus.toString()
                                }
                                if (data.data?.get(0)?.tripMode.equals("1")) {
                                    if (data.data?.get(0)?.triplocations?.get(0)?.tripStatus.equals(
                                            "completed"
                                        )
                                    ) {
                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(1)?.tripStatus.toString()

                                    } else {
                                        trip_status_from_api =
                                            data.data?.get(0)?.triplocations?.get(0)?.tripStatus.toString()

                                    }
                                }
                                if (trip_status_from_api.equals("ontheway")) {
                                    if (currentTripState != "trackingToPickup") {
                                        clearPreviousState() // Clear polyline and reset
                                    }
                                    // clearPreviousState()
                                    currentTripState = "trackingToPickup"
                                    if (driverLatLng != null) {
                                        updateDriverMarker(driverLatLng!!)
                                        animateCamera(driverLatLng!!, pickupLatLng!!) //
                                        drawRoute(driverLatLng!!, pickupLatLng!!, true)
                                    }

                                    showPickupMarker(pickupLatLng!!, "trackingToPickup")
                                    // showDropMarker(dropLatLng!!)
                                    if (!isUserCollapsedDriverOnWay) {  // Only expand if user hasn’t collapsed it
                                        bottomSheetBehaviorForDriverOnWay.state =
                                            BottomSheetBehavior.STATE_EXPANDED
                                    }

                                    bottomSheetBehaviorForDriverOnWay.setHideable(false);

                                    bottomSheetBehavior.setHideable(true);
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetForRideIn.setHideable(true);
                                    bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetForRideC.setHideable(true);
                                    bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN
                                } else if (trip_status_from_api.equals("ongoing")) {
                                    // clearPreviousState()
                                    if (currentTripState != "trackingToDrop") {
                                        clearPreviousState() // Clear polyline and reset
                                    }
                                    currentTripState = "trackingToDrop"
                                    if (driverLatLng != null) {
                                        updateDriverMarker(driverLatLng!!)
                                        animateCamera(driverLatLng!!, dropLatLng!!) //
                                        drawRoute(pickupLatLng!!, dropLatLng!!, false)
                                    }
                                    //updateDropMarker()
                                    //showPickupMarker(dropLatLng!!)
                                    //showPickupMarker(pickupLatLng!!, "trackingToDrop")
                                    showDropMarker(dropLatLng!!)

                                    bottomSheetBehaviorForDriverOnWay.setHideable(true);
                                    bottomSheetBehaviorForDriverOnWay.state =
                                        BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetBehavior.setHideable(true);
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                    if (!isUserCollapsedRideIn) {
                                        bottomSheetForRideIn.state =
                                            BottomSheetBehavior.STATE_EXPANDED
                                    }
                                    bottomSheetForRideIn.setHideable(false);
                                    // bottomSheetForRideIn.state = BottomSheetBehavior.STATE_EXPANDED

                                    bottomSheetForRideC.setHideable(true);
                                    bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN

                                }
                                /* }
                                 if (data.data?.get(0)?.status.equals("4"))
                                 {*/
                                else if (trip_status_from_api.equals("completed")) {
                                    clearPreviousState()
                                    status = "trackingToPickup"
                                    pickupMarker?.remove()
                                    pickupMarker = null
                                    dropMarker?.remove()
                                    dropMarker = null
                                    if (driverLatLng != null) {
                                        updateDriverMarker(driverLatLng!!)
                                    }
                                    //animateCamera(driverLatLng!!,dropLatLng!!) //
                                    /* drawRoute(driverLatLng!!, dropLatLng!!, true)
                                     showPickupMarker()*/

                                    bottomSheetBehaviorForDriverOnWay.setHideable(true);
                                    bottomSheetBehaviorForDriverOnWay.state =
                                        BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetBehavior.setHideable(true);
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetForRideIn.setHideable(true);
                                    bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN
                                    if (!isUserCollapsedRideC) {
                                        bottomSheetForRideC.state =
                                            BottomSheetBehavior.STATE_EXPANDED
                                    }
                                    bottomSheetForRideC.setHideable(false);
                                    // bottomSheetForRideC.state = BottomSheetBehavior.STATE_EXPANDED
                                } else {
                                    bottomSheetBehaviorForDriverOnWay.setHideable(true);
                                    bottomSheetBehaviorForDriverOnWay.state =
                                        BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetBehavior.setHideable(false);
                                    if (!isUserCollapsedDriverDataOnly) {
                                        bottomSheetBehavior.state =
                                            BottomSheetBehavior.STATE_EXPANDED
                                        //bottomSheetForRideC.state = BottomSheetBehavior.STATE_EXPANDED
                                    }
                                    // bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                                    bottomSheetForRideIn.setHideable(true);
                                    bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

                                    bottomSheetForRideC.setHideable(true);
                                    bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN
                                }
                                //}


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
        viewModels.rescheduledBookingResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is BookingDetailResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.tvTime.text =
                                    data.data?.get(0)?.triplocations?.get(0)?.startTime + " " + data.data?.get(
                                        0
                                    )?.triplocations?.get(0)?.startTimeUnit
                                binding.btBooked.text = getString(R.string.rescheduled)
                                binding.tvstartDate.text =
                                    data.data?.get(0)?.triplocations?.get(0)?.startDate
                                bookingDetailsList.clear()
                                data.data?.let { it1 -> bookingDetailsList.addAll(it1) }

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

    // Draw route using Directions API
    /*  private fun drawRoute(start: LatLng, end: LatLng) {
          CoroutineScope(Dispatchers.IO).launch {
              val url = getDirectionsUrl(start, end) // Get directions URL
              val request = Request.Builder().url(url).build()
              val response = client.newCall(request).execute()

              if (response.isSuccessful) {
                  val responseData = response.body?.string()
                  responseData?.let {
                      val routePoints = parseDirections(it) // Parse the response to get route points
                      withContext(Dispatchers.Main) {
                          mMap.clear() // Clear previous paths
                          drawPolyline(routePoints) // Draw the new polyline
                      }
                  }
              }
          }
      }*/
    //private var currentRoute: List<LatLng>? = null
    private var currentRoute: List<LatLng>? = null
    private var currentRoutePoints: List<LatLng>? = null
    private var currentTripState: String? = null

    private fun drawRoute(start: LatLng, end: LatLng, isPickupRoute: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = getDirectionsUrl(start, end)
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                responseData?.let {
                    val newRoutePoints = parseDirections(it)

                    withContext(Dispatchers.Main) {
                        // Skip redrawing if the route and state are the same
                        if (currentTripState == (if (isPickupRoute) "trackingToPickup" else "trackingToDrop")
                            && currentRoutePoints != null
                            && PolyUtil.isLocationOnPath(
                                start,
                                currentRoutePoints!!,
                                false,
                                50.0 /* Tolerance in meters */
                            )
                        ) {
                            Log.d("Route", "No route redraw needed.")
                            return@withContext
                        }

                        // Update current state and redraw polyline
                        currentRoutePoints = newRoutePoints
                        currentTripState =
                            if (isPickupRoute) "trackingToPickup" else "trackingToDrop"
                        drawPolyline(newRoutePoints, isPickupRoute)
                    }
                }
            }
        }
    }

    /* private fun drawRoute(start: LatLng, end: LatLng, b: Boolean) {
         CoroutineScope(Dispatchers.IO).launch {
             val url = getDirectionsUrl(start, end)
             val request = Request.Builder().url(url).build()
             val response = client.newCall(request).execute()

             if (response.isSuccessful) {
                 val responseData = response.body?.string()
                 responseData?.let {
                     val routePoints = parseDirections(it)

                     // Check if driver is still on the current route
                     if (currentRoute == null || !PolyUtil.isLocationOnPath(
                             start, currentRoute!!, false, 10.0 *//* tolerance in meters *//*
                        )
                    ) {
                        currentRoute = routePoints
                        withContext(Dispatchers.Main) {
                            mMap.clear() // Only clear the map if the path is redrawn
                            drawPolyline(routePoints,b)
                            showPickupMarker() // Re-add the pickup marker
                            updateDriverMarker(start) // Re-add the driver marker
                        }
                    }
                }
            }
        }
    }
*/

    private fun parseDirections(response: String): List<LatLng> {
        Log.d("DirectionsResponse", response)

        val result = mutableListOf<LatLng>()
        try {
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")

            if (routes.length() == 0) {
                // No routes found
                runOnUiThread {
                    Toast.makeText(this, getString(R.string.no_routes_found), Toast.LENGTH_SHORT)
                        .show()
                }
                return result
            }

            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            val durationObject = legs.getJSONObject(0).getJSONObject("duration")
            val durationText = durationObject.getString("text") // e.g., "15 mins
            //binding.tvTimeTaken.text = durationText

            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                result.addAll(PolyUtil.decode(points))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                //Toast.makeText(this, "Failed to parse directions", Toast.LENGTH_SHORT).show()
            }
        }

        return result
        /*val result = mutableListOf<LatLng>()
        try {
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")

            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                result.addAll(PolyUtil.decode(points))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result*/
    }

    // Draw polyline on the map
    /* private fun drawPolyline(points: List<LatLng>) {
         val polylineOptions = PolylineOptions().apply {
             color(Color.BLUE)
             width(10f)
             addAll(points)
         }
         mMap.addPolyline(polylineOptions)
     }*/
    private var polyline: Polyline? = null

    /* private fun drawPolyline(points: List<LatLng>) {
         if (polyline == null) {
             val polylineOptions = PolylineOptions().apply {
                 color(Color.BLUE)
                 width(10f)
                 addAll(points)
             }
             polyline = mMap.addPolyline(polylineOptions)
         } else {
             polyline?.points = points // Update the polyline's points
         }
     }*/
    private fun drawPolyline(points: List<LatLng>, isPickupRoute: Boolean) {
        if (isPickupRoute) {
            Log.d("Route", "Drawing pickup route with ${points.size} points")
            polylineToPickup = mMap.addPolyline(
                PolylineOptions()
                    .addAll(points)
                    .color(Color.BLUE)
                    .width(10f)
            )
        } else {
            Log.d("Route", "Drawing drop route with ${points.size} points")
            polylineToDrop = mMap.addPolyline(
                PolylineOptions()
                    .addAll(points)
                    .color(Color.BLUE)
                    .width(10f)
            )
        }
    }


    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"

        val key = "key=${getString(R.string.google_search_key)}"
        val parameters = "$strOrigin&$strDest&$key"
        //val parameters = "$strOrigin&$strDest&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
    }


    private fun showCalenderDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_calender)
        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

        }

        //yyyy-mm-dd

        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val btSubmitDate: Button = dialog.findViewById(R.id.btSubmitDate)
        val btContinueDate: Button = dialog.findViewById(R.id.btContinueDate)

        if (trip_mode == 0) {
            btContinueDate.visibility = View.GONE
            btSubmitDate.visibility = View.VISIBLE
        }
        if (trip_mode == 1) {
            btContinueDate.visibility = View.VISIBLE
            btSubmitDate.visibility = View.GONE
        }

        var calenderView = dialog.findViewById<CalendarView>(R.id.calenderView)
        //calenderViewRound = dialog.findViewById(R.id.calenderViewRound)
        var timePicker = dialog.findViewById<TimePicker>(R.id.timePicker)
        var timePicker2 = dialog.findViewById<TimePicker>(R.id.timePicker2)
        /*// Get the current date in milliseconds
        val currentDateInMillis = System.currentTimeMillis()

        // Set the minimum date (today's date) in the CalendarView
        calenderView.minDate = currentDateInMillis*/


        // Get today's date
        val calendar = Calendar.getInstance()

        // Add one day to the current date
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        // Get the time in milliseconds
        val tomorrow = calendar.timeInMillis

        // Set the minimum date to tomorrow
        calenderView.minDate = tomorrow

        selectedDate = getFormattedDateFromCalendarView(calenderView)
        round_pickup_date = getFormattedDateFromCalendarView(calenderView)
        val currentDate = getFormattedDateFromCalendarView(calenderView)
        // val currentDate = getFormattedDateOfTommorowView(calenderView)

        calenderView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            if (trip_mode == 0) {
                selectedDate = format.format(calendar.time)
            }
            if (trip_mode == 1) {
                if (is_Contunue_clicked) {
                    round_pickup_date = format.format(calendar.time)

                } else {
                    selectedDate = format.format(calendar.time)
                }
            }


            //Toast.makeText(this@SelectRideWithTypeActivity, selectedDate, Toast.LENGTH_SHORT).show()
            // Use the selectedDate string as needed

        }

        // Get the selected time from the TimePicker
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            // Format time in 12-hour format with AM/PM
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            selectedTime = timeFormat.format(calendar.time)

            // Split the formatted time into time part and period part
            val timeParts = selectedTime.split(" ")
            // Time part (e.g., "02:30")
            time = timeParts[0]
            // Period part (e.g., "PM")
            time_unit = timeParts[1]

        }
        timePicker2.setOnTimeChangedListener { _, hourOfDay, minute ->

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            // Format time in 12-hour format with AM/PM
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            roundTime = timeFormat.format(calendar.time)

            // Split the formatted time into time part and period part
            val timeParts = roundTime.split(" ")
            // Time part (e.g., "02:30")
            round_pickup_time = timeParts[0]
            // Period part (e.g., "PM")
            round_pickup_time_unit = timeParts[1]


        }

        getCurrentTimes(timePicker, timePicker2)

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        btContinueDate.setOnClickListener {
            Log.d("btContinueDate", selectedDate.toString())//6
            Log.d("btContinueDate", currentDate.toString())//7

            val selectedDate = Calendar.getInstance().apply {
                timeInMillis = calenderView.date
            }

            // Get the selected time from TimePicker
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute

            // Set the selected time
            selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedDate.set(Calendar.MINUTE, selectedMinute)

            // Check if the selected time is at least 24 hours from the current time
            val currentTime = Calendar.getInstance().timeInMillis
            val twentyFourHoursFromNow = currentTime + 24 * 60 * 60 * 1000

            /* if (selectedDate.timeInMillis < twentyFourHoursFromNow) {
                 // Show an error message
                 Toast.makeText(
                     this,
                     getString(R.string.you_cannot_book_a_ride_less_than_24_hours_from_now),
                     Toast.LENGTH_SHORT
                 ).show()
             } else {*/

            is_Contunue_clicked = true
            //calenderView.visibility = View.GONE
            // calenderViewRound.visibility = View.VISIBLE
            timePicker.visibility = View.GONE
            timePicker2.visibility = View.VISIBLE
            btContinueDate.visibility = View.GONE
            btSubmitDate.visibility = View.VISIBLE
            /* dialog.dismiss()

             getVehicleCategoryApi()*/
            // Proceed with the booking
            //Toast.makeText(this, "Booking confirmed", Toast.LENGTH_SHORT).show()
            //}
            /* if (selectedDate == currentDate) {
                 showToast("You cannot select Now Time for Today")
             } else {
                 is_Contunue_clicked = true
                 //calenderView.visibility = View.GONE
                 // calenderViewRound.visibility = View.VISIBLE
                 timePicker.visibility = View.GONE
                 timePicker2.visibility = View.VISIBLE
                 btContinueDate.visibility = View.GONE
                 btSubmitDate.visibility = View.VISIBLE

             }*/
        }
        btSubmitDate.setOnClickListener {
            /*if (selectedDate != null && selectedTime != null) {
                dialog.dismiss()

                getVehicleCategoryApi()

            } else {
                showToast(getString(R.string.please_select_date_and_time))
            }*/
            if (trip_mode == 0) {

                handleDateAndTimeSelection(calenderView, timePicker, dialog)
                /*  val selectedDate = Calendar.getInstance().apply {
                      timeInMillis = calenderView.date
                  }

                  // Get the selected time from TimePicker
                  val selectedHour = timePicker.hour
                  val selectedMinute = timePicker.minute

                  // Set the selected time
                  selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour)
                  selectedDate.set(Calendar.MINUTE, selectedMinute)

                  // Check if the selected time is at least 24 hours from the current time
                  val currentTime = Calendar.getInstance().timeInMillis
                  val twentyFourHoursFromNow = currentTime + 24 * 60 * 60 * 1000

                  if (selectedDate.timeInMillis < twentyFourHoursFromNow) {
                      // Show an error message
                      Toast.makeText(
                          this,
                          "You cannot book a ride less than 24 hours from now",
                          Toast.LENGTH_SHORT
                      ).show()
                  } else {
                      dialog.dismiss()

                      getVehicleCategoryApi()
                      // Proceed with the booking
                      //Toast.makeText(this, "Booking confirmed", Toast.LENGTH_SHORT).show()
                  }*/
                /* if (!selectedTime.equals("") ) {
                     dialog.dismiss()

                     getVehicleCategoryApi()

                 } else {
                     showToast(getString(R.string.you_cannot_select_now_time))
                 }*/
            } else if (trip_mode == 1) {
                if (selectedDate == round_pickup_date) {
                    showToast(getString(R.string.both_the_dates_for_round_trip_should_not_be_same))
                } else {
                    dialog.dismiss()
                    // getVehicleCategoryApi()
                }
            }
        }

        dialog.show()
    }

    // Function to get the current selected date from CalendarView
    private fun getFormattedDateFromCalendarView(calendarView: CalendarView): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarView.date
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun getCurrentTimes(timePicker: TimePicker, timePicker2: TimePicker) {
        // this method is called when no time is selected from the timepicker change listener
        val hourOfDay = if (Build.VERSION.SDK_INT >= 23) {
            timePicker.hour
        } else {
            timePicker.currentHour
        }

        val minute = if (Build.VERSION.SDK_INT >= 23) {
            timePicker.minute
        } else {
            timePicker.currentMinute
        }
        val hourOfDay2 = if (Build.VERSION.SDK_INT >= 23) {
            timePicker2.hour
        } else {
            timePicker2.currentHour
        }

        val minute2 = if (Build.VERSION.SDK_INT >= 23) {
            timePicker2.minute
        } else {
            timePicker2.currentMinute
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val calendar2 = Calendar.getInstance()
        calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay2)
        calendar2.set(Calendar.MINUTE, minute2)

// Format time in 12-hour format with AM/PM
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        roundTime = timeFormat.format(calendar2.time)
        selectedTime = timeFormat.format(calendar.time)

// Split the formatted time into time part and period part
        val timeParts = roundTime.split(" ")
        round_pickup_time = timeParts[0]
        round_pickup_time_unit = timeParts[1]

        val timeParts2 = selectedTime.split(" ")
        // Time part (e.g., "02:30")
        time = timeParts2[0]
        // Period part (e.g., "PM")
        time_unit = timeParts2[1]

    }

    private fun handleDateAndTimeSelection(
        calenderView: CalendarView,
        timePicker: TimePicker,
        dialog: Dialog
    ) {
        val selectedDate = Calendar.getInstance().apply {
            timeInMillis = calenderView.date
        }

        val selectedHour = timePicker.hour
        val selectedMinute = timePicker.minute

        selectedDate.set(Calendar.HOUR_OF_DAY, selectedHour)
        selectedDate.set(Calendar.MINUTE, selectedMinute)

        val currentTime = Calendar.getInstance().timeInMillis
        val twentyFourHoursFromNow = currentTime + 24 * 60 * 60 * 1000

        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_YEAR, 1)
        val isTomorrow = selectedDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)

        currentDate.add(Calendar.DAY_OF_YEAR, -1)

        /* if (isTomorrow && selectedDate.timeInMillis < twentyFourHoursFromNow) {
             Toast.makeText(
                 this,
                 getString(R.string.you_cannot_book_a_ride_less_than_24_hours_from_now),
                 Toast.LENGTH_SHORT
             ).show()
         } else {*/
        dialog.dismiss()
        //getVehicleCategoryApi()
        rescheduledBookingForOneWayApi()
        //}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the location layer if permission is granted
        if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }


        mMap.isMyLocationEnabled = true

        // mMap.setLatLngBoundsForCameraTarget(kuwaitBounds)
        // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(kuwaitBounds, 100))

        // Get the current location of the device and set the position of the map

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                driverMarker = mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Current Location")
                )!!
                //updateLocationInfo(currentLatLng)
            }
        }


    }

    /*// Show pickup marker
    private fun showPickupMarker() {
        mMap.addMarker(MarkerOptions().position(pickupLatLng!!).title("Pickup Location"))
    }*/
    private var pickupMarker: Marker? = null
    private var dropMarker: Marker? = null

    private fun showPickupMarker(dropLatLng: LatLng, s: String) {
        // if (dropLatLng != null && driverMarker?.position != dropLatLng) {
        // if (location != null && driverMarker?.position != location) {
        /*if (s.equals("trackingToPickup")) {
            mMap.addMarker(
                MarkerOptions()
                    .position(dropLatLng!!)
                    .title("Pickup Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_customer))
                    .zIndex(0.5f) // Lower Z-index for pickup marker
            )
        }
        else{*/
        pickupMarker = mMap.addMarker(
            MarkerOptions()
                .position(dropLatLng!!)
                .title("Pickup Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_customer))
                .zIndex(0.5f) // Lower Z-index for pickup marker
        )
        // }

        // }
    }

    private fun showDropMarker(dropLatLng: LatLng) {

        // if (location != null && driverMarker?.position != location) {
        dropMarker = mMap.addMarker(
            MarkerOptions()
                .position(dropLatLng!!)
                .title("Pickup Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_flag))
                .zIndex(0.5f) // Lower Z-index for pickup marker
        )

    }


    // Update the drop marker and clear the driver marker if needed
    private fun updateDropMarker() {
        // Clear the driver marker
        driverMarker?.remove()


        // Add markers for pickup and drop locations
        mMap.addMarker(MarkerOptions().position(pickupLatLng!!).title("Pickup Location"))
        mMap.addMarker(MarkerOptions().position(dropLatLng!!).title("Drop Location"))

        // Update driver marker at current location
        updateDriverMarker(driverLatLng!!)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, proceed with location access
                    // Initialize the map fragment
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
                    mapFragment.getMapAsync(this@MyRideActivity)
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_denied), Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }

            else -> {
                // Handle other permissions if necessary
            }
        }
    }


    // Optional: Stop location updates when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Stop location updates when activity is destroyed
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacksAndMessages(null) // Stop location updates when activity is destroyed

    }

    // Start tracking the driver
    fun startTracking() {
        handler.post(object : Runnable {
            override fun run() {
                driverStatusApi() // Fetch driver location and status
                handler.postDelayed(this, 15000) // Repeat every 3 seconds
            }
        })
    }

    /*  // Update driver marker on the map
      private fun updateDriverMarker(location: LatLng) {
          if (driverMarker == null) {
              driverMarker = mMap.addMarker(MarkerOptions().position(location).title("Driver"))!!
          } else {
              driverMarker?.position = location
          }
      }*/

    // this one is previous
    /* private fun updateDriverMarker(location: LatLng) {
         if (driverMarker == null) {
             driverMarker = mMap.addMarker(
                 MarkerOptions()
                     .position(location)
                     .title("Driver")
                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                     .zIndex(1.0f) // Lower Z-index for pickup marker
             )!!
         } else {
             driverMarker?.apply {
                 position = location
                 setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                 zIndex = 1.0f // Always set higher Z-index to ensure visibility
             }
         }
     }
       private var lastKnownLocation: LatLng? = null
   */
    private fun updateDriverMarker(location: LatLng) {
        val bearing = lastKnownLocation?.let { calculateBearing(it, location) } ?: 0f

        if (driverMarker == null) {
            // Create the marker if it doesn't exist
            driverMarker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Driver")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                    .rotation(bearing) // Set initial rotation
                    .flat(true) // Ensure the icon rotates flatly
                    .zIndex(1.0f)
            )!!
        } else {
            // Update the existing marker
            driverMarker?.apply {
                position = location
                rotation = bearing // Update rotation
                setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
            }
        }

        lastKnownLocation = location // Update the last known location
    }

    private fun calculateBearing(start: LatLng, end: LatLng): Float {
        val startLat = Math.toRadians(start.latitude)
        val startLng = Math.toRadians(start.longitude)
        val endLat = Math.toRadians(end.latitude)
        val endLng = Math.toRadians(end.longitude)

        val deltaLng = endLng - startLng
        val x = Math.sin(deltaLng) * Math.cos(endLat)
        val y = Math.cos(startLat) * Math.sin(endLat) -
                Math.sin(startLat) * Math.cos(endLat) * Math.cos(deltaLng)

        return (Math.toDegrees(Math.atan2(x, y)).toFloat() + 360) % 360
    }


    /* private fun updateDriverMarker(location: LatLng) {
         if (driverMarker == null) {
             driverMarker = mMap.addMarker(
                 MarkerOptions()
                     .position(location)
                     .title("Driver")
                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
             )!!
         } else {
             driverMarker?.position = location
         }
     }
 */

    // Animate the camera to the driver's position
    /*private fun animateCamera(location: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }*/
    private fun animateCamera(driverLatLng: LatLng, pickupLatLng: LatLng) {
        val bounds = LatLngBounds.builder()
            .include(driverLatLng)
            .include(pickupLatLng)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)) // Padding of 100px
    }


    override fun onResume() {
        super.onResume()
        if (where.equals("schedule")) {
            /* bottomSheetBehavior.setHideable(false)
             bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

             bottomSheetBehaviorForDriverOnWay.setHideable(true)
             bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_HIDDEN

             bottomSheetForRideIn.setHideable(true)
             bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

             bottomSheetForRideC.setHideable(true)
             bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN*/


            /* headerMyRide.ivMenu.visibility = View.GONE
             headerMyRide.headerTitle.text = ""*/

            /* bottomSheetBehaviorForDriverOnWay.setHideable(false);
             bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_EXPANDED

             bottomSheetBehavior.setHideable(true);
             bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

             bottomSheetForRideIn.setHideable(true);
             bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

             bottomSheetForRideC.setHideable(true);
             bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN
 */
            // handler.post(runnable)

            // driverStatusApi()

            bottomSheetBehavior.setHideable(false)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetBehaviorForDriverOnWay.setHideable(true)
            bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideIn.setHideable(true)
            bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideC.setHideable(true)
            bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN

            startTracking()


        }

        if (where.equals("checkout")) {
            /* headerMyRide.ivMenu.visibility = View.VISIBLE
             headerMyRide.headerTitle.text = getString(R.string.my_ride)*/
            bottomSheetBehavior.setHideable(false)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBehaviorForDriverOnWay.setHideable(true)
            bottomSheetBehaviorForDriverOnWay.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideIn.setHideable(true)
            bottomSheetForRideIn.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetForRideC.setHideable(true)
            bottomSheetForRideC.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    private fun clearPreviousState() {

        currentRoutePoints = null
        currentTripState = null

        polylineToPickup?.remove()
        polylineToPickup = null

        polylineToDrop?.remove()
        polylineToDrop = null


    }


}