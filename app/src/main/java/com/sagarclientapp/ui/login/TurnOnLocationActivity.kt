package com.sagarclientapp.ui.login

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sagarclientapp.R
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityTurnOnLocationBinding
import com.sagarclientapp.utils.Util.launchActivity

class TurnOnLocationActivity : BaseActivity() {
    lateinit var binding: ActivityTurnOnLocationBinding
    lateinit var bundle: Bundle
    private  val LOCATION_PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTurnOnLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {
        bundle = Bundle()

        locationHeader.tvTitle.text = getString(R.string.turn_the_location_services)
        locationHeader.tvSubTitle.text =
            getString(R.string.this_will_help_sagor_to_take_your_current_location_while_booking_the_trip)

        btTurnOn.setOnClickListener {

            if (hasLocationPermission()) {
                bundle.putString("where", "location")
                launchActivity(
                    CreateProfileActivity::class.java,
                    removeAllPrevActivities = false
                )
            } else {
                requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)
            }

        }
        btTurnOff.setOnClickListener {

            bundle.putString("where", "location")
            launchActivity(
                CreateProfileActivity::class.java,
                removeAllPrevActivities = false
            )
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
                    bundle.putString("where", "location")
                    launchActivity(
                        CreateProfileActivity::class.java,
                        removeAllPrevActivities = false
                    )
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

}