package com.sagarclientapp.ui.home.selectRide

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemBusesBinding
import com.sagarclientapp.model.BusCategory
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref


class BusesListAdapter(
    private val activity: Activity,
    val vehicleCategoryList: MutableList<BusCategory>,
    OnItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<BusesListAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemBusesBinding
    private var selectedPosition = RecyclerView.NO_POSITION


    inner class CommonViewHolder(var binding: ItemBusesBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int?)
    }

    private var selectedItem: OnItemClickListener? = null

    init {
        selectedItem = OnItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemBusesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding


        //binding.relat.setSelected(position == selectedPosition);

        // Toggle visibility of shadow view based on selection
        if (position == selectedPosition) {
            binding.relat.setBackgroundResource(R.drawable.relat_background_selector)
        } else {
            binding.relat.setBackgroundResource(R.drawable.stroke_theme_blue)

        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.tvVehicleName.text = vehicleCategoryList[position].name
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvVehicleName.text = vehicleCategoryList[position].name_ar
        }
        binding.tvCapacity.text =
            vehicleCategoryList[position].capacity.toString() + activity.getString(
                R.string.people_cap
            )
        binding.tvPrice.text =
            vehicleCategoryList[position].calculated_price.toString() + activity.getString(
                R.string.kd
            )

        Glide.with(activity)
            .load(AppConstants.BASE_URL + "" + vehicleCategoryList[position].image)
            .placeholder(R.drawable.mask)
            .into(binding.ivBus)

        binding.relat.setOnClickListener(View.OnClickListener {
            // Update the selected position
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // Notify item changed for updating UI
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
            /* val previousItem = selectedItem
             selectedItem = holder.adapterPosition

             notifyItemChanged(previousItem)
             notifyItemChanged(selectedItem)*/

            selectedItem?.onItemClick(vehicleCategoryList[position].id)
        })

    }

    override fun getItemCount(): Int {
        return vehicleCategoryList.size

    }

    fun callThis() {
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}