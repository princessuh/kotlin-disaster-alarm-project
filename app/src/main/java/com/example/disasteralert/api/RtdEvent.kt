package com.example.disasteralert.api

data class RtdEvent(
    val id: String,
    val rtd_loc: String,
    val rtd_details: List<String>,
    val latitude: Double?,
    val longitude: Double?,
    val time: String
)

data class RtdResponse(
    val count: Int,
    val results: List<RtdEvent>
)
