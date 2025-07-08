package com.example.disasteralert.api

import java.util.UUID


data class ReportDeleteRequest(
    val report_id: UUID,
    val user_id: String
)
