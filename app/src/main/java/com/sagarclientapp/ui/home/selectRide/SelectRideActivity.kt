package com.sagarclientapp.ui.home.selectRide

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivitySelectRideBinding
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LastSearchesLocations
import com.sagarclientapp.model.LocationCategory
import com.sagarclientapp.model.LocationCategoryResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.home.selectRide.viewModels.SelectRideViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModelProvider
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SelectRideActivity : BaseActivity() {
    var bundle: Bundle? = null
    var page: String? = null
    lateinit var binding: ActivitySelectRideBinding
    lateinit var adapter: PlacesAdapterForDefault
    lateinit var lastSearchLocationsAdapter: LastSearchLocationsAdapter
    var locationCategoryList = mutableListOf<LocationCategory>()
    var lastSearchedLocationList = mutableListOf<LastSearchesLocations>()
    var sourceLat: Double? = null
    var sourceLong: Double? = null
    var desLat: Double? = null
    var distance: Double? = null
    var roundedDistance: String? = null

    var desLong: Double? = null
    var sourceAddress: String? = null
    var desAddress: String? = null
    var desTitle: String? = null
    var type_check: String? = null
    var need_get_current: String = "false"
    var need_to_get_current_location: String? =null
    var sourceTitle: String? = null
    var location_id: Int? = null
    private var focusedTextView: Int? = null // 0 for current, 1 for destination
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    //  lateinit var sharedViewModel: SharedViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModels: SelectRideViewModel by viewModels<SelectRideViewModel>()

    // private val sharedViewModel: SharedViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by lazy {
        SharedViewModelProvider.getSharedViewModel(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)


        initUis()
        apisObservers()
    }

    private fun initUis() = binding.apply {
        bundle = intent.extras

        if (bundle != null) {

            page = bundle?.getString("where")
            need_to_get_current_location = bundle?.getString("need_to_get_current_location")

        }
        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@SelectRideActivity)

        // Enable the location layer if permission is granted
        if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)

        } else {
           /* if (page.equals("destination") && need_to_get_current_location.equals("true")) {
                showToast("inside current not")
                getCurrentLocation()
            }*/


            if (page.equals("home"))
            {
               // showToast("inside current not")
                getCurrentLocation()
            }
            else if (page.equals("destination") && need_to_get_current_location.equals("true")){
                //showToast("inside current not")
                getCurrentLocation()
            }



        }


        /*if (page.equals("current")) {
            tvCurrentLocation.text =
                getString(R.string.phase_4_sheikh_zayed_bin_sultan_al_nahyan_rd_avenues_mall_92000)
        } else {
            tvCurrentLocation.text = getString(R.string.current_location)
        }
        if (page.equals("destination")) {
            tvDestination.text =
                getString(R.string.phase_4_sheikh_zayed_bin_sultan_al_nahyan_rd_avenues_mall_92000)

        } else {
            tvDestination.text = getString(R.string.destination)

        }*/

        rvPlacesListForSearch.layoutManager =
            LinearLayoutManager(this@SelectRideActivity, LinearLayoutManager.HORIZONTAL, false)
        adapter = PlacesAdapterForDefault(
            this@SelectRideActivity,
            locationCategoryList,
            object : PlacesAdapterForDefault.OnItemClickListener {
                override fun onItemClick(id: Int?) {
                    getLocationListByCategoryIdApi(id)
                }

                override fun onLastSearchClick() {
                    getLastSearchedLoactionApi()
                }

            })

        rvPlacesListForSearch.adapter = adapter

        rvPlacesData.layoutManager =
            LinearLayoutManager(this@SelectRideActivity)
        lastSearchLocationsAdapter = LastSearchLocationsAdapter(
            this@SelectRideActivity,
            lastSearchedLocationList,
            object : LastSearchLocationsAdapter.OnItemClickListener {
                override fun onItemClick(lastSearchesLocations: LastSearchesLocations) {
                    addToWishListApi(lastSearchesLocations)
                }

                override fun onAddressClick(lastSearchesLocations: LastSearchesLocations) {
                    /*if (sourceAddress == null && desAddress == null) {

                        // Set address for current location
                        binding.tvCurrentLocation.text = lastSearchesLocations.address
                        // You can also update any other related data here
                        sharedViewModel.currentLocation.value = LatLng(
                            lastSearchesLocations.lat!!.toDouble(),
                            lastSearchesLocations.jsonMemberLong!!.toDouble()
                        )
                        sharedViewModel.currentAddress.value = lastSearchesLocations.address
                        sharedViewModel.currentName.value = lastSearchesLocations.title
                    } else if (sourceAddress != null && desAddress == null) {

                        // Set address for destination
                        binding.tvDestination.text = lastSearchesLocations.address
                        // You can also update any other related data here
                        sharedViewModel.destinationLocation.value = LatLng(
                            lastSearchesLocations.lat!!.toDouble(),
                            lastSearchesLocations.jsonMemberLong!!.toDouble()
                        )
                        sharedViewModel.destinationAddress.value = lastSearchesLocations.address
                        sharedViewModel.destinationName.value = lastSearchesLocations.title
                    } else if (sourceAddress != null && desAddress != null) {
*/
                     binding.tvDestination.text = lastSearchesLocations.address
                   // binding.tvDestination.text = lastSearchesLocations.title
                    // You can also update any other related data here
                    sharedViewModel.destinationLocation.value = LatLng(
                        lastSearchesLocations.lat!!.toDouble(),
                        lastSearchesLocations.jsonMemberLong!!.toDouble()
                    )
                    sharedViewModel.destinationAddress.value = lastSearchesLocations.address
                    sharedViewModel.destinationName.value = lastSearchesLocations.title
                    // }


                }
            }
        )
        rvPlacesData.adapter = lastSearchLocationsAdapter
        binding.tvCurrentLocation.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                focusedTextView = 0 // 0 for current
            }
            false // Return false to allow the touch event to be processed by other listeners
        }

        binding.tvDestination.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                focusedTextView = 1 // 1 for destination
            }
            false // Return false to allow the touch event to be processed by other listeners
        }

        tvCurrentLocation.setOnClickListener {
            if (!hasLocationPermission()) {
                requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)

            } else {
                var current_text_value=binding.tvCurrentLocation.text.toString()

                if (current_text_value.equals("Current Location"))
                {
                    need_get_current = "true"
                }
                focusedTextView = 0
                val bundle = Bundle()
                bundle.putString("where", "current")
                bundle.putString("need_get_current", need_get_current)

                launchActivity(
                    MapScreenActivity::class.java, removeOnlyThisActivity = false,
                    bundle = bundle
                )
            }

        }
        tvDestination.setOnClickListener {
            if (!hasLocationPermission()) {
                requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)

            } else {
                focusedTextView = 1
                val bundle = Bundle()
                bundle.putString("where", "destination")
                launchActivity(
                    MapScreenActivity::class.java,
                    removeOnlyThisActivity = false,
                    bundle = bundle
                )
            }

        }
        btSelectRide.setOnClickListener {
            if (sourceAddress.equals(desAddress)) {
                showToast(getString(R.string.your_both_address_should_not_be_same))
            } else {
                var distance_with_unit = tvTotalDistance.text.toString()
                val distanceParts = distance_with_unit.split(" ")
                // Time part (e.g., "02:30")
                var dis = distanceParts[0]
                // Period part (e.g., "PM")
                var dis_unit = distanceParts[1]

                val bundle = Bundle()
                bundle.putString("distance", dis)
                bundle.putString("dis_unit", dis_unit)
                launchActivity(
                    SelectRideWithTypeActivity::class.java, removeAllPrevActivities = false,
                    bundle = bundle
                )
            }

        }
        ivBack.setOnClickListener {
            launchActivity(
                HomeActivity::class.java, removeAllPrevActivities = true,
                bundle = bundle
            )
        }
        shuffle.setOnClickListener {
            Log.d("SWAP ", "sourceAddress" + sourceAddress)
            Log.d("SWAP ", "desAddress" + desAddress)

            if (sourceAddress != null && desAddress != null) {
                Log.d("SWAP ", "sourceAddress" + sourceAddress)
                swapLocations()
            }

        }
        if (page?.equals("home") == true) {

            val click_search = bundle?.getString("onSearchedItem")
            if (click_search.equals("true")) {
                val address = bundle?.getString("address")
                val title = bundle?.getString("title")
                val lat = bundle?.getString("lat")?.toDouble()
                val lng = bundle?.getString("long")?.toDouble()

                if (lat != null && lng != null) {
                    sharedViewModel.destinationLocation.value =
                        LatLng(lat, lng)
                } else {
                    sharedViewModel.destinationLocation.value = null

                }
                sharedViewModel.destinationAddress.value = address
                sharedViewModel.destinationName.value = title
            } else {
                sourceAddress = null
                sourceTitle = null
                sourceLat = null
                sourceLong = null
                desAddress = null
                desTitle = null
                desLat = null
                desLong = null
                /*if (sourceLat != null && sourceLong != null) {
                sharedViewModel.currentLocation.value = LatLng(sourceLat!!, sourceLong!!)

            } else {
                sharedViewModel.currentLocation.value = null

            }
            sharedViewModel.currentAddress.value = sourceAddress
            sharedViewModel.currentName.value = sourceTitle*/
                if (desLat != null && desLong != null) {
                    sharedViewModel.destinationLocation.value = LatLng(desLat!!, desLong!!)


                } else {
                    sharedViewModel.destinationLocation.value = null

                }
                sharedViewModel.destinationAddress.value = desAddress
                sharedViewModel.destinationName.value = desTitle
            }

        }


        sharedViewModel.currentLocation.observe(this@SelectRideActivity, Observer { latLng ->
            // Update current location UI
            Log.d("LOCA", "current lat")
            if (latLng != null) {
                sourceLat = latLng.latitude
                sourceLong = latLng.longitude
            }
            // getDistanceFromCalculate()

            if (sourceLat != null && sourceLong != null && desLat != null && desLong != null) {

                /*var distance = calculateDistance(sourceLat!!, sourceLong!!, desLat!!, desLong!!)
                val roundedDistance = String.format("%.1f", distance)
                tvTotalDistance.text = roundedDistance + " km"*/
                val origin = "$sourceLat,$sourceLong"
                val destination = "$desLat,$desLong"

                getLocationDistanceFromPointsApi(origin, destination)

            }

        })

        sharedViewModel.destinationLocation.observe(this@SelectRideActivity, Observer { latLng ->
            // Update destination location UI
            Log.d("LOCA", "des lat")
            if (latLng != null) {
                desLat = latLng.latitude
                desLong = latLng.longitude
            }

            // getDistanceFromCalculate()
            if (sourceLat != null && sourceLong != null && desLat != null && desLong != null) {

                /*var distance = calculateDistance(sourceLat!!, sourceLong!!, desLat!!, desLong!!)
                val roundedDistance = String.format("%.1f", distance)
                tvTotalDistance.text = roundedDistance + " km"*/
                val origin = "$sourceLat,$sourceLong"
                val destination = "$desLat,$desLong"

                getLocationDistanceFromPointsApi(origin, destination)

            }
        })
        sharedViewModel.destinationAddress.observe(this@SelectRideActivity, Observer { address ->
           // desAddress = address
            // Update destination address UI
             focusedTextView = 1

             desAddress = address
             if (address == null) {
                 tvDestination.text = getString(R.string.destination)

             } else {
                 tvDestination.text = address

             }
            //tvDestination.text = address
        })

        sharedViewModel.currentAddress.observe(this@SelectRideActivity, Observer { address ->
            // Update current address UI
            focusedTextView = 0
            Log.d("LOCA", "current In select Ride" + address)

            if (address == null || page?.equals("home") == true) {

                tvCurrentLocation.text = getString(R.string.current_location)
            } else {
                tvCurrentLocation.text = address

            }


            //tvCurrentLocation.text = address
            sourceAddress = address
        })
        sharedViewModel.currentName.observe(this@SelectRideActivity, Observer { name ->
            // Update current address UI

            sourceTitle = name

            /*focusedTextView = 0
            Log.d("LOCA", "current In select Ride" + name)

            if (name == null || page?.equals("home") == true) {

                tvCurrentLocation.text = getString(R.string.current_location)
            } else {
                tvCurrentLocation.text = name

            }*/
        })
        sharedViewModel.destinationName.observe(this@SelectRideActivity, Observer { name ->
            // Update current address UI

            desTitle = name

            /*focusedTextView = 1


            if (name == null) {
                tvDestination.text = getString(R.string.destination)

            } else {
                tvDestination.text = name

            }*/
        })



        getLocationCategoryApi()
        getLastSearchedLoactionApi()
    }

    private fun getDistanceFromCalculate() {
        if (sourceLat != null && sourceLong != null && desLat != null && desLong != null) {

            distance = calculateDistance(sourceLat!!, sourceLong!!, desLat!!, desLong!!)
            roundedDistance = String.format("%.1f", distance)
            binding.tvTotalDistance.text = roundedDistance + " km"
            binding.btSelectRide.setBackgroundResource(R.drawable.solid_theme_blue)
            binding.btSelectRide.isEnabled = true

            /* val origin = "$sourceLat,$sourceLong"
             val destination = "$desLat,$desLong"

             getLocationDistanceFromPointsApi(origin,destination)*/

        } else {
            binding.btSelectRide.setBackgroundResource(R.drawable.solid_disable_button)
            binding.btSelectRide.isEnabled = false
        }
    }

    fun getLocationCategoryApi() {
        showDialog()
        viewModels.getLocationCategory(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )
    }

    fun getLocationListByCategoryIdApi(id: Int?) {
        showDialog()
        viewModels.getLocationListByCategoryId(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            id.toString()
        )
    }

    fun getLocationDistanceFromPointsApi(origin: String, destination: String) {
        showDialog()
        viewModels.getDistanceWithPoints(
            origin, destination, getString(R.string.google_search_key)
        )
    }

    fun getLastSearchedLoactionApi() {
        showDialog()
        viewModels.getLastSearchedLocationList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )
    }

    fun addToWishListApi(lastSearchesLocations: LastSearchesLocations) {
        // showDialog()

        location_id = lastSearchesLocations.id

        var wishListFlag = if (lastSearchesLocations.wishlistFlag == 0) 1 else 0

        viewModels.addToWishList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            lastSearchesLocations.id.toString(),
            wishListFlag.toString()
        )
    }

    fun apisObservers() {
        viewModels.getLocationCategoryResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LocationCategoryResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                locationCategoryList.clear()
                                locationCategoryList.add(
                                    0,
                                    LocationCategory("", "", "", "", 1, 1, "")
                                )
                                locationCategoryList.addAll(data.data)
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
        viewModels.lastSearchedLocationListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LastSearchLocationsResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                lastSearchedLocationList.clear()

                                if (data.data?.size!! > 0) {
                                    binding.tvNoLocation.visibility = View.GONE

                                    data.data?.let { it1 -> lastSearchedLocationList.addAll(it1) }
                                } else {
                                    binding.tvNoLocation.visibility = View.VISIBLE
                                    binding.tvNoLocation.text =
                                        getString(R.string.no_last_search_records_found)
                                }
                                lastSearchLocationsAdapter.notifyDataSetChanged()

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
        viewModels.locationListByCategoryIdResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LastSearchLocationsResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                lastSearchedLocationList.clear()
                                if (data.data?.size!! > 0) {

                                    binding.tvNoLocation.visibility = View.GONE


                                    data.data?.let { it1 -> lastSearchedLocationList.addAll(it1) }
                                } else {
                                    binding.tvNoLocation.visibility = View.VISIBLE
                                    binding.tvNoLocation.text =
                                        getString(R.string.no_location_found)
                                }
                                lastSearchLocationsAdapter.notifyDataSetChanged()

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
        viewModels.addToWishListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                var updatedLocation = lastSearchedLocationList.find { location ->
                                    location.id == location_id
                                }
                                updatedLocation?.wishlistFlag =
                                    if (updatedLocation?.wishlistFlag == 0) 1 else 0

                                // Notify the adapter of the change
                                lastSearchLocationsAdapter.notifyDataSetChanged()

                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    //hideDialog()

                }

                is Response.Loading -> {
                    try {
                        // showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    // hideDialog()
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
        viewModels.getDistanceResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {

                    binding.btSelectRide.setBackgroundResource(R.drawable.solid_theme_blue)
                    binding.btSelectRide.isEnabled = true
                    val directionsResponse = it.responseData
                    val distance =
                        directionsResponse?.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.text
                    /*  if (page.equals("home")) {
                          binding.tvTotalDistance.text = "0 km"
                      } else {*/
                    if (distance != null) {
                        binding.tvTotalDistance.text = distance
                    } else {
                        binding.tvTotalDistance.text = "0 km"
                    }
                    //}

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

    fun swapLocations() {
        val tempLat = sourceLat
        val tempLong = sourceLong
        val tempAddress = sourceAddress
        val tempTitle = sourceTitle

        sourceLat = desLat
        sourceLong = desLong
        sourceAddress = desAddress
        sourceTitle = desTitle

        desLat = tempLat
        desLong = tempLong
        desAddress = tempAddress
        desTitle = tempTitle
        Log.d("SWAP ", "sourceAddress" + sourceAddress)
        Log.d("SWAP ", "des" + desAddress)

        sharedViewModel.currentLocation.value = LatLng(sourceLat!!, sourceLong!!)
        sharedViewModel.currentAddress.value = sourceAddress
        sharedViewModel.currentName.value = sourceTitle

        sharedViewModel.destinationLocation.value = LatLng(desLat!!, desLong!!)
        sharedViewModel.destinationAddress.value = desAddress
        sharedViewModel.destinationName.value = desTitle


        updateUi()
    }

    private fun updateUi() {
        Log.d("SWAP ", "des" + desAddress)
        Log.d("SWAP ", "sou" + desAddress)

        binding.tvCurrentLocation.text = sourceAddress
        binding.tvDestination.text = desAddress
    }

    /* override fun onRequestPermissionsResult(
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
                     getCurrentLocation()

                 } else {
                     // Permission denied, show a message to the user
                     Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                 }
                 return
             }

             else -> {
                 // Handle other permissions if necessary
             }
         }
     }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with getting the current location
                    getCurrentLocation()
                } else {
                    // Permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        // Show rationale dialog again
                        showPermissionRationaleDialog(LOCATION_PERMISSION_REQUEST_CODE)
                    } else {
                        // "Don't ask again" was selected, guide the user to settings
                        showSettingsDialog()
                    }
                }
            }

            else -> {
                // Handle other permissions if necessary
            }
        }
    }


    private fun getCurrentLocation() {
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
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val lat = it.latitude
                    val lng = it.longitude
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    sharedViewModel.currentLocation.value = LatLng(lat, lng)


                    // country_code= addresses?.get(0)?.countryCode
                    if (addresses!!.isNotEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0)
                        val name = address.featureName
                        sharedViewModel.currentAddress.value = addressText
                        sharedViewModel.currentName.value = name

                    }

                    // Set the latitude, longitude, and address to the TextView
                    //locationTextView.text = "Latitude: $lat, Longitude: $lng\nAddress: $address"
                }
            }
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.location_permission_required))
            .setMessage(getString(R.string.you_have_denied_location_permission_please_go_to_the_app_settings_and_enable_it_manually))
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}