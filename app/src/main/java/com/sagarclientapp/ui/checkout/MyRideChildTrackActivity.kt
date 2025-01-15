package com.sagarclientapp.ui.checkout

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.sagarclientapp.R
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityMyRideBinding
import com.sagarclientapp.databinding.ActivityMyRideChildTrackBinding
import com.sagarclientapp.ui.checkout.viewModels.MyRideChildViewModel
import com.sagarclientapp.ui.checkout.viewModels.MyRideViewModel
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.DriverStatusResponse
import com.sagarclientapp.model.SchoolDriverStatusResponse
import com.sagarclientapp.utils.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


@AndroidEntryPoint
class MyRideChildTrackActivity : BaseActivity(), OnMapReadyCallback {
    val kuwaitBounds = LatLngBounds(
        LatLng(28.5246, 46.5527), // Southwest corner
        LatLng(30.0935, 48.4326)  // Northeast corner
    )
    lateinit var binding: ActivityMyRideChildTrackBinding
    private lateinit var mMap: GoogleMap

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var userMarker: Marker
  //  private lateinit var driverMarker: Marker
    private var driverMarker: Marker? = null
    private lateinit var polyline: Polyline
    private val driverPathPoints = mutableListOf<LatLng>()
    private lateinit var driverPolyline: Polyline
    private val client = OkHttpClient()
    var bundle: Bundle? = null
    var ride_id: String? = null
    var child_id: String? = null
    var driverLatLng: LatLng? = null  // San Francisco
    var pickupLatLng: LatLng? = null  // San Francisco

    private val viewModels: MyRideChildViewModel by viewModels<MyRideChildViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyRideChildTrackBinding.inflate(layoutInflater)

        setContentView(binding.root)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        initUis()
    }

    private fun initUis() = binding.apply {

        bundle = intent.extras

        if (bundle != null) {

            child_id = bundle?.getString("child_id")
            ride_id = bundle?.getString("ride_id")

        }


        // Initialize the fused location client
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@MyRideChildTrackActivity)

        // Initialize the map fragment
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MyRideChildTrackActivity)

        apiObserver()


    }

    // Start tracking the driver
    fun startTracking() {
        handler.post(object : Runnable {
            override fun run() {
                driverStatusApi() // Fetch driver location and status
                handler.postDelayed(this, 3000) // Repeat every 3 seconds
            }
        })
    }

    fun driverStatusApi() {
        viewModels.childDriverStatus(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            ride_id.toString(), child_id.toString()
        )
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the location layer if permission is granted
        if (!hasLocationPermission()) {
            requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
      // mMap.setLatLngBoundsForCameraTarget(kuwaitBounds)
       // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(kuwaitBounds, 100))

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                //mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                driverMarker = mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Current Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))

                )!!
                //updateLocationInfo(currentLatLng)
            }
        }

        mMap.isMyLocationEnabled = true
        startTracking()


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
                    mapFragment.getMapAsync(this@MyRideChildTrackActivity)
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
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

    fun apiObserver() {
        viewModels.driverStatusResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SchoolDriverStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (!data.data.isNullOrEmpty()) {
                                    var driverLat =
                                        data.data?.get(0)?.currentLocationLatitude?.toDouble()
                                    var driverLng =
                                        data.data?.get(0)?.currentLocationLongitude?.toDouble()
                                    var pickupLat =
                                        data.data?.get(0)?.endLatitude?.toDouble()
                                    var pickupLng =
                                        data.data?.get(0)?.endLongitude?.toDouble()
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

                                    Glide.with(this)
                                        .load(
                                            AppConstants.BASE_URL_IMAGE + "" + data.data?.get(0)?.schoolTripChildrens?.get(
                                                0
                                            )?.user?.get(0)?.avatar
                                        )
                                        .placeholder(R.drawable.mask)
                                        .into(binding.ivChildImage1)
                                    binding.tvChildName.text =
                                        getString(R.string.hello) + " " + data.data?.get(0)?.schoolTripChildrens?.get(
                                            0
                                        )?.user?.get(0)?.name
                                    binding.tvCurrentLocation1.text =
                                        data.data?.get(0)?.startLocationTitle
                                    binding.tvDestination1.text =
                                        data.data?.get(0)?.endLocationTitle
                                    updateDriverMarker(driverLatLng!!)
                                    animateCamera(driverLatLng!!)
                                   // drawRoute(driverLatLng!!, pickupLatLng!!)
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

                        }
                        else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }
                }
            }
        })

    }

   /* private fun updateDriverMarker(location: LatLng) {
        if (driverMarker == null) {
            driverMarker = mMap.addMarker(MarkerOptions().position(location).title("Driver")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)))!!
        } else {
            driverMarker?.position = location
        }
    }*/

    // version one fro child tracking
   private fun updateDriverMarker(location: LatLng) {
       // Remove the existing marker if it exists
       driverMarker?.remove()

       // Add a new marker at the updated location
       driverMarker = mMap.addMarker(
           MarkerOptions()
               .position(location)
               .title("Driver")
               .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
       )
   }


   /* private fun updateDriverMarker(newLocation: LatLng) {
        if (driverMarker == null) {
            // Add the marker if it doesn't already exist
            driverMarker = mMap.addMarker(
                MarkerOptions()
                    .position(newLocation)
                    .title("Driver")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
            )
        } else {
            // Animate marker movement
            val startLatLng = driverMarker?.position
            val handler = Handler(Looper.getMainLooper())
            val startTime = SystemClock.uptimeMillis()
            val duration = 1000 // Duration of the animation in milliseconds

            val interpolator = LinearInterpolator()

            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - startTime
                    val t = (elapsed / duration.toFloat()).coerceAtMost(1f)
                    val interpolatedT = interpolator.getInterpolation(t)

                    val newLat = interpolatedT * newLocation.latitude + (1 - interpolatedT) * (startLatLng?.latitude ?: newLocation.latitude)
                    val newLng = interpolatedT * newLocation.longitude + (1 - interpolatedT) * (startLatLng?.longitude ?: newLocation.longitude)

                    driverMarker?.position = LatLng(newLat, newLng)

                    if (t < 1f) {
                        handler.postDelayed(this, 16) // Re-post to achieve smooth movement
                    }
                }
            })
        }
    }*/






    // Animate the camera to the driver's position
    private fun animateCamera(location: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    // Draw route using Directions API
    private fun drawRoute(start: LatLng, end: LatLng) {
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
    }

    private fun parseDirections(response: String): List<LatLng> {
        Log.d("DirectionsResponse", response)

        val result = mutableListOf<LatLng>()
        try {
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")

            if (routes.length() == 0) {
                // No routes found
                runOnUiThread {
                    Toast.makeText(this, getString(R.string.no_routes_found), Toast.LENGTH_SHORT).show()
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
    private fun drawPolyline(points: List<LatLng>) {
        val polylineOptions = PolylineOptions().apply {
            color(Color.BLUE)
            width(10f)
            addAll(points)
        }
        mMap.addPolyline(polylineOptions)
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"

        val key = "key=${getString(R.string.google_search_key)}"
        val parameters = "$strOrigin&$strDest&$key"
        //val parameters = "$strOrigin&$strDest&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
    }


}