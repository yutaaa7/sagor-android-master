package com.sagarclientapp.model

data class DistanceMatrixResponse(
    val rows: List<DistanceMatrixRow>
)

data class DistanceMatrixRow(
    val elements: List<DistanceMatrixElement>
)

data class DistanceMatrixElement(
    val distance: Distance,
    val duration: Duration
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)