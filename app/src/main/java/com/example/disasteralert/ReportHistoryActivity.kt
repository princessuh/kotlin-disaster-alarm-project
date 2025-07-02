package com.example.disasteralert

import com.example.disasteralert.ReportAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.disasteralert.api.ReportDetail
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.UserReportReceptionRequest
import com.example.disasteralert.api.UserReportReceptionResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportHistoryActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var selectedDisastersTextView: TextView
    private lateinit var fabReport: ExtendedFloatingActionButton
    private lateinit var recyclerView: RecyclerView

    private val reportList = mutableListOf<ReportDetail>()
    private lateinit var adapter: ReportAdapter  // 기존 어댑터 이름 그대로 쓰는 것으로 가정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        setupBottomNavigation(R.id.bottom_navigation, "ReportHistoryActivity")

        // UI 연결
        btnFilter = findViewById(R.id.btn_filter)
        selectedDisastersTextView = findViewById(R.id.tv_selected_disasters)
        fabReport = findViewById(R.id.fabReport)
        recyclerView = findViewById(R.id.recyclerReportHistory)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReportAdapter(reportList)
        recyclerView.adapter = adapter

        loadReportsFromServer()

        btnFilter.setOnClickListener {
            val dialog = FilterBottomSheetDialog { filtered ->
                val result = if (filtered.isEmpty()) {
                    "적용된 필터: 없음"
                } else {
                    "적용된 필터: ${filtered.joinToString(", ")}"
                }
                selectedDisastersTextView.text = result
            }
            dialog.show(supportFragmentManager, "FilterBottomSheet")
        }

        fabReport.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
    }

    private fun loadReportsFromServer() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val localUserId = prefs.getString("user_id", null)

        RetrofitClient.userReportReceptionService.getUserReportHistory(localUserId.toString(), 50)
            .enqueue(object : Callback<UserReportReceptionResponse> {
                override fun onResponse(
                    call: Call<UserReportReceptionResponse>,
                    response: Response<UserReportReceptionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val details = response.body()!!.results
                        reportList.clear()
                        reportList.addAll(details)
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("Retrofit", "서버 응답 오류: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserReportReceptionResponse>, t: Throwable) {
                    Log.e("Retrofit", "서버 연결 실패", t)
                }
            })
    }
}
