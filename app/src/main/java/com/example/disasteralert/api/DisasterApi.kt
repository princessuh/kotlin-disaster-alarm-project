package com.example.disasteralert.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface DisasterApi {

    @GET("rtd/search")
    suspend fun searchDisaster(
        @Query("rtd_loc") rtdLoc: String? = null,
        @Query("days") days: Int? = 1,
        @Query("sort") sort: String? = "desc"
    ): Response<SearchResult>
}
