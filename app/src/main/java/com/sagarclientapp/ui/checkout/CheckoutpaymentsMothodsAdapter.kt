package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemPaymentMethodsBinding
import com.sagarclientapp.databinding.ItemTripDetailsCheckoutBinding

class CheckoutpaymentsMothodsAdapter  (private val activity: Activity) :
    RecyclerView.Adapter<CheckoutpaymentsMothodsAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemPaymentMethodsBinding


    inner class CommonViewHolder(var binding: ItemPaymentMethodsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemPaymentMethodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding

    }

    override fun getItemCount(): Int {
        return 1

    }
}