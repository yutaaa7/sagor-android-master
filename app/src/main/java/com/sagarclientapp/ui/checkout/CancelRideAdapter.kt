package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemCancelRideBinding
import com.sagarclientapp.model.CancelBookingReasons
import com.sagarclientapp.utils.SharedPref

class CancelRideAdapter(
    private val activity: Activity,
    val cancelRideReasonsList: MutableList<CancelBookingReasons>,
    private val clickListener: onItemClickListenre
) :
    RecyclerView.Adapter<CancelRideAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemCancelRideBinding
    private lateinit var itemClickListener: onItemClickListenre
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    interface onItemClickListenre {
        fun onClick(id: Int?)
    }

    init {
        itemClickListener = clickListener
    }


    inner class CommonViewHolder(var binding: ItemCancelRideBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemCancelRideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.ivCancel.text = cancelRideReasonsList[position].name
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1"))
        {
            binding.ivCancel.text = cancelRideReasonsList[position].nameAr

        }
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
        }

    }

    override fun getItemCount(): Int {
        return cancelRideReasonsList.size

    }
}