package com.example.disasteralert.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface ReportService {

    @POST("report/delete")
    fun deleteReport(@Body request: ReportDeleteRequest): Call<Void>

    @POST("report/vote_by_id")
    fun voteReport(@Body request: VoteRequest): Call<Void>

    @POST("rtd/vote")
    fun voteRtd(@Body request: RtdVoteRequest): Call<Void>
}
