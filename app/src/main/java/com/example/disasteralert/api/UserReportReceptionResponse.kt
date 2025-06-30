// UserReportReceptionResponse.kt
package com.example.disasteralert.api

data class UserReportReceptionResponse(
    val count: Int,
    val results: List<ReportDetail>
)

data class ReportDetail(
    val report_id: String,
    val report_time: String,
    val middle_type: String,
    val small_type: String,
    val report_location: String,
    val report_content: String,
    val latitude: Double,
    val longitude: Double,
    val visible: Boolean,
    val delete_vote: Int
)