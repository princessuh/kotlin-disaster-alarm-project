package com.example.disasteralert.api

data class UserReportRequest(
    val userId: String,             // 로그인된 사용자 ID
    val disasterType: String,          // "태풍" → 31, 정수 코드
    val disasterTime: String?,      // yyyy-MM-dd'T'HH:mm:ss
    val reportContent: String?,     // 제보 내용
    val disasterPos: String?,       // 텍스트 위치 정보 (예: 서울특별시 강남구 역삼동)
    val latitude: Float?,          // 위도
    val longitude: Float?          // 경도
)
