package com.sagarclientapp.ui.home.selectRide

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityMapScreenBinding
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.home.selectRide.viewModels.MapScreenViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModel
import com.sagarclientapp.ui.home.selectRide.viewModels.SharedViewModelProvider
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale


@AndroidEntryPoint
class MapScreenActivity : BaseActivity(), OnMapReadyCallback {
    var bundle: Bundle? = null
    var page: String? = null
    var need_get_current: String? = null
    var country_code: String? = null
    var first: Boolean? = false
    var latitude: Double = 0.0
    var longitute: Double = 0.0
    val kuwaitBounds = LatLngBounds(
        LatLng(28.5246, 46.5527), // Southwest corner
        LatLng(30.0935, 48.4326)  // Northeast corner
    )
    private var isManualSelection = false
    lateinit var binding: ActivityMapScreenBinding
    private lateinit var placesClient: PlacesClient
    private var predictionsList = mutableListOf<AutocompletePrediction>()
    private lateinit var placesAdapter: PlacesDataAdapter
    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    var bottomSheetBehaviorLocationConfirm: BottomSheetBehavior<LinearLayout>? = null
    var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null


    private val viewModels: MapScreenViewModel by viewModels<MapScreenViewModel>()

    /*private val sharedViewModel: SharedViewModel by viewModels()

*/
    private val sharedViewModel: SharedViewModel by lazy {
        SharedViewModelProvider.getSharedViewModel(this)
    }

    //lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMapScreenBinding.inflate(layoutInflater)
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

            page = bundle?.getString("where")
            need_get_current = bundle?.getString("need_get_current")

        }

        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MapScreenActivity)

        // Initialize the map fragment
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapScreenActivity)

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(this@MapScreenActivity, getString(R.string.google_search_key))
        }

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this@MapScreenActivity)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehaviorLocationConfirm = BottomSheetBehavior.from(bottomSheetLocationConfirm)
        // app:behavior_hideable="false"
        // Show the Bottom Sheet
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehaviorLocationConfirm!!.setHideable(true);
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehaviorLocationConfirm!!.state = BottomSheetBehavior.STATE_HIDDEN


        rvLastSearch.layoutManager = LinearLayoutManager(this@MapScreenActivity)
        placesAdapter = PlacesDataAdapter(this@MapScreenActivity, predictionsList,
            object : PlacesDataAdapter.OnItemClickListener {
                override fun onItemClick(place: Place) {
                    isManualSelection = true
                    val latLng = place.latLng
                    latLng?.let {

                        // Move the camera to the selected place
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                15f
                            )
                        ) // Zoom level 15

                        // Update marker position to the selected place
                        if (currentMarker == null) {
                            currentMarker = mMap.addMarker(MarkerOptions().position(latLng))
                        } else {
                            currentMarker?.position = latLng
                        }

                        latitude = it.latitude
                        longitute = it.longitude
                        // Use latitude and longitude as needed
                    }
                    Log.d(
                        "SELECTED_PLACE",
                        "ID: ${place.id}, Name: ${place.name}, Address: ${place.address}, LatLng: ${place.latLng}"
                    )

                    val address = place.address
                    val placeName = place.name
                    binding.tvLocationName.text = place.name
                    binding.tvLocationAddress.text = place.address
                    keyBoardHide(this@MapScreenActivity)
                    /* bottomSheetBehavior.setPeekHeight(0)
                     bottomSheetBehavior.setHideable(true);*/
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    //bottomSheetBehaviorLocationConfirm!!.isDraggable =false

                    bottomSheetBehaviorLocationConfirm!!.setHideable(false);
                    bottomSheetBehaviorLocationConfirm!!.state =
                        BottomSheetBehavior.STATE_EXPANDED
                    //bottomSheetBehaviorLocationConfirm!!.setPeekHeight(500)
                    bottomSheetBehaviorLocationConfirm!!.isDraggable = false
                }

            }, country_code
        )
        rvLastSearch.adapter = placesAdapter


        btConfirmLocation.setOnClickListener {
            addLastSearchLocationApi()

        }
        ivBack.setOnClickListener {
            finish()
        }

        binding.etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString()
                if (!hasLocationPermission()) {
                    requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)

                } else {
                    if (query.isNotEmpty()) {
                        performAutocomplete(query)
                    } else {
                        predictionsList.clear()
                        placesAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        // Set bottom sheet behavior callback
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Handle state changes
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                bottomSheetBehavior.peekHeight =
                    screenHeight / 2 // Set peek height to half of the screen height
                // Optional: you can respond to slide events here if needed
            }
        })


        // Ensure the button is always fully visible
        bottomSheetBehaviorLocationConfirm?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Ensure full button visibility when expanded
                    bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Optionally handle sliding events

            }
        })

        apisObservers()


    }


    // Disable drag when camera is moving
    fun disableBottomSheetDrag() {
        bottomSheetBehaviorLocationConfirm?.isDraggable = false
    }

    // Enable drag when camera stops
    fun enableBottomSheetDrag() {
        bottomSheetBehaviorLocationConfirm?.isDraggable = true
    }


    private fun performAutocomplete(query: String) {
        val token = AutocompleteSessionToken.newInstance()
        // val countryCode = getCurrentCountryCode(latitude, longitute) // Fallback to default 'US'
// Provide a fallback for country code
        val countryCode = if (latitude != null && longitute != null) {
            getCurrentCountryCode(latitude!!, longitute!!)
        } else {
            "KW" // Default country code
        }
        val request = FindAutocompletePredictionsRequest.builder()
            .setCountries(listOf(countryCode))
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            predictionsList.clear()
            predictionsList.addAll(predictions)
            placesAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }

    /* fun getCurrentCountryCode(latitude: Double, longitude: Double): String? {
         val geocoder = Geocoder(this, Locale.getDefault())
         return try {
             val addresses = geocoder.getFromLocation(latitude, longitude, 1)
             if (addresses != null && addresses.isNotEmpty()) {
                 addresses[0].countryCode // This gives you the ISO country code (e.g., "US", "IN")
             } else {
                 null // Handle no address found
             }
         } catch (e: IOException) {
             e.printStackTrace()
             null // Handle the IOException case
         }
     }
 */
    private fun getCurrentCountryCode(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(this)
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.get(0)?.countryCode ?: "KW" // Default to Kuwait if null
        } catch (e: Exception) {
            "KW" // Default to Kuwait on any failure
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the location layer if permission is granted
        if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        //mMap.setLatLngBoundsForCameraTarget(kuwaitBounds)
       // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(kuwaitBounds, 100))

        // Set etSearchLocation to empty initially
        // binding.etSearchLocation.setText("")  // Keep it empty at the start

        // Get the current location of the device and set the position of the map

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                currentMarker = mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("")
                )
                // updateLocationInfo(currentLatLng,true)
                // Keep the bottom sheet expanded
                // bottomSheetBehaviorLocationConfirm!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        mMap.setOnCameraIdleListener {
            if (!isManualSelection) {
                // bottomSheetBehaviorLocationConfirm!!.state = BottomSheetBehavior.STATE_EXPANDED
                val cameraPosition = mMap.cameraPosition.target
                currentMarker?.position = cameraPosition
                // findViewById<TextView>(R.id.tvLocation).text = "Lat: ${cameraPosition.latitude}, Lng: ${cameraPosition.longitude}"
                updateLocationInfo(cameraPosition, false)
            } else {
                // Reset the flag after the manual selection has been handled
                isManualSelection = false
            }

        }
        mMap.setOnMapClickListener { latLng ->
            if (!isManualSelection) {
                // Move the marker to the clicked position
                currentMarker?.position = latLng


                // Optionally animate the camera to the clicked position
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                updateLocationInfo(latLng, false)
            }
            else {
                // Reset the flag after the manual selection has been handled
                isManualSelection = false
            }
            // Fetch and update location details for the clicked position

        }
    }

    private fun updateLocationInfo(latLng: LatLng, b: Boolean) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        latitude = latLng.latitude
        longitute = latLng.longitude
        if (addresses != null && addresses.isNotEmpty()) {
            country_code =
                addresses[0].countryCode // This gives you the ISO country code (e.g., "US", "IN")
        } else {
            null
        }
        // country_code= addresses?.get(0)?.countryCode
        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val addressText = address.getAddressLine(0)
            val name = address.featureName
            binding.tvLocationName.text = name
            binding.tvLocationAddress.text = addressText
            if (first == false) {
                binding.etSearchLocation.setText("")
                first = true

            } else {
                binding.etSearchLocation.setText(addressText)

            }
        }
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
                    mapFragment.getMapAsync(this@MapScreenActivity)
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
                    // Permission denied, show a message to the user
                    //Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
                // Handle other permissions if necessary
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

    fun addLastSearchLocationApi() {
        showDialog()
        viewModels.addLastLocationSearch(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            binding.tvLocationAddress.text.toString(),
            latitude.toString(), longitute.toString(),
            binding.tvLocationName.text.toString()
        )
    }

    fun apisObservers() {
        viewModels.addLastLocationSearchResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                var type = ""
                                Log.d("LOCA", page.toString())
                                if (page.equals("current", true)) {
                                    type = "current_selected"
                                    Log.d("LOCA", page.toString())
                                    Log.d("LOCA", LatLng(latitude, longitute).toString())
                                    Log.d("LOCA", binding.tvLocationAddress.text.toString())
                                    Log.d("LOCA", binding.tvLocationName.text.toString())
                                    sharedViewModel.currentLocation.value =
                                        LatLng(latitude, longitute)
                                    sharedViewModel.currentAddress.value =
                                        binding.tvLocationAddress.text.toString()
                                    sharedViewModel.currentName.value =
                                        binding.tvLocationName.text.toString()
                                }
                                if (page.equals("destination", true)) {
                                    type = "destination_selected"
                                    Log.d("LOCA", page.toString())
                                    sharedViewModel.destinationLocation.value =
                                        LatLng(latitude, longitute)
                                    sharedViewModel.destinationAddress.value =
                                        binding.tvLocationAddress.text.toString()
                                    sharedViewModel.destinationName.value =
                                        binding.tvLocationName.text.toString()
                                }
                                val bundle = Bundle()
                                bundle.putString("where", page)

                                bundle.putString("need_to_get_current_location", need_get_current)

                            launchActivity(
                                SelectRideActivity::class.java,
                                removeOnlyThisActivity = true,
                                bundle = bundle
                            )
                        }

                        else {
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

override fun onDestroy() {
    super.onDestroy()
    // placesClient.shutdown() // Call shutdown to prevent the memory leak
}




}