package com.example.disasteralert.api

import UserReportReceptionService
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "http://61.245.248.197:8000/"

    val apiService: DisasterCollect by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("FULL_URL", "apiService baseUrl = ${retrofit.baseUrl()}")

        retrofit.create(DisasterCollect::class.java)
    }

    val userReportService: UserReportService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("FULL_URL", "userReportService baseUrl = ${retrofit.baseUrl()}")

        retrofit.create(UserReportService::class.java)
    }

    val userReportReceptionService: UserReportReceptionService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UserReportReceptionService::class.java)
    }
}
