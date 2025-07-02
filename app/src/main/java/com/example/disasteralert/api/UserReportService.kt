package com.example.disasteralert.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserReportService {
    @Headers("Content-Type: application/json")
    @POST("userReport")
    fun submitReport(@Body report: UserReportRequest): Call<Void>
}
