package com.example.disasteralert.api

import java.util.*

data class RtdVoteRequest(
    val rtd_time: String,
    val rtd_id: UUID,
    val user_id: String
)
