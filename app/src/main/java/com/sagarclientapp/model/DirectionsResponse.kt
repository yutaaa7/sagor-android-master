package com.sagarclientapp.model

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val distance: DistanceCal
)

data class DistanceCal(
    val text: String,
    val value: Int
)
