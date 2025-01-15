package com.sagarclientapp.ui.profile

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.sagarclientapp.R
import com.sagarclientapp.base.BaseFragment
import com.sagarclientapp.databinding.FragmentProfileBinding
import com.sagarclientapp.ui.login.CreateProfileActivity
import com.sagarclientapp.ui.login.LoginActivity
import com.sagarclientapp.utils.CommonDialog
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sagarclientapp.api.Response
import com.sagarclientapp.model.GetLanguageStatusResponse
import com.sagarclientapp.model.GetNotificationStatusResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.LanguageSelectActivity
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.profile.viewmodels.ProfileViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var dialog: Dialog

    lateinit var binding: FragmentProfileBinding
    var language_state = ""
    private val viewModels: ProfileViewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ProfileFragmentLifecycle", "onCreateView called")

        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
        // return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            initUis()
            apisObservers()

    }

    private fun initUis() = binding.apply {
        dialog = Dialog(requireActivity())

        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.iv1.setImageResource(R.drawable.right)
            binding.iv2.setImageResource(R.drawable.right)
            binding.iv3.setImageResource(R.drawable.right)
            binding.iv4.setImageResource(R.drawable.right)
            binding.iv5.setImageResource(R.drawable.right)
            binding.iv6.setImageResource(R.drawable.right)
            binding.iv7.setImageResource(R.drawable.right)
            binding.iv8.setImageResource(R.drawable.right)
            binding.iv9.setImageResource(R.drawable.right)


        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.iv1.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv2.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv3.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv4.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv5.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv6.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv7.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv8.setImageResource(R.drawable.only_arrow_arabic)
            binding.iv9.setImageResource(R.drawable.only_arrow_arabic)
        }


        headerProfile.ivBack.visibility = View.GONE
        headerProfile.headerTitle.text = getString(R.string.account_setting)


        tvEditProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("where", "profile")
            requireActivity().launchActivity(
                CreateProfileActivity::class.java, removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvSavedPlaces.setOnClickListener {

            requireActivity().launchActivity(
                SavedPlacesActivity::class.java,
                removeAllPrevActivities = false
            )

        }

        tvEditMobileNumber.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("where", "profile")
            requireActivity().launchActivity(
                LoginActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }

        tvTermsCondition.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "terms")
            requireActivity().launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvRefundPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "refund")
            requireActivity().launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvPrivacyPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", "privacy")
            requireActivity().launchActivity(
                PolicyTextScreenActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle
            )

        }
        tvManageChild.setOnClickListener {
            requireActivity().launchActivity(
                ManageYourChildActivity::class.java,
                removeAllPrevActivities = false
            )
        }
        tvContactUs.setOnClickListener {

            requireActivity().launchActivity(
                ContactUsActivity::class.java,
                removeAllPrevActivities = false
            )

        }

        tvLogout.setOnClickListener {
            showDialog(
                getString(R.string.confirmation),
                getString(R.string.are_yo_sure_you_want_to_log_out),
                getString(R.string.logout),
                getString(R.string.cancel),
                "logout"
            )
        }
        tvLanguage.setOnClickListener {

            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.language_dialog, null)

            // Create the dialog
            val dialog = Dialog(requireContext(), R.style.DialogBackgroundStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(view)

            // Set the button texts
            val positiveButton = view.findViewById<TextView>(R.id.tvPostiveText)
            val negativeButton = view.findViewById<TextView>(R.id.tvNegetiveText)

            var lang_id = SharedPref.readString(SharedPref.LANGUAGE_ID)


            Log.d("LANGUAGR",lang_id)

            if (lang_id.equals("0")) {
                Log.d("LANGUAGR",lang_id)

                negativeButton.setBackgroundResource(R.drawable.solid_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.white))
                positiveButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.theme_blue))

            }
            if (lang_id.equals("1")) {
                Log.d("LANGUAGR",lang_id)

                negativeButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.theme_blue))
                positiveButton.setBackgroundResource(R.drawable.solid_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.white))
            }

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            positiveButton.setOnClickListener {
                language_state = "1"
                negativeButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.theme_blue))
                positiveButton.setBackgroundResource(R.drawable.solid_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.white))
                languageUpdateApi("1")
                dialog.dismiss()

            }
            negativeButton.setOnClickListener {
                language_state = "0"
                negativeButton.setBackgroundResource(R.drawable.solid_theme_blue)
                negativeButton.setTextColor(requireActivity().getColor(R.color.white))
                positiveButton.setBackgroundResource(R.drawable.stroke_theme_blue)
                positiveButton.setTextColor(requireActivity().getColor(R.color.theme_blue))

                languageUpdateApi("0")
                dialog.dismiss()

            }
            dialog.show()


            /*showDialog(getString(R.string.confirmation),
                getString(R.string.are_yo_sure_you_want_to_log_out),
                getString(R.string.logout),
                getString(R.string.cancel))*/
        }
        tvDeleteAccount.setOnClickListener {
            showDialog(
                getString(R.string.confirmation),
                getString(R.string.you_want_to_delete_account),
                getString(R.string.delete),
                getString(R.string.cancel),
                "deleteAccount"
            )
        }

        /*notificationToggle.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                updateNotificationStatus("1")
            } else {
                updateNotificationStatus("0")
            }
        }*/

        notificationToggle.setOnClickListener {
            if (binding.notificationToggle.isChecked == true) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
                else{
                    updateNotificationStatus("1")
                }
                //updateNotificationStatus("1")
            } else {
                updateNotificationStatus("0")

            }
        }

        getNotificationStatus()
        getLanguageStatusApi()
    }


    fun showDialog(
        title: String,
        subtitle: String,
        positiveButtonText: String,
        negativeButtonText: String,
        dialogType: String
    ) {
        // Create the dialog
        val dialog = CommonDialog.createDialog(
            context = requireContext(),
            title = title,
            subtitle = subtitle,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            positiveButtonCallback = {
                if (dialogType.equals("logout")) {
                    logoutApi()
                }
                if (dialogType.equals("deleteAccount")) {
                    deleteAccount()
                }

            },
            negativeButtonCallback = {

            }
        )


        // Show the dialog
        dialog.show()
    }

    fun updateNotificationStatus(notificationStatus: String) {
        showDialog()
        viewModels.updateNotificationStatus(
            SharedPref.readString(SharedPref.USER_ID),
            SharedPref.readString(SharedPref.DEVICE_ID),
            notification_status = notificationStatus,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun getNotificationStatus() {
        showDialog()
        viewModels.getNotificationStatus(
            SharedPref.readString(SharedPref.USER_ID),
            SharedPref.readString(SharedPref.DEVICE_ID),

            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun getLanguageStatusApi() {
        showDialog()
        viewModels.getLanguageStatus(
            SharedPref.readString(SharedPref.USER_ID),
            SharedPref.readString(SharedPref.DEVICE_ID),
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun deleteAccount() {
        showDialog()
        viewModels.deleteAccount(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun logoutApi() {
        showDialog()
        viewModels.logout(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )
    }

    fun languageUpdateApi(lang_id: String) {
        showDialog()
        viewModels.languageUpdate(
            SharedPref.readString(SharedPref.USER_ID),
            SharedPref.readString(SharedPref.DEVICE_ID),
            lang_id,
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN)

        )

    }

    fun apisObservers() {
        viewModels.notificationUpdateResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

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
        viewModels.getLanguageStatusResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GetLanguageStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                SharedPref.writeString(
                                    SharedPref.LANGUAGE_ID,
                                    data.data?.langId.toString()
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
        viewModels.deleteAccountResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
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
                                SharedPref.writeString(SharedPref.CHILD_ADD_SHOW, "")
                                SharedPref.writeBoolean(SharedPref.IS_LOGIN, false)

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
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.getNotificationStatusResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GetNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                if (data.data?.notificationStatus == 1) {
                                    binding.notificationToggle.isChecked = true
                                } else {
                                    binding.notificationToggle.isChecked = false

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
                            showToast(getFirstErrorMessageInside(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.languageUpdateResponse.observe(requireActivity(), Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                Log.d("languageUpdateResponse",language_state)
                                SharedPref.writeString(SharedPref.LANGUAGE_ID, language_state)
                                if (language_state.equals("0"))
                                {
                                    Util.setLocale(requireActivity(), "en")

                                }
                                if (language_state.equals("1"))
                                {
                                    Util.setLocale(requireActivity(), "ar")

                                }
                                requireActivity().launchActivity(
                                    HomeActivity::class.java,
                                    removeAllPrevActivities = true
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
                    if (it.errorBody?.status == 422) {
                        showToast("The mobile has already been taken.")
                    } else if (it.errorBody?.status == 401) {
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            updateNotificationStatus("1")

        }
    }
    fun getFirstErrorMessageInside(errors: String): String {
        return if (errors.contains(",")) {
            // If there is a comma, get the first part before the comma
            errors.substringBefore(",")
        } else {
            // If no comma, return the whole error string
            errors
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ProfileFragmentLifecycle", "onCreate called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProfileFragmentLifecycle", "onResume called, isAdded: ${isAdded}")


    }


    override fun onDetach() {
        super.onDetach()
        Log.d("ProfileFragmentLifecycle", "onDetach called")
    }
    private fun showToast(message: String?) {
        val activity: FragmentActivity? = activity;
        if(activity != null && isAdded()) {
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


}