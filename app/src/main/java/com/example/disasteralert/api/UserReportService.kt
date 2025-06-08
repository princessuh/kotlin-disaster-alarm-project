package com.example.disasteralert.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserReportService {
    @POST("userReport")
    fun submitReport(@Body report: UserReportRequest): Call<Void>
}
