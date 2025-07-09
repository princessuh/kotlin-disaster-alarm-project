package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.disasteralert.api.ReportDetail
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.UserReportReceptionResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ReportHistoryActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var selectedDisastersTextView: TextView
    private lateinit var fabReport: ExtendedFloatingActionButton
    private lateinit var recyclerView: RecyclerView

    private val reportList = mutableListOf<ReportDetail>()
    private lateinit var adapter: ReportAdapter

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
        adapter = ReportAdapter(reportList) { report ->
            val formattedTime = try {
                OffsetDateTime.parse(report.report_time)
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
            } catch (e: Exception) {
                Log.e("TimeFormat", "❌ 기본 파싱 실패: ${report.report_time}", e)

                try {
                    val fallbackTime = report.report_time?.replace(" ", "T")
                    val fallback = if (fallbackTime != null) "$fallbackTime+09:00" else null

                    if (fallback != null) {
                        OffsetDateTime.parse(fallback)
                            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                    } else {
                        "시간 없음"
                    }
                } catch (e2: Exception) {
                    Log.e("TimeFormat", "❌ fallback 파싱 실패: ${report.report_time}", e2)
                    report.report_time ?: "시간 없음"
                }
            }


            val message = Message(
                sender = report.report_location,
                sentTime = formattedTime,
                title = "제보: ${report.middle_type}-${report.small_type}",
                content = report.report_content,
                category = "제보",
                id = report.report_id,
                visible = false // 제보 내역이므로 '해제' 상태로 고정
            )

            val bottomSheet = MessageDetailBottomSheetReport(message) {}
            bottomSheet.show(supportFragmentManager, "ReportDetail")
        }

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

        Log.d("ReportHistory", "📌 SharedPreferences user_id = $localUserId")

        if (localUserId.isNullOrBlank()) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            Log.e("ReportHistory", "❌ user_id is null or blank")
            return
        }

        RetrofitClient.userReportReceptionService.getUserReportHistory(localUserId, 50)
            .enqueue(object : Callback<UserReportReceptionResponse> {
                override fun onResponse(
                    call: Call<UserReportReceptionResponse>,
                    response: Response<UserReportReceptionResponse>
                ) {
                    Log.d("Retrofit", "✅ 서버 응답 코드: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val details = response.body()!!.results
                        Log.d("Retrofit", "📦 받은 제보 개수: ${details.size}")

                        details.forEachIndexed { index, detail ->
                            Log.d("Report[$index]", "🕒 ${detail.report_time} / 📍 ${detail.report_location}")
                        }

                        reportList.clear()
                        reportList.addAll(details)
                        adapter.notifyDataSetChanged()

                        Log.d("Adapter", "✅ 어댑터 갱신 완료. 현재 리스트 크기: ${reportList.size}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("Retrofit", "❌ 서버 응답 오류: ${response.code()}, 내용: $errorBody")
                    }
                }

                override fun onFailure(call: Call<UserReportReceptionResponse>, t: Throwable) {
                    Log.e("Retrofit", "❌ 서버 연결 실패", t)
                }
            })
    }
}
