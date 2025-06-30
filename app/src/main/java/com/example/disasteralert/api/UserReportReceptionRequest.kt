package com.example.disasteralert.api


data class UserReportReceptionRequest(
    val userId: String,
    val from_time: String,
    val to_time: String,
    val days: Int
)