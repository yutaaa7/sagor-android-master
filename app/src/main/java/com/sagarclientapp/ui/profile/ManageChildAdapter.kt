package com.sagarclientapp.ui.profile

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemManageChildBinding
import com.sagarclientapp.model.ChildListing
import com.sagarclientapp.utils.SharedPref

class ManageChildAdapter(private val activity: Activity, val childList: MutableList<ChildListing>,
                         listener: OnItemClickListener
) :
    RecyclerView.Adapter<ManageChildAdapter.CommonViewHolder>() {
    private var selectListlistener: OnItemClickListener? = null
    private lateinit var binding: ItemManageChildBinding
    init {

        selectListlistener = listener

    }

    inner class CommonViewHolder(var binding: ItemManageChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemManageChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        binding.tvChildName.text = childList[position].name
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

            binding.tvGradeName.text = childList[position].grades?.name
        }

        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvGradeName.text = childList[position].grades?.nameAr

        }

        binding.ivDelete.setOnClickListener {
            selectListlistener!!.onItemClick(childList[position].id)
        }

    }

    override fun getItemCount(): Int {
        return childList.size
    }
}