package com.example.disasteralert.api

data class RtdEvent(
    val type: String,                    // "rtd" 또는 "report"

    // rtd 타입 필드
    val id: String,
    val rtd_loc: String? = null,
    val rtd_details: List<String>? = null,
    val rtd_code: Int? = null,
    val regioncode: Long? = null,

    // report 타입 필드
    val report_location: String? = null,
    val middle_type: String? = null,
    val small_type: String? = null,
    val content: String? = null,
    val report_by: String? = null,
    val delete_vote: Int? = null,

    // 공통 필드
    val latitude: Double? = null,
    val longitude: Double? = null,
    val time: String,
    val visible: Boolean? = null
)
