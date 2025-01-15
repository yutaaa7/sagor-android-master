package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.databinding.ItemPaymentModesBinding
import com.sagarclientapp.model.PaymentrModesDataItem
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref

class PaymentModesAdapter(
    private val activity: Activity,

    private val paymentModesList: MutableList<PaymentrModesDataItem>,
    private val clickListener: onItemClickListenre
) :
    RecyclerView.Adapter<PaymentModesAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemPaymentModesBinding
    lateinit var itemClickListener: onItemClickListenre

    // Initially set the first item as selected (KNET)
    private var selectedItemPosition: Int = 0

    interface onItemClickListenre {
        fun onClick(id: String?, nameEn: String?, image: String?)
    }

    init {
        itemClickListener = clickListener


    }


    inner class CommonViewHolder(var binding: ItemPaymentModesBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemPaymentModesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.tvCreditDebit.text = paymentModesList[position].nameEn
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvCreditDebit.text = paymentModesList[position].nameAr

        }
        Glide.with(activity)
            .load(AppConstants.BASE_URL_IMAGE + "" + paymentModesList?.get(position)?.image)

            .into(binding.ivpaymode)

        // Set the selection state
        binding.ivCreditRadioButton.isChecked = position == selectedItemPosition

        /*
        // Change background or text color based on selection
        if (position == selectedItemPosition) {
            binding.ivCancel.isChecked = true
        } else {
            binding.ivCancel.isChecked = false

        }
        binding.ivCancel.setOnClickListener {
            // Handle item selection
            val previousPosition = selectedItemPosition
            selectedItemPosition = position

            // Notify changes for updating UI
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedItemPosition)
            // binding.ivCancel.setImageResource(R.drawable.dot_circle)
            itemClickListener.onClick(cancelRideReasonsList[position].id)
        }*/
        binding.root.setOnClickListener {
            // Update the selected position
            val previousPosition = selectedItemPosition
            selectedItemPosition = position
            var name = ""
            // Notify the adapter to refresh the UI
            notifyItemChanged(previousPosition) // Deselect the previous item
            notifyItemChanged(selectedItemPosition) // Select the new item
            if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
                name = paymentModesList[position].nameEn.toString()
            }
            if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                name = paymentModesList[position].nameAr.toString()

            }
            itemClickListener.onClick(
                paymentModesList.get(position).payment_type,
                name,
                paymentModesList?.get(position)?.image

            )
        }

    }

    override fun getItemCount(): Int {
        return paymentModesList.size

    }
    fun selectPaymentType(paymentType: String) {
        val position = paymentModesList.indexOfFirst { it.payment_type == paymentType }
        if (position != -1) {
            // Update the selected item position and notify the adapter to update the UI
            val previousPosition = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedItemPosition)

            // Call the click listener for the selected item
            val selectedItem = paymentModesList[position]
            itemClickListener.onClick(selectedItem.payment_type, selectedItem.nameEn, selectedItem.image)
        }
    }

}