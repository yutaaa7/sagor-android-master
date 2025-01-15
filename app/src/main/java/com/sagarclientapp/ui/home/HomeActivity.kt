package com.sagarclientapp.ui.home

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ActivityHomeBinding
import com.sagarclientapp.ui.profile.ProfileFragment
import com.sagarclientapp.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    var bundle: Bundle? = null
    var page: String? = null
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // If there's no saved state, this is the first creation, so load the default fragment
        if (savedInstanceState == null) {
            showPage("home") // Default page is home
        }


        initUis()
    }


    private fun initUis() = binding.apply {

       /* FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM", "Token: $token")
        }*/
        // Log.d("SHA", getSHA1().toString())


        bundle = intent.extras

        if (bundle != null) {

            page = bundle?.getString("page")

        }


        if (page.equals("3")) {
            showPage("settings")
        } /*else {
            showPage("home")
        }*/

        rlHome.setOnClickListener {
            if (isClickAllowed()) {
                showPage("home")

            }
        }
        rlTrips.setOnClickListener {
            if (isClickAllowed()) {
                showPage("trips")
            }
        }
        rlSettings.setOnClickListener {
            if (isClickAllowed()) {
                showPage("settings")
            }
        }
    }

    fun showPage(s: String) = binding.apply {
        if (s.equals("home")) {
            Log.d("HomeActivity", "Home tab clicked")
            rlHomeFocus.visibility = View.VISIBLE
            rlHome.visibility = View.GONE
            rlSettingsFocus.visibility = View.GONE
            rlSettings.visibility = View.VISIBLE
            rlTripsFocus.visibility = View.GONE
            rlTrips.visibility = View.VISIBLE
            /*if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is HomeFragment) {
                showFragment(HomeFragment())
            }*/
              showFragment(HomeFragment())
        }
        if (s.equals("trips")) {
            Log.d("HomeActivity", "trips tab clicked")


            rlHomeFocus.visibility = View.GONE
            rlHome.visibility = View.VISIBLE
            rlSettingsFocus.visibility = View.GONE
            rlSettings.visibility = View.VISIBLE
            rlTripsFocus.visibility = View.VISIBLE
            rlTrips.visibility = View.GONE
            /*if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is TripsFragment) {
                showFragment(TripsFragment())
            }*/
            showFragment(TripsFragment())
        }
        if (s.equals("settings")) {
            Log.d("HomeActivity", "settings tab clicked")

            rlHomeFocus.visibility = View.GONE
            rlHome.visibility = View.VISIBLE
            rlSettingsFocus.visibility = View.VISIBLE
            rlSettings.visibility = View.GONE
            rlTripsFocus.visibility = View.GONE
            rlTrips.visibility = View.VISIBLE
           /* if (supportFragmentManager.findFragmentById(R.id.fragment_container) !is ProfileFragment) {
                showFragment(ProfileFragment())
            }*/
           showFragment(ProfileFragment())
        }

        Log.d("USER", SharedPref.readString(SharedPref.USER_ID))
        Log.d("USER", SharedPref.readString(SharedPref.ACCESS_TOKEN))
        Log.d("USER", SharedPref.readString(SharedPref.DEVICE_ID))


    }

    fun showFragment(fragment: Fragment) {
        // Ensure the activity is not in an invalid state

        val backStateName = fragment.javaClass.name
        val fragmentManager = supportFragmentManager
        val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit()
        }
    }

   /* private fun showFragment(fragment: Fragment) {
        // Ensure the activity is not in an invalid state
        if (isFinishing || isDestroyed) return

        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)

        Log.d("FragmentLifecycle", "Current Fragment: ${currentFragment?.javaClass?.simpleName}")

        // Check if the current fragment is the same as the one to be shown
        if (currentFragment == null || !currentFragment.isAdded || currentFragment::class.java != fragment::class.java) {
            Log.d("FragmentLifecycle", "Replacing with Fragment: ${fragment.javaClass.simpleName}")

            // Begin the fragment transaction
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commitAllowingStateLoss() // Commit the transaction, allowing state loss if necessary
        } else {
            Log.d("FragmentLifecycle", "Fragment ${fragment.javaClass.simpleName} is already displayed.")
        }
    }
*/


    override fun onResume() {
        super.onResume()
    }



    @RequiresApi(Build.VERSION_CODES.P)
    fun getSHA1(): String? {
        try {
            val info =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = info.signingInfo.apkContentsSigners
            val md = MessageDigest.getInstance("SHA-1")
            signatures.forEach {
                md.update(it.toByteArray())
            }
            val digest = md.digest()
            return digest.joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    var lastClickTime = 0L
    val MIN_CLICK_INTERVAL = 500L

    fun isClickAllowed(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }
}