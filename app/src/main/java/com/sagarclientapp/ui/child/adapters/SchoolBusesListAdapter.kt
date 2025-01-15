package com.sagarclientapp.ui.child.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemSchoolBussListBinding
import com.sagarclientapp.model.GradesList
import com.sagarclientapp.model.SchoolList
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref

class SchoolBusesListAdapter(
    private val requireActivity: FragmentActivity,
    private val schoolList: MutableList<SchoolList>,
    onItemClick: onItemClick
) :
    RecyclerView.Adapter<SchoolBusesListAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemSchoolBussListBinding
    private var selectedItemClickLis: onItemClick? = null
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    // Update data and reset selection
    fun updateData(newBusList: List<SchoolList>) {
        schoolList.clear()
        schoolList.addAll(newBusList)
        selectedItemPosition = -1 // Reset selection
        notifyDataSetChanged()
    }
    interface onItemClick {
        fun onClick(id: Int?)
    }

    init {
        selectedItemClickLis = onItemClick
    }


    inner class FindMyTrainViewHolder(var binding: ItemSchoolBussListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding =
            ItemSchoolBussListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding
        Glide.with(requireActivity)
            .load(AppConstants.BASE_URL_IMAGE + "" +schoolList[position].logo)
            .placeholder(R.drawable.mask)
            .into(binding.ivSchoolImage);

            // binding.rlBusesBack.background = null
        // Reset background and text color based on selection state
        if (position == selectedItemPosition) {
            binding.rlBusesBack.setBackgroundResource(R.drawable.stroke_theme_blue)
            binding.tvSchoolName.setTextColor(requireActivity.getColor(R.color.theme_blue))
        } else {
            binding.rlBusesBack.setBackgroundResource(0) // Or set to default background if any
            binding.tvSchoolName.setTextColor(requireActivity.getColor(R.color.gray_7E7E7E))
        }

        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {
            binding.tvSchoolName.text = schoolList[position].name
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvSchoolName.text = schoolList[position].name_ar

        }

        binding.rlBusesBack.setOnClickListener {
           /* binding.rlBusesBack.setBackgroundResource(R.drawable.stroke_theme_blue)
            binding.tvSchoolName.setTextColor(requireActivity.getColor(R.color.theme_blue))*/

            val previousSelectedPosition = selectedItemPosition
            selectedItemPosition = position

            // Notify the adapter to refresh the previous selected item and the current item
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedItemPosition)

            selectedItemClickLis?.onClick(schoolList[position].id)
        }


    }

    override fun getItemCount(): Int {
        return schoolList.size
    }

}