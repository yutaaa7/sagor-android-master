package com.sagarclientapp.api

import com.sagarclientapp.model.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsService {

    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): DirectionsResponse
}