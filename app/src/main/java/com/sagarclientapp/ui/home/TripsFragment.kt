package com.sagarclientapp.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.sagarclientapp.R
import com.sagarclientapp.databinding.FragmentTripsBinding
import com.sagarclientapp.ui.LanguageSelectActivity
import com.sagarclientapp.utils.CommonDialog
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsFragment : Fragment() {
    private lateinit var dialog: Dialog

    lateinit var binding: FragmentTripsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TripsFragmentLifecycle", "onCreateView called")
        binding = FragmentTripsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TripsFragmentLifecycle", "onCreate called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUis()
    }

    fun initUis() = binding.apply {
        dialog = Dialog(requireActivity())

        tripTootlbar.ivBack.visibility=View.GONE
        tripTootlbar.headerTitle.text=getString(R.string.my_ride)
        val adapter = TripsViewPagerAdapter(requireContext(),childFragmentManager)

        tripsViewPager.adapter = adapter


        tripsTab.setupWithViewPager(tripsViewPager)
    }
    override fun onResume() {
        super.onResume()
        Log.d("TripsFragmentLifecycle", "onResume called, isAdded: ${isAdded}")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("TripsFragmentLifecycle", "onDetach called")
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