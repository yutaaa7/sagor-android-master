package com.sagarclientapp.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemHistoryBinding
import com.sagarclientapp.model.DataItem

class HistoryAdapter(val activity: FragmentActivity, val historyList: MutableList<DataItem>) :
    RecyclerView.Adapter<HistoryAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemHistoryBinding


    inner class FindMyTrainViewHolder(var binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding
        binding.tvCurrentLocation.text =
            historyList.get(position)?.triplocations?.get(0)?.pickupLocation.toString()
        binding.date.text =
            historyList.get(position)?.triplocations?.get(0)?.startDate.toString()
        binding.tvTime.text =
            historyList.get(position)?.triplocations?.get(0)?.startTime.toString() + " " + historyList.get(
                position
            )?.triplocations?.get(0)?.startTimeUnit.toString()
        binding.tvDestination.text =
            historyList.get(position)?.triplocations?.get(0)?.dropLocation.toString()
        binding.tvDistanace.text =
            historyList.get(position)?.triplocations?.get(0)?.distance.toString() + " " + historyList.get(
                position
            )?.triplocations?.get(0)?.distanceUnit.toString()
        if (historyList.get(position)?.tripMode.equals("0")) {
            binding.tvType.text = activity.getString(R.string.one_way)
        }
        if (historyList.get(position)?.tripMode.equals("1")) {
            binding.tvType.text = activity.getString(R.string.round_trip)
        }
        if (historyList.get(position)?.discountPrice == null) {

            binding.tvPayment.text =
                historyList.get(position)?.total_price + " " + historyList.get(position)?.priceUnit
        } else {
            binding.tvPayment.text =
                historyList.get(position)?.price + " " + historyList.get(position)?.priceUnit
        }


    }

    override fun getItemCount(): Int {
        return historyList.size
    }

}