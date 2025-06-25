package com.example.disasteralert

/**
 * 수신 메시지 데이터를 담는 클래스
 */
data class Message(
    val sender: String,          // 발신자 정보 (ex: 기상청)
    val sentTime: String,        // 발송 시각 (ex: 2025/06/25 15:00)
    val title: String,           // 메시지 제목
    val content: String,         // 메시지 본문
    val category: String         // 정보 유형 (기사, 특보, 제보 등)
)