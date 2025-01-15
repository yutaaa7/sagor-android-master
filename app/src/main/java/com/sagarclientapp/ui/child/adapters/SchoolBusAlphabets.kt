package com.sagarclientapp.ui.child.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemSchoolBusBinding

class SchoolBusAlphabets (private  val requireActivity: FragmentActivity, onItemClick: onItemClick) : RecyclerView.Adapter<SchoolBusAlphabets.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemSchoolBusBinding
    private var selectedItemClickLis: onItemClick? = null

    interface onItemClick {
        fun onClick()
    }

    init {
        selectedItemClickLis = onItemClick
    }


    inner class FindMyTrainViewHolder(var binding: ItemSchoolBusBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = ItemSchoolBusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding




    }

    override fun getItemCount(): Int {
        return 3
    }

}