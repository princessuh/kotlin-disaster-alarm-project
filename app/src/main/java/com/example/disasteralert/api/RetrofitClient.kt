package com.example.disasteralert.api

// [기능] Retrofit 인스턴스를 싱글턴으로 생성하여 API 서비스 객체 제공
// [설명] DisasterCollect 인터페이스를 구현한 Retrofit API 클라이언트 설정

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://61.245.248.197:8000/"  // 재난 정보 제공 API 기본 URL

    val apiService: DisasterCollect by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DisasterCollect::class.java)
    }
}