package com.sagarclientapp.ui.home.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.R
import com.sagarclientapp.databinding.LayoutChildAddedOnHomeBinding
import com.sagarclientapp.model.ChildListing
import com.sagarclientapp.ui.checkout.MyRideActivity
import com.sagarclientapp.ui.checkout.MyRideChildTrackActivity
import com.sagarclientapp.utils.Util.launchActivity

class ChildListOnHomeAdapter(val activity: FragmentActivity,val childList: MutableList<ChildListing>) :
    RecyclerView.Adapter<ChildListOnHomeAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: LayoutChildAddedOnHomeBinding


    inner class FindMyTrainViewHolder(var binding: LayoutChildAddedOnHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = LayoutChildAddedOnHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding
        binding.tvChildNameHome.text=childList[position].name
        val schoolTrips = childList[position].studentDetail?.vehicle?.schoolTrips

// Check if schoolTrips is not null and not empty
        if (schoolTrips != null && schoolTrips.isNotEmpty() && schoolTrips[0]?.rideStatus == "2") {
            binding.btTrack.setBackgroundResource(R.drawable.solid_theme_blue)
            binding.btTrack.isEnabled = true
            binding.btTrack.isClickable = true
            binding.rideText.text=activity.getString(R.string.ride_in_progress)
            binding.rideText.setTextColor(activity.getColor(R.color.green))

        } else {
            binding.btTrack.setBackgroundResource(R.drawable.solid_gray)
            binding.btTrack.isEnabled = false
            binding.btTrack.isClickable = false
            binding.rideText.text=activity.getString(R.string.no_active_rides)
            binding.rideText.setTextColor(activity.getColor(R.color.red))


        }
       /* if (childList[position].studentDetail?.vehicle?.schoolTrips?.get(0)?.rideStatus.equals("2"))
        {
            binding.btTrack.setBackgroundResource(R.drawable.solid_theme_blue)
            binding.btTrack.isEnabled = true
            binding.btTrack.isClickable = true
        }
        else{
            binding.btTrack.setBackgroundResource(R.drawable.solid_gray)
            binding.btTrack.isEnabled = false
            binding.btTrack.isClickable = false
        }*/

        binding.btTrack.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("child_id",childList[position].id.toString())
            bundle.putString("ride_id", childList[position].studentDetail?.vehicle?.schoolTrips?.get(0)?.id.toString())
            activity.launchActivity(
                MyRideChildTrackActivity::class.java,
                removeAllPrevActivities = false,
                bundle = bundle)
        }

    }

    override fun getItemCount(): Int {
        return childList.size
    }

}