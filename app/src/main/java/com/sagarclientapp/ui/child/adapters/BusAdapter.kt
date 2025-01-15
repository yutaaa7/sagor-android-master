package com.sagarclientapp.ui.child.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemBusBinding
import com.sagarclientapp.model.BusListing
import com.sagarclientapp.utils.SharedPref

class BusAdapter(
    private val requireActivity: FragmentActivity,
    val busList: MutableList<BusListing>,
    onItemClick: onItemClick
) :
    RecyclerView.Adapter<BusAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemBusBinding
    private var selectedItemClickLis: onItemClick? = null
    private var selectedItemPosition: Int = -1


    interface onItemClick {
        fun onClick(id: Int?)
    }

    // Update data and reset selection
    fun updateData(newBusList: List<BusListing>) {
        busList.clear()
        busList.addAll(newBusList)
        selectedItemPosition = -1 // Reset selection
        notifyDataSetChanged()
    }

    init {
        selectedItemClickLis = onItemClick
    }


    inner class FindMyTrainViewHolder(var binding: ItemBusBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = ItemBusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding


        binding.tvBusNumber.text =
            requireActivity.getString(R.string.bus_) + busList[position].vehicleNumber
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.tvDescription.text = busList[position].routeDescription
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1"))
        {
            binding.tvDescription.text = busList[position].routeDescriptionAr

        }

        if (position == selectedItemPosition) {
            binding.llBusBack.setBackgroundResource(R.drawable.stroke_theme_blue)

        } else {
            binding.llBusBack.background = null
        }

        binding.llBusOfChild.setOnClickListener {
            //binding.llBusBack.setBackgroundResource(R.drawable.stroke_theme_blue)
            val previousSelectedPosition = selectedItemPosition
            selectedItemPosition = position

            // Notify the adapter to refresh the previous selected item and the current item
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedItemPosition)
            selectedItemClickLis?.onClick(busList[position].id)
        }


    }

    override fun getItemCount(): Int {
        return busList.size
    }

}