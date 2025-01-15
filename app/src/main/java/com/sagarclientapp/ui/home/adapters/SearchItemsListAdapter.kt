package com.sagarclientapp.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemLastSearchBinding
import com.sagarclientapp.model.LastSearchesLocations

fun <T> List<T>.takeFirstThreeOrAll(): List<T> {
    return if (this.size > 3) this.take(3) else this
}

class SearchItemsListAdapter(
    requireActivity: FragmentActivity,
    val lastSearchedLocationList: MutableList<LastSearchesLocations>,
    onItemClickListener: onItemClickListener
) : RecyclerView.Adapter<SearchItemsListAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemLastSearchBinding
    private var displayedList: List<LastSearchesLocations> =
        lastSearchedLocationList.takeFirstThreeOrAll()

    private lateinit var onItemClick: onItemClickListener

    interface onItemClickListener {
        fun onClick(address: String?, title: String?, lat: String?, jsonMemberLong: String?)
    }

    init {
        onItemClick = onItemClickListener

    }

    inner class FindMyTrainViewHolder(var binding: ItemLastSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = ItemLastSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding
        binding.tvAddress.text = lastSearchedLocationList[position].address

        binding.root.setOnClickListener {
            onItemClick.onClick(
                lastSearchedLocationList[position].address,
                lastSearchedLocationList[position].title,
                lastSearchedLocationList[position].lat,
                lastSearchedLocationList[position].jsonMemberLong
            )
        }

    }

    override fun getItemCount(): Int {
        return displayedList.size
    }

    fun updateItems(newItems: List<LastSearchesLocations>) {
        displayedList = newItems.takeFirstThreeOrAll()
        notifyDataSetChanged()
    }

}