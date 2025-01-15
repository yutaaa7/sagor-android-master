package com.sagarclientapp.ui.login.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemCountryListBinding
import com.sagarclientapp.model.CountryList

class CountryListAdapter(
    private val activity: Activity,
    private val countryList: MutableList<CountryList>,
    val btnlistener: BtnClickListener
) : RecyclerView.Adapter<CountryListAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemCountryListBinding

    interface BtnClickListener {
        fun onClick(countryList: CountryList)
    }

    companion object {
        var mClickListener: BtnClickListener? = null
    }


    inner class CommonViewHolder(var binding: ItemCountryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemCountryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        mClickListener = btnlistener

        val binding = holder.binding
        binding.tvCountryName.text = countryList[position].name
        binding.tvCountryCode.text = countryList[position].phoneCode
        Glide.with(activity)
            .load(countryList[position].flag)
            .placeholder(R.drawable.flag).into(binding.ivCountryFlag)

        binding.llCountryView.setOnClickListener {
            mClickListener?.onClick(countryList[position])
        }

    }

    override fun getItemCount(): Int {
        return countryList.size

    }
}