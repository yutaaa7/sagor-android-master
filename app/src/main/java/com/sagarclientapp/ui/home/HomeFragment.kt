package com.sagarclientapp.ui.home

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.databinding.FragmentHomeBinding
import com.sagarclientapp.model.Banner
import com.sagarclientapp.model.BannerResponse
import com.sagarclientapp.model.ChildListing
import com.sagarclientapp.model.ChildListingResponse
import com.sagarclientapp.model.GetProfileResponse
import com.sagarclientapp.model.LastSearchLocationsResponse
import com.sagarclientapp.model.LastSearchesLocations
import com.sagarclientapp.ui.LanguageSelectActivity
import com.sagarclientapp.ui.child.AddSchoolBusActivity
import com.sagarclientapp.ui.home.adapters.ChildListOnHomeAdapter
import com.sagarclientapp.ui.home.adapters.SearchItemsListAdapter
import com.sagarclientapp.ui.home.selectRide.SelectRideActivity
import com.sagarclientapp.ui.home.viewModels.HomeViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.CommonDialog
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val REQUEST_CHECK_SETTINGS = 1001
    private var isLocationDialogHandled = false
    private lateinit var dialog: Dialog

    lateinit var binding: FragmentHomeBinding
    lateinit var imageList: List<Int>
    var token: String? = null

    var bannersList = mutableListOf<Banner>()
    lateinit var viewPagerAdapter: ImageSliderViewPagerAdapter
    private val viewModels: HomeViewModel by viewModels<HomeViewModel>()
    var lastSearchedLocationList = mutableListOf<LastSearchesLocations>()
    var childList = mutableListOf<ChildListing>()
    lateinit var adapter: SearchItemsListAdapter
    lateinit var childListAdapter: ChildListOnHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragmentLifecycle", "onCreateView called")

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        //return inflater.inflate(R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUis()
    }


    private fun initUis() = binding.apply {
        dialog = Dialog(requireActivity())
        val packageManager = context?.packageManager // or this.packageManager in Activity
        val packageInfo =
            packageManager?.getPackageInfo("com.sagarclientapp", PackageManager.GET_SIGNATURES)
        if (packageInfo != null) {
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                val fingerprint = md.digest().joinToString(":") { String.format("%02X", it) }
                Log.d("SHA-256 Fingerprint", fingerprint)
            }
        }



        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location services are already enabled; proceed to the next screen
           // proceedToNextScreen()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the system dialog to enable location services
                    exception.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Handle the error
                   // proceedToNextScreen() // Proceed even if the dialog fails
                }
            } else {
                //proceedToNextScreen() // Proceed if the failure is not resolvable
            }
        }

       /* FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d("FCM", "Token: $token")
            updateFcmTokenApi()
        }*/
        imageList = ArrayList<Int>()
        imageList = imageList + R.drawable.banner
        imageList = imageList + R.drawable.banner
        imageList = imageList + R.drawable.banner

        if (SharedPref.readString(SharedPref.CHILD_ADD_SHOW).equals("false")) {

            addChild.cardChild.visibility = View.GONE
        } else {

            addChild.cardChild.visibility = View.VISIBLE
        }

        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.rlStartBooking.setBackgroundResource(R.drawable.home_bus)
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.rlStartBooking.setBackgroundResource(R.drawable.group_18153)

        }


        // on below line we are initializing our view
        // pager adapter and adding image list to it.
        viewPagerAdapter = ImageSliderViewPagerAdapter(requireActivity(), bannersList)
        homeViewPager.adapter = viewPagerAdapter

        rvLastSearch.layoutManager = LinearLayoutManager(requireActivity())
        adapter = SearchItemsListAdapter(
            requireActivity(),
            lastSearchedLocationList,
            object : SearchItemsListAdapter.onItemClickListener {
                override fun onClick(
                    address: String?,
                    title: String?,
                    lat: String?,
                    lng: String?
                ) {

                    val bundle = Bundle()
                    bundle.putString("where", "home")
                    bundle.putString("onSearchedItem", "true")
                    bundle.putString("address", address)
                    bundle.putString("title", title)
                    bundle.putString("lat", lat)
                    bundle.putString("long", lng)
                    requireActivity().launchActivity(
                        SelectRideActivity::class.java,
                        removeAllPrevActivities = false, bundle = bundle
                    )
                }

            })
        rvLastSearch.adapter = adapter

        rvChildList.layoutManager = LinearLayoutManager(requireActivity())
        childListAdapter = ChildListOnHomeAdapter(requireActivity(), childList)
        rvChildList.adapter = childListAdapter

        addChild.btNotAddChild.setOnClickListener {
            /* requireActivity().launchActivity(
                 MyRideChildTrackActivity::class.java,
                 removeAllPrevActivities = false
             )*/
            addChild.cardChild.visibility = View.GONE
            SharedPref.writeString(SharedPref.CHILD_ADD_SHOW, "false")
        }
        addChild.btProceedChild.setOnClickListener {
            requireActivity().launchActivity(
                AddSchoolBusActivity::class.java,
                removeAllPrevActivities = false
            )

        }
        tvStartBooking.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("where", "home")
            requireActivity().launchActivity(
                SelectRideActivity::class.java,
                removeAllPrevActivities = false, bundle = bundle
            )

        }
        //getLastSearchedLoactionApi()
        getBannerListApi()

        onGoingTripsApi()
        getProfile()
        //updateFcmTokenApi()
        apiObserver()
        Log.d("USER ID", SharedPref.readString(SharedPref.USER_ID))
    }

    fun getProfile() {
        showDialog()

        viewModels.getProfile(
            user_id = SharedPref.readString(SharedPref.USER_ID),
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun getLastSearchedLoactionApi() {
        showDialog()
        viewModels.getLastSearchedLocationList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )
    }

    fun updateFcmTokenApi() {


        /*if (token.isNullOrEmpty())
        {
            showToast("token empty")
        }
        else {*/
            showDialog()
            viewModels.updateFcmToken(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                SharedPref.readString(SharedPref.COUNTRY_CODE),
                SharedPref.readString(SharedPref.MOBILE),
                "android",
                SharedPref.readString(SharedPref.DEVICE_ID),
                "",
                token.toString()
            )
        //}
    }

    fun getChildListApi() {
        showDialog()
        viewModels.getChildrenList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID)
        )
    }

    fun getBannerListApi() {
        showDialog()
        viewModels.getBannerList(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)
        )
    }

    fun onGoingTripsApi() {

        viewModels.onGoingTrips(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            SharedPref.readString(SharedPref.USER_ID),
            "8"
        )
    }

    fun apiObserver() {
        viewModels.getProfileResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GetProfileResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                SharedPref.writeString(
                                    SharedPref.USERNAME,
                                    data.data?.get(0)?.name.toString()
                                )
                                //showToast("name "+data.data?.get(0)?.name.toString())
                                //showToast("first name "+data.data?.get(0)?.firstName.toString())
                               // showToast(data.data?.get(0)?.name.toString())


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
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        }  else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.childListResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is ChildListingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                childList.clear()
                                if (data.data?.size!! > 0) {
                                    binding.addChild.cardChild.visibility = View.GONE
                                    childList.addAll(data.data)
                                } else {
                                    if (SharedPref.readString(SharedPref.CHILD_ADD_SHOW)
                                            .equals("false")
                                    ) {

                                        binding.addChild.cardChild.visibility = View.GONE
                                    } else {

                                        binding.addChild.cardChild.visibility = View.VISIBLE
                                    }

                                }
                                childListAdapter.notifyDataSetChanged()

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
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        }else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })

        viewModels.lastSearchedLocationListResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is LastSearchLocationsResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                if (data.data?.isNotEmpty() == true) {
                                    binding.llLastSearch.visibility = View.VISIBLE

                                    lastSearchedLocationList.clear()
                                    data.data?.let { it1 -> lastSearchedLocationList.addAll(it1) }
                                    // adapter.notifyDataSetChanged()
                                    adapter.updateItems(lastSearchedLocationList)
                                } else {
                                    binding.llLastSearch.visibility = View.GONE

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
                    if (it.errorCode == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }

                    }
                }
            }
        })
        viewModels.BannerListResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is BannerResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                bannersList.clear()
                                data.data?.let { it1 -> bannersList.addAll(it1) }
                                viewPagerAdapter.notifyDataSetChanged()
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
                    if (it.errorCode == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        }else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragmentLifecycle", "onCreate called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragmentLifecycle", "onResume called, isAdded: ${isAdded}")
        getChildListApi()
        getLastSearchedLoactionApi()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d("FCM", "Token: $token")
            updateFcmTokenApi()
        }
    }


    override fun onDetach() {
        super.onDetach()
        Log.d("TripsFragmentLifecycle", "onDetach called")
    }

    /*fun showAuthenticateDialog() {
        val dialog = CommonDialog.createAuthDialog(
            context = requireActivity(),
            positiveButtonCallback = {
                requireActivity().launchActivity(
                    LanguageSelectActivity::class.java,
                    removeAllPrevActivities = true
                )
                SharedPref.writeString(
                    SharedPref.ACCESS_TOKEN,
                    ""
                )

                SharedPref.writeString(
                    SharedPref.USER_ID,
                    ""
                )
                SharedPref.writeBoolean(SharedPref.IS_LOGIN, false)

            },

        )


        // Show the dialog
        dialog.show()
    }*/

    private fun showToast(message: String?) {
        val activity: FragmentActivity? = activity;
        if (activity != null && isAdded()) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog() {
        try {
            dialog.setContentView(R.layout.dialog_progress)
            dialog.window?.setBackgroundDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                )
            )
            dialog.setCancelable(false)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * this function is use for hiding progress dialog
     */
    private fun hideDialog() {
        try {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showAuthenticateDialog() {
        val dialog = CommonDialog.createAuthDialog(
            context = requireActivity(),
            positiveButtonCallback = {
                requireActivity().launchActivity(
                    LanguageSelectActivity::class.java,
                    removeAllPrevActivities = true
                )
                SharedPref.writeString(
                    SharedPref.ACCESS_TOKEN,
                    ""
                )

                SharedPref.writeString(
                    SharedPref.USER_ID,
                    ""
                )
                SharedPref.writeBoolean(SharedPref.IS_LOGIN, false)

            },

            )


        // Show the dialog
        dialog.show()
    }

    /*
 This will solve your problem.

 Add This on your Fragemnt
    var activity: Activity? = null
     override fun onAttach(@NonNull context: Context?) {
         super.onAttach(context!!)
         activity = if (context is Activity) context else null

         Then change getContext() , getActivity() , requireActivity() or requireContext() with activity
     }*/

    fun getFirstErrorMessageInside(errors: String): String {
        return if (errors.contains(",")) {
            // If there is a comma, get the first part before the comma
            errors.substringBefore(",")
        } else {
            // If no comma, return the whole error string
            errors
        }
    }


}