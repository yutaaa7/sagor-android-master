package com.sagarclientapp.ui.home.selectRide

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivitySelectRideWithTypeBinding
import com.sagarclientapp.model.BusCategory
import com.sagarclientapp.model.BusCategoryResponse
import com.sagarclientapp.model.CreateBooking
import com.sagarclientapp.model.CreateBookingResponse
import com.sagarclientapp.model.NotesResponse
import com.sagarclientapp.ui.checkout.CheckoutActivity
import com.sagarclientapp.ui.home.selectRide.viewModels.SelectRideWithTypeViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModelProvider
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
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SelectRideWithTypeActivity : BaseActivity() {

    lateinit var calenderView: CalendarView
    lateinit var calenderViewRound: CalendarView
    lateinit var binding: ActivitySelectRideWithTypeBinding
    var bookingDataList: ArrayList<CreateBooking> = ArrayList<CreateBooking>()
    var trip_mode: Int? = null
    var selectedDate: String? = null
    var round_pickup_date: String? = null
    var selectedTime: String = ""
    var roundTime: String = ""
    var vehicle_cat_id: String? = null
    var pickup_location: String? = null
    var pickup_latitude: String? = null
    var pickup_longitude: String? = null
    var drop_location: String? = null
    var drop_latitude: String? = null
    var drop_longitude: String? = null
    private val client = OkHttpClient()
    var round_pickup_time: String? = null
    var round_pickup_time_unit: String? = null
    var time: String? = null
    var time_unit: String? = null
    var duration_to_travel = ""
    var time_difference = ""

    var pickup_location_title: String? = null
    var drop_location_title: String? = null
    var distance: String? = null
    var dis_unit: String? = null
    var bundle: Bundle? = null
    var is_Contunue_clicked = false
    lateinit var notesAdapter: NotesAdapter

    var vehicleCategoryList = mutableListOf<BusCategory>()
    lateinit var busCategoryAdapter: BusesListAdapter
    private val viewModels: SelectRideWithTypeViewModel by viewModels<SelectRideWithTypeViewModel>()
    private val sharedViewModel: SharedViewModel by lazy {
        SharedViewModelProvider.getSharedViewModel(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectRideWithTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUi()
        apisObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUi() = binding.apply {
        bundle = intent.extras

        if (bundle != null) {

            distance = bundle?.getString("distance")
            dis_unit = bundle?.getString("dis_unit")

        }
        rvBuses.layoutManager =
            LinearLayoutManager(this@SelectRideWithTypeActivity)
        busCategoryAdapter = BusesListAdapter(this@SelectRideWithTypeActivity, vehicleCategoryList,
            object : BusesListAdapter.OnItemClickListener {
                override fun onItemClick(id: Int?) {
                    vehicle_cat_id = id.toString()
                    btBookRide.setBackgroundResource(R.drawable.solid_theme_blue)
                    btBookRide.isClickable = true
                    btBookRide.isEnabled = true
                }

            })
        rvBuses.adapter = busCategoryAdapter




        btSelectTimeDate.setOnClickListener {
            showCalenderDialog()
        }
        tvChangeDate.setOnClickListener {
            is_Contunue_clicked =false
            showCalenderDialog()
        }
        llOneWay.setOnClickListener {
            //'0 => one-way, 1 => round-trip'
            trip_mode = 0
            llOneWay.setBackgroundResource(R.drawable.solid_theme_blue)
            llRound.setBackgroundResource(R.drawable.solid_gray)
            ivOneWay.setColorFilter(getColor(R.color.white))
            ivRoundTrip.setColorFilter(getColor(R.color.theme_blue))
            tvRound.setTextColor(resources.getColor(R.color.theme_blue))
            tvOneWay.setTextColor(resources.getColor(R.color.white))
            btSelectTimeDate.setBackgroundResource(R.drawable.solid_theme_blue)
            btSelectTimeDate.isEnabled = true

            vehicle_cat_id = ""
            binding.btSelectTimeDate.visibility = View.VISIBLE
            is_Contunue_clicked =false
            binding.cardPickupData.visibility = View.GONE
            binding.tvChangeDate.visibility = View.GONE
            binding.llPickUpAddress.visibility = View.GONE
            binding.llPickupTimes.visibility = View.GONE

            binding.llRecommendedBuses.visibility = View.GONE
            binding.btBookRide.visibility = View.GONE

            btBookRide.setBackgroundResource(R.drawable.solid_disable_button)
            btBookRide.isClickable = false
            btBookRide.isEnabled = false
            vehicleCategoryList.clear()
            busCategoryAdapter.callThis()
            //busCategoryAdapter.notifyDataSetChanged()
        }
        llRound.setOnClickListener {
            trip_mode = 1
            btSelectTimeDate.setBackgroundResource(R.drawable.solid_theme_blue)
            btSelectTimeDate.isEnabled = true
            ivOneWay.setColorFilter(getColor(R.color.theme_blue))
            ivRoundTrip.setColorFilter(getColor(R.color.white))
            llOneWay.setBackgroundResource(R.drawable.solid_gray)
            llRound.setBackgroundResource(R.drawable.solid_theme_blue)
            tvRound.setTextColor(resources.getColor(R.color.white))
            tvOneWay.setTextColor(resources.getColor(R.color.theme_blue))

            vehicle_cat_id = ""
            is_Contunue_clicked =false
            binding.cardPickupData.visibility = View.GONE
            binding.btSelectTimeDate.visibility = View.VISIBLE
            binding.tvChangeDate.visibility = View.GONE
            binding.llPickUpAddress.visibility = View.GONE
            binding.llPickupTimes.visibility = View.GONE

            binding.llRecommendedBuses.visibility = View.GONE
            binding.btBookRide.visibility = View.GONE
            btBookRide.setBackgroundResource(R.drawable.solid_disable_button)
            btBookRide.isClickable = false
            btBookRide.isEnabled = false
            vehicleCategoryList.clear()
            busCategoryAdapter.callThis()

            // busCategoryAdapter.notifyDataSetChanged()
        }
        btBookRide.setOnClickListener {
            // launchActivity(CheckoutActivity::class.java, removeAllPrevActivities = false)
            if (trip_mode == 0) {
                createBookingApi()
            }
            if (trip_mode == 1) {
                createBookingForRoundTripApi()
            }

        }
        headerSelectRide.ivBack.setOnClickListener {
            finish()
        }

        sharedViewModel.currentAddress.observe(
            this@SelectRideWithTypeActivity,
            Observer { address ->
                // Update current address UI
                pickup_location = address
                tvPickupAddress.text = address

            })
        sharedViewModel.currentName.observe(this@SelectRideWithTypeActivity, Observer { name ->
            // Update current address UI
            pickup_location_title = name
            tvPicupTitle.text = name
        })
        sharedViewModel.destinationName.observe(this@SelectRideWithTypeActivity, Observer { name ->
            // Update current address UI
            drop_location_title = name
            tvdesTitle.text = name
        })

        sharedViewModel.destinationAddress.observe(
            this@SelectRideWithTypeActivity,
            Observer { address ->
                // Update destination address UI
                tvDesAddes.text = address
                drop_location = address

            })
        sharedViewModel.currentLocation.observe(
            this@SelectRideWithTypeActivity,
            Observer { latLng ->
                pickup_latitude = latLng.latitude.toString()
                pickup_longitude = latLng.longitude.toString()


            })

        sharedViewModel.destinationLocation.observe(
            this@SelectRideWithTypeActivity,
            Observer { latLng ->
                drop_latitude = latLng.latitude.toString()
                drop_longitude = latLng.longitude.toString()
            })




        getNotesListApi()

    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        calenderView = dialog.findViewById(R.id.calenderView)
        calenderViewRound = dialog.findViewById(R.id.calenderViewRound)
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
            Log.d("CAL",trip_mode.toString())
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            if (trip_mode == 0) {
                selectedDate = format.format(calendar.time)
            }
            if (trip_mode == 1) {
                Log.d("CAL",trip_mode.toString())
                if (is_Contunue_clicked) {
                    round_pickup_date = format.format(calendar.time)
                    Log.d("CAL","is_Contunue_clicked "+round_pickup_date)
                } else {

                    selectedDate = format.format(calendar.time)
                    Log.d("CAL","else is_Contunue_clicked "+selectedDate)

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
            round_pickup_date=selectedDate
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

            /*if (selectedDate.timeInMillis < twentyFourHoursFromNow) {
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
        /*btSubmitDate.setOnClickListener {
            *//*if (selectedDate != null && selectedTime != null) {
                dialog.dismiss()

                getVehicleCategoryApi()

            } else {
                showToast(getString(R.string.please_select_date_and_time))
            }*//*
            if (trip_mode == 0) {

                handleDateAndTimeSelection(calenderView, timePicker, dialog)
                *//*  val selectedDate = Calendar.getInstance().apply {
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
                  }*//*
                *//* if (!selectedTime.equals("") ) {
                     dialog.dismiss()

                     getVehicleCategoryApi()

                 } else {
                     showToast(getString(R.string.you_cannot_select_now_time))
                 }*//*
            } else if (trip_mode == 1) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

// Parse the times into Date objects
                val selectedTimeDate = timeFormat.parse(selectedTime)
                val roundTimeDate = timeFormat.parse(roundTime)

// Compare the times
                if (selectedDate == round_pickup_date) { // Check if the dates are the same
                    when {
                        roundTimeDate == selectedTimeDate -> { // Times are exactly the same
                            Toast.makeText(
                                this,
                                getString(R.string.please_select_a_time_after_the_selected_time),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        roundTimeDate!!.before(selectedTimeDate) -> { // Round time is before selected time
                            Toast.makeText(
                                this,
                                getString(R.string.round_time_is_less_than_selected_time),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> { // Valid case
                            dialog.dismiss()
                            getVehicleCategoryApi()
                        }
                    }
                }

                else{
                    dialog.dismiss()
                    getVehicleCategoryApi()
                }
            }
        }*/
        btSubmitDate.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust format to match your date strings
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())   // Adjust format for time strings

            if (trip_mode == 0) {
                // Handle one-way trip logic
                handleDateAndTimeSelection(calenderView, timePicker, dialog)
            } else if (trip_mode == 1) {
                try {
                    // Parse dates
                    val selectedDateParsed = dateFormat.parse(selectedDate)
                    val roundDateParsed = dateFormat.parse(round_pickup_date)

                    // Parse times
                    val selectedTimeParsed = timeFormat.parse(selectedTime)
                    val roundTimeParsed = timeFormat.parse(roundTime)

                    if (selectedDateParsed == roundDateParsed) {
                        when {
                            roundTimeParsed == selectedTimeParsed -> {
                                showToast(getString(R.string.please_select_a_time_after_the_selected_time))
                            }
                            roundTimeParsed!!.before(selectedTimeParsed) -> {
                                showToast(getString(R.string.round_time_is_less_than_selected_time))
                            }
                            else -> {
                                // Valid case: Dates are the same, and roundTime is after selectedTime
                                dialog.dismiss()
                                getVehicleCategoryApi()
                            }
                        }
                    } else if (roundDateParsed!!.before(selectedDateParsed)) {
                        // Round trip date is before the selected date
                        showToast(getString(R.string.round_date_cannot_before_pickup_date))
                    } else {
                        // Valid case: Round date is after the selected date
                        dialog.dismiss()
                        getVehicleCategoryApi()
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    //showToast(getString(R.string.invalid_date_or_time_format))
                }
            }
        }


        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        /*if (isTomorrow && selectedDate.timeInMillis < twentyFourHoursFromNow) {
            Toast.makeText(
                this,
                getString(R.string.you_cannot_book_a_ride_less_than_24_hours_from_now),
                Toast.LENGTH_SHORT
            ).show()
        } else {*/
        dialog.dismiss()
        getVehicleCategoryApi()
        //}
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
        Log.d("TIME HERE", roundTime)
        Log.d("TIME HERE", selectedTime)

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

    // Function to get the current selected date from CalendarView
    private fun getFormattedDateFromCalendarView(calendarView: CalendarView): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarView.date
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    // Function to get the tommorow  date from CalendarView
    private fun getFormattedDateOfTommorowView(calendarView: CalendarView): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarView.date

        // Add one day to the calendar's date to get tomorrow's date
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVehicleCategoryApi() {
        var pickupLocation =
            pickup_latitude?.toDouble()?.let {
                pickup_longitude?.toDouble()
                    ?.let { it1 -> LatLng(it, it1) }
            } // Los Angeles
        var dropLocation = drop_latitude?.toDouble()?.let {
            drop_longitude?.toDouble()
                ?.let { it1 -> LatLng(it, it1) }
        }
        if (pickupLocation != null) {
            if (dropLocation != null) {
                fetchTravelTime(pickupLocation, dropLocation) { durationText ->
                    if (durationText != null) {
                        duration_to_travel = durationText
                        Log.d("duration_to_travel in ", duration_to_travel)

                        //sendDurationToApi(durationText) // Pass duration as a String to your API
                    } else {
                        // Toast.makeText(this, "Failed to retrieve travel time", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Log.d("duration_to_travel", duration_to_travel)


        if (trip_mode == 1) {
            Log.d("TIME_ES", round_pickup_date.toString())
            Log.d("TIME_ES", round_pickup_time.toString())
            Log.d("TIME_ES", round_pickup_time_unit.toString())
            Log.d("TIME_ES", selectedDate.toString())
            Log.d("TIME_ES", time.toString())
            Log.d("TIME_ES", time_unit.toString())

            calculateTimeDifference(
                selectedDate.toString(),
                time.toString(),
                time_unit.toString(),
                round_pickup_date.toString(),
                round_pickup_time.toString(),
                round_pickup_time_unit.toString()
            )

        }


        showDialog()
        viewModels.vehicleCategoryApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            distance.toString(),
            dis_unit.toString(),
            trip_mode.toString(),
            time_difference
        )
    }

    fun getNotesListApi() {
        showDialog()
        viewModels.notesListingApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            "1"
        )
    }


    fun createBookingApi() {

        showDialog()
        viewModels.createBookingApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            pickup_location = pickup_location.toString(),
            pickup_latitude = pickup_latitude.toString(),
            pickup_longitude = pickup_longitude.toString(),
            drop_location = drop_location.toString(),
            drop_latitude = drop_latitude.toString(),
            drop_longitude = drop_longitude.toString(),
            distance = distance.toString(),
            distance_unit = dis_unit.toString(),
            pickup_date = selectedDate.toString(),
            pickup_time = time.toString(),
            pickup_time_unit = time_unit.toString(),
            trip_mode = trip_mode.toString(),
            vehicle_cat_id = vehicle_cat_id.toString(),
            pickup_location_title = pickup_location_title.toString(),
            drop_location_title = drop_location_title.toString(),
            approx_total_time = duration_to_travel

        )
    }

    fun createBookingForRoundTripApi() {

        showDialog()
        viewModels.createBookingForRoundTripApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            pickup_location = pickup_location.toString(),
            pickup_latitude = pickup_latitude.toString(),
            pickup_longitude = pickup_longitude.toString(),
            drop_location = drop_location.toString(),
            drop_latitude = drop_latitude.toString(),
            drop_longitude = drop_longitude.toString(),
            distance = distance.toString(),
            distance_unit = dis_unit.toString(),
            pickup_date = selectedDate.toString(),
            pickup_time = time.toString(),
            pickup_time_unit = time_unit.toString(),
            trip_mode = trip_mode.toString(),
            vehicle_cat_id = vehicle_cat_id.toString(),
            round_pickup_location = drop_location.toString(),
            round_pickup_latitude = drop_latitude.toString(),
            round_pickup_longitude = drop_longitude.toString(),
            round_drop_location = pickup_location.toString(),
            round_drop_latitude = pickup_latitude.toString(),
            round_drop_longitude = pickup_longitude.toString(),
            round_pickup_date = round_pickup_date.toString(),
            round_pickup_time = round_pickup_time.toString(),
            round_pickup_time_unit = round_pickup_time_unit.toString(),
            pickup_location_title = pickup_location_title.toString(),
            drop_location_title = drop_location_title.toString(),
            approx_total_time = duration_to_travel,
            time_difference = time_difference
        )
    }

    fun apisObservers() {
        viewModels.vehicleCategoryResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is BusCategoryResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                vehicleCategoryList.clear()
                                data.data?.let { it1 -> vehicleCategoryList.addAll(it1) }
                                busCategoryAdapter.notifyDataSetChanged()
                                binding.btSelectTimeDate.visibility = View.GONE
                                binding.cardPickupData.visibility = View.VISIBLE
                                binding.tvChangeDate.visibility = View.VISIBLE
                                binding.llPickUpAddress.visibility = View.VISIBLE
                                binding.llPickupTimes.visibility = View.VISIBLE
                                binding.tvPickupDate.text =
                                    getString(R.string.date) + " " + selectedDate
                                binding.tvPickupTime.text =
                                    getString(R.string.time) + " " + selectedTime
                                binding.llRecommendedBuses.visibility = View.VISIBLE
                                binding.btBookRide.visibility = View.VISIBLE

                                if (trip_mode == 0) {
                                    binding.llDesAddress.visibility = View.GONE
                                    binding.llDesTimes.visibility = View.GONE
                                }
                                if (trip_mode == 1) {
                                    binding.llDesAddress.visibility = View.VISIBLE
                                    binding.llDesTimes.visibility = View.VISIBLE
                                    binding.tvDesTime.text =
                                        getString(R.string.time) + " " + roundTime
                                    binding.tvDesDate.text =
                                        getString(R.string.date) + " " + round_pickup_date
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
        viewModels.createBookingResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CreateBookingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                // showToast(data.message)
                                bookingDataList.clear()
                                data.data?.let { it1 -> bookingDataList.addAll(it1) }
                                var bundle = Bundle()
                                bundle.putParcelableArrayList(
                                    AppConstants.BOOKING_DATA,
                                    bookingDataList
                                )
                                launchActivity(
                                    CheckoutActivity::class.java, removeAllPrevActivities = false,
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
        viewModels.notesListingResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is NotesResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.rvNotes.layoutManager =
                                    LinearLayoutManager(this@SelectRideWithTypeActivity)
                                notesAdapter =
                                    NotesAdapter(this@SelectRideWithTypeActivity, data.data)
                                binding.rvNotes.adapter = notesAdapter

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
                        /*if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }*/
                    }
                }
            }
        })
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"

        val key = "key=${getString(R.string.google_search_key)}"
        val parameters = "$strOrigin&$strDest&$key"
        //val parameters = "$strOrigin&$strDest&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
    }

    private fun fetchTravelTime(
        pickupLocation: LatLng,
        dropLocation: LatLng,
        onResult: (String?) -> Unit // Callback to handle the duration result
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = getDirectionsUrl(pickupLocation, dropLocation)
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val durationText = responseData?.let { extractTravelTime(it) }
                withContext(Dispatchers.Main) {
                    onResult(durationText) // Pass the travel time to the callback
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(null) // Handle unsuccessful response
                }
            }
        }
    }

    private fun extractTravelTime(response: String): String? {
        return try {
            val jsonResponse = JSONObject(response)

            // Check status
            if (jsonResponse.optString("status") != "OK") {
                Log.e("ParseError", "API status not OK")
                return null
            }

            // Extract the duration from the first leg of the first route
            val routes = jsonResponse.optJSONArray("routes")
            val legs = routes?.getJSONObject(0)?.optJSONArray("legs")
            val durationObject = legs?.getJSONObject(0)?.optJSONObject("duration")

            durationObject?.optString("text") // e.g., "15 mins"
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("ParseError", "Failed to parse JSON: ${e.message}")
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateTimeDifference(
        pickdate: String,
        picktime: String,
        picktimeUnit: String,
        rounddate: String,
        roundtime: String,
        roundtimeUnit: String
    ) {
        // Input values
        val date1 = pickdate
        val time1 = picktime + " " + picktimeUnit  // Start time

        val date2 = rounddate
        val time2 = roundtime + " " + roundtimeUnit

        // Parse the dates
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val startDate = LocalDate.parse(date1, dateFormatter)
        val endDate = LocalDate.parse(date2, dateFormatter)

        val startTime = LocalTime.parse(time1, timeFormatter)
        val endTime = LocalTime.parse(time2, timeFormatter)

        // Combine into LocalDateTime
        val startDateTime = LocalDateTime.of(startDate, startTime)
        val endDateTime = LocalDateTime.of(endDate, endTime)

        // Calculate the difference
        val duration = ChronoUnit.HOURS.between(startDateTime, endDateTime)
        val minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime) % 60

        time_difference = "$duration:$minutes"

        // Print the result
        Log.d("TIME_ES", "Difference: $duration:$minutes ")
    }


}