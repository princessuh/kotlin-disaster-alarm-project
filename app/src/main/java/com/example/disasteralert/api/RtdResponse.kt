package com.example.disasteralert.api

data class RtdResponse(
    val count: Int,
    val results: List<RtdEvent>
)
