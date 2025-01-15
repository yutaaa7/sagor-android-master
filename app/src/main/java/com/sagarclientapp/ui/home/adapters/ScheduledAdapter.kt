package com.sagarclientapp.ui.home.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemScheduledBinding
import com.sagarclientapp.model.DataItem

class ScheduledAdapter(
    requireActivity: FragmentActivity,
    val historyList: MutableList<DataItem>,
    onItemClickListener: onItemClickListener
) : RecyclerView.Adapter<ScheduledAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemScheduledBinding
    private lateinit var onItemClick: onItemClickListener

    interface onItemClickListener {
        fun onClick(id: Int?)
    }

    init {
        onItemClick = onItemClickListener

    }


    inner class FindMyTrainViewHolder(var binding: ItemScheduledBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = ItemScheduledBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding
        var trip_mode_from_api=historyList.get(position).tripMode
        Log.d("trip_mode_from_api",trip_mode_from_api.toString())
        if (trip_mode_from_api.equals("0")) {
            binding.tv1.text = historyList.get(position)?.triplocations?.get(0)?.pickupLocation.toString()
            binding.tv2.text =
                historyList.get(position)?.triplocations?.get(0)?.dropLocation.toString()
            binding.date.text =
                historyList.get(position)?.triplocations?.get(0)?.startDate.toString()
            binding.tvPickUpTime.text =
                historyList.get(position)?.triplocations?.get(0)?.startTime.toString() + " " + historyList.get(
                    position
                )?.triplocations?.get(0)?.startTimeUnit.toString()

        }

        if (trip_mode_from_api.equals("1")) {
            if (historyList.get(0)?.triplocations?.get(0)?.tripStatus.equals("completed") && historyList.get(0)?.triplocations?.size!! > 1) {
                binding.tv1.text = historyList.get(position)?.triplocations?.get(1)?.pickupLocation.toString()
                binding.tv2.text =
                    historyList.get(position)?.triplocations?.get(1)?.dropLocation.toString()
                binding.date.text =
                    historyList.get(position)?.triplocations?.get(1)?.startDate.toString()
                binding.tvPickUpTime.text =
                    historyList.get(position)?.triplocations?.get(1)?.startTime.toString() + " " + historyList.get(
                        position
                    )?.triplocations?.get(1)?.startTimeUnit.toString()
            }
            else{
                binding.tv1.text = historyList.get(position)?.triplocations?.get(0)?.pickupLocation.toString()
                binding.tv2.text =
                    historyList.get(position)?.triplocations?.get(0)?.dropLocation.toString()
                binding.date.text =
                    historyList.get(position)?.triplocations?.get(0)?.startDate.toString()
                binding.tvPickUpTime.text =
                    historyList.get(position)?.triplocations?.get(0)?.startTime.toString() + " " + historyList.get(
                        position
                    )?.triplocations?.get(0)?.startTimeUnit.toString()
            }
        }
       /* binding.tv1.text = historyList.get(position)?.triplocations?.get(0)?.pickupLocation.toString()
        binding.tv2.text =
            historyList.get(position)?.triplocations?.get(0)?.dropLocation.toString()
        binding.date.text =
            historyList.get(position)?.triplocations?.get(0)?.startDate.toString()
        binding.tvPickUpTime.text =
            historyList.get(position)?.triplocations?.get(0)?.startTime.toString() + " " + historyList.get(
                position
            )?.triplocations?.get(0)?.startTimeUnit.toString()
*/
        binding.llScheduled.setOnClickListener {
            onItemClick.onClick(historyList.get(position)?.id)
        }


    }

    override fun getItemCount(): Int {
        return historyList.size
    }

}