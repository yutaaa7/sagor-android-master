package com.sagarclientapp.ui.home.selectRide

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.sagarclientapp.databinding.ItemPlaceDataBinding

class PlacesDataAdapter(
    private val activity: Activity,
    val predictions: MutableList<AutocompletePrediction>,
    listener: OnItemClickListener,
    val country_code: String?
) :
    RecyclerView.Adapter<PlacesDataAdapter.CommonViewHolder>() {
    private var selectListlistener: OnItemClickListener? = null
    private lateinit var binding: ItemPlaceDataBinding

    init {

        selectListlistener = listener

    }

    inner class CommonViewHolder(var binding: ItemPlaceDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface OnItemClickListener {
        fun onItemClick(place: Place)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        binding =
            ItemPlaceDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommonViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val binding = holder.binding
        Log.d("PREDICTIONS", "Predictions: ${predictions[position].getFullText(null).toString()}")

        binding.tvFullAddress.text = predictions[position].getFullText(null).toString()
        binding.tvPrimaryLocation.text = predictions[position].getPrimaryText(null).toString()
        binding.ivfav.visibility = View.GONE

        if (activity is MapScreenActivity) {
            binding.llDistanceView.visibility = View.GONE
        } else {
            binding.llDistanceView.visibility = View.VISIBLE

        }

        binding.llPlace.setOnClickListener {

            if (activity is MapScreenActivity) {
                fetchPlaceDetails(predictions[position])


            }
        }


    }

    private fun fetchPlaceDetails(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId
        val placeFields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        val placesClient = Places.createClient(activity)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
            selectListlistener?.onItemClick(place)

        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                Log.e("PLACE", "Place not found: ${exception.statusCode} ${exception.message}")
            }
        }
    }

    override fun getItemCount(): Int {
        return predictions.size

    }
}