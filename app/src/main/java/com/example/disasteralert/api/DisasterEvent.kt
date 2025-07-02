package com.example.disasteralert.api

// [기능] 재난 API 응답 JSON을 매핑하는 데이터 클래스 정의
// [설명] 서버로부터 수신한 재난 이벤트 정보를 객체로 구조화

data class DisasterEvent(
    val disaster_id: String,    // 재난 알람 고유 ID
    val description: String,    // 재난 상세 설명
    val disaster_time: String,  // 재난 발생 시간
    val disaster_type: String,  // 재난 유형 타입
    val latitude: Double,       // 재난 발생 위치 위도
    val longitude: Double       // 재난 발생 위치 경도
)

data class DisasterResponse(
    val events: List<DisasterEvent>,
    val count: Int
)