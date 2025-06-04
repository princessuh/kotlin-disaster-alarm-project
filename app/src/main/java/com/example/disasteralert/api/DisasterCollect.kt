package com.example.disasteralert.api

// [기능] Retrofit을 사용하여 재난 이벤트 데이터를 비동기 방식으로 수집
// [설명] "/events" 엔드포인트에서 재난 데이터를 받아오는 API 인터페이스 정의

import retrofit2.Call
import retrofit2.http.GET

interface DisasterCollect {
    @GET("events")
    suspend fun getEvents(): DisasterResponse
}
