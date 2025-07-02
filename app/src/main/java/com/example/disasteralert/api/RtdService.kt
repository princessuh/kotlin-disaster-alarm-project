package com.example.disasteralert.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RtdService {
    @GET("rtd/search")
    fun getRtdEvents(
        @Query("days") days: Int = 1,
        @Query("sort") sort: String = "desc"
    ): Call<RtdResponse>
}
