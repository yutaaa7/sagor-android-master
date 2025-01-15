package com.sagarclientapp.ui.home.selectRide

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemPlaceDataBinding
import com.sagarclientapp.model.LastSearchesLocations

class LastSearchLocationsAdapter(
    private val activity: Activity,
    private val lastSearchedLocationList: MutableList<LastSearchesLocations>,
    listener: OnItemClickListener

) :
    RecyclerView.Adapter<LastSearchLocationsAdapter.CommonViewHolder>() {
    private var selectListlistener: OnItemClickListener? = null

    private lateinit var binding: ItemPlaceDataBinding

    init {

        selectListlistener = listener

    }

    interface OnItemClickListener {
        fun onItemClick(lastSearchesLocations: LastSearchesLocations)
        fun onAddressClick(lastSearchesLocations: LastSearchesLocations)
    }


    inner class CommonViewHolder(var binding: ItemPlaceDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemPlaceDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        binding.llDistanceView.visibility = View.GONE
        binding.tvPrimaryLocation.text = lastSearchedLocationList[position].title
        binding.tvFullAddress.text = lastSearchedLocationList[position].address
        if (lastSearchedLocationList[position].wishlistFlag == 0) {
            binding.ivfav.setImageResource(R.drawable.heart_stroke)
        } else {
            binding.ivfav.setImageResource(R.drawable.heart_fill)

        }
        binding.ivfav.setOnClickListener {
          /*  if (lastSearchedLocationList[position].wishlistFlag == 0) {
                binding.ivfav.setImageResource(R.drawable.heart_fill)
            } else {
                binding.ivfav.setImageResource(R.drawable.heart_stroke)

            }*/
            selectListlistener!!.onItemClick(lastSearchedLocationList[position])
        }
        binding.llPlace.setOnClickListener {
            selectListlistener!!.onAddressClick(lastSearchedLocationList[position])
        }


    }

    override fun getItemCount(): Int {
        return lastSearchedLocationList.size

    }
}