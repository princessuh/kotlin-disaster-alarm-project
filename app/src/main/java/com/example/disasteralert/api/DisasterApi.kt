package com.example.disasteralert.api

import android.app.appsearch.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface DisasterApi {

    @GET("rtd/search")
    suspend fun searchDisaster(
        @Query("rtd_loc") rtdLoc: String? = null,
        @Query("regioncode") regioncode: Int? = null,
        @Query("rtd_code") rtdCode: Int? = null,
        @Query("from_time") fromTime: String? = null,
        @Query("to_time") toTime: String? = null,
        @Query("days") days: Int? = 1,
        @Query("sort") sort: String? = "desc"
    ): Response<SearchResult>
}
