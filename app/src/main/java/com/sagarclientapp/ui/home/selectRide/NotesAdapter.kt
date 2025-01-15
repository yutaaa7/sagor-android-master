package com.sagarclientapp.ui.home.selectRide

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sagarclientapp.databinding.ItemNotesBinding
import com.sagarclientapp.model.NotesList
import com.sagarclientapp.utils.SharedPref

class NotesAdapter(private val activity: Activity, val data: NotesList?) :
    RecyclerView.Adapter<NotesAdapter.CommonViewHolder>() {

    private lateinit var binding: ItemNotesBinding


    inner class CommonViewHolder(var binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

            binding.tvNotes.text = data?.descriptionEn
        }

        if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
            binding.tvNotes.text = data?.descriptionAr

        }


    }

    override fun getItemCount(): Int {
        return 1

    }
}