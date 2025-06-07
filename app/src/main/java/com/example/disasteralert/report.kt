package com.example.disasteralert

/**
 * 한 건의 제보 정보를 담는 데이터 클래스
 */
data class Report(
    val locationTime: String,        // ex) "서울 강남구 • 2024/11/14 07:30"
    val customTag: String,           // et_custom_tag, ex) "#호우"
    val content: String,             // 제보 제목
    val recommendedTags: List<String>// 추천 태그 리스트, ex) ["풍수해"]
)
