package com.sagarclientapp.ui.profile

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemPlaceDataBinding
import com.sagarclientapp.model.SavedPlaces

class SavedPlacesAdapter(private val activity: Activity,
                         val list: MutableList<SavedPlaces>,
                         listener: OnItemClickListener
) :
    RecyclerView.Adapter<SavedPlacesAdapter.CommonViewHolder>() {
    private var selectListlistener: OnItemClickListener? = null
    private lateinit var binding: ItemPlaceDataBinding
    init {

        selectListlistener = listener

    }

    inner class CommonViewHolder(var binding: ItemPlaceDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemPlaceDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding

        binding.llDistanceView.visibility = View.GONE

        binding.tvFullAddress.text=list[position].address
        binding.tvPrimaryLocation.text=list[position].title

        binding.ivfav.setOnClickListener {
            selectListlistener!!.onItemClick(list[position].id)

        }

        /*if (activity is MapScreenActivity)
        {
            binding.llDistanceView.visibility= View.GONE
        }
        else
        {
            binding.llDistanceView.visibility= View.VISIBLE

        }

        binding.llPlace.setOnClickListener {

            if (activity is MapScreenActivity)
            {
                selectListlistener?.onItemClick()


            }
        }



*/

    }

    override fun getItemCount(): Int {
        return list.size

    }
}