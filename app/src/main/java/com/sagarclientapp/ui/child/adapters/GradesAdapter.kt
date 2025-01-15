package com.sagarclientapp.ui.child.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ItemGradesBinding
import com.sagarclientapp.model.BusListing
import com.sagarclientapp.model.GradesList
import com.sagarclientapp.utils.SharedPref

class GradesAdapter(
    private val requireActivity: FragmentActivity,
    val gradesList: MutableList<GradesList>,
    onItemClick: onItemClick
) :
    RecyclerView.Adapter<GradesAdapter.FindMyTrainViewHolder>() {

    private lateinit var binding: ItemGradesBinding
    private var selectedItemClickLis: onItemClick? = null
    private var selectedItemPosition: Int = -1

    // Update data and reset selection
    fun updateData(newBusList: List<GradesList>) {
        gradesList.clear()
        gradesList.addAll(newBusList)
        selectedItemPosition = -1 // Reset selection
        notifyDataSetChanged()
    }

    interface onItemClick {
        fun onClick(id: Int?)
    }

    init {
        selectedItemClickLis = onItemClick
    }


    inner class FindMyTrainViewHolder(var binding: ItemGradesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindMyTrainViewHolder {
        binding = ItemGradesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindMyTrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FindMyTrainViewHolder, position: Int) {

        val binding = holder.binding

       // binding.llGradesBack.background = null
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

            binding.tvGradeName.text = gradesList[position].name
        }
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvGradeName.text = gradesList[position].nameAr

        }


            if (position == selectedItemPosition) {
            binding.llGradesBack.setBackgroundResource(R.drawable.stroke_theme_blue)
            binding.tvGrade1.setTextColor(requireActivity.getColor(R.color.theme_blue))
            binding.tvGradeName.setTextColor(requireActivity.getColor(R.color.theme_blue))
        } else {
            binding.llGradesBack.background = null
            binding.tvGrade1.setTextColor(requireActivity.getColor(R.color.gray_7E7E7E))
            binding.tvGradeName.setTextColor(requireActivity.getColor(R.color.gray_7E7E7E))
        }

        binding.llGrades.setOnClickListener {
           /* binding.llGradesBack.setBackgroundResource(R.drawable.stroke_theme_blue)
            binding.tvGrade1.setTextColor(requireActivity.getColor(R.color.theme_blue))
            binding.tvGradeName.setTextColor(requireActivity.getColor(R.color.theme_blue))*/

            val previousSelectedPosition = selectedItemPosition
            selectedItemPosition = position

            // Notify the adapter to refresh the previous selected item and the current item
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedItemPosition)
            selectedItemClickLis?.onClick(gradesList[position].id)
        }


    }

    override fun getItemCount(): Int {
        return gradesList.size
    }

}