package com.sagarclientapp.ui.home.selectRide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemPlacesListBinding
import com.sagarclientapp.model.LocationCategory
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref

class PlacesAdapterForDefault(
    val activity: SelectRideActivity,
    val locationCategoryList: MutableList<LocationCategory>,
    OnItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<PlacesAdapterForDefault.CommonViewHolder>() {
    private var lastClickedPosition: Int = 0
    private lateinit var binding: ItemPlacesListBinding

    interface OnItemClickListener {
        fun onItemClick(id: Int?)
        fun onLastSearchClick()
    }

    private var selectedItem: OnItemClickListener? = null

    init {
        selectedItem = OnItemClickListener
    }


    inner class CommonViewHolder(var binding: ItemPlacesListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemPlacesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding

        if (position == 0) {
            binding.ivLocationCategory.setImageResource(R.drawable.places_icon)
            binding.tvLocationCategory.text = activity.getString(R.string.last_search)
            //binding.viewLine.visibility = View.VISIBLE

        } else {
            Glide.with(activity)
                .load(AppConstants.BASE_URL_IMAGE + "" +locationCategoryList[position].image)
                .into(binding.ivLocationCategory);
            //binding.viewLine.visibility = if (position == lastClickedPosition) View.VISIBLE else View.GONE
            if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
                binding.tvLocationCategory.text = locationCategoryList[position].nameEn
            }
            if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                binding.tvLocationCategory.text = locationCategoryList[position].nameAr

            }

        }
        binding.viewLine.visibility =
            if (position == lastClickedPosition) View.VISIBLE else View.GONE
        binding.rlPlace.setOnClickListener {
            val previousClickedPosition = lastClickedPosition
            lastClickedPosition = position
            notifyItemChanged(previousClickedPosition)
            notifyItemChanged(lastClickedPosition)
            if (position == 0) {
                selectedItem!!.onLastSearchClick()
            } else {
                selectedItem!!.onItemClick(locationCategoryList[position].id)

            }
        }


    }

    override fun getItemCount(): Int {
        return locationCategoryList.size

    }
}