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

        fabReport.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }

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
                        getString(R.string.time_none)
                    }
                } catch (e2: Exception) {
                    Log.e("TimeFormat", "❌ fallback 파싱 실패: ${report.report_time}", e2)
                    report.report_time ?: getString(R.string.time_none)
                }
            }

            val message = Message(
                sender = report.report_location,
                sentTime = formattedTime,
                title = "제보: ${report.middle_type}-${report.small_type}",
                content = report.report_content,
                category = "제보",
                id = report.report_id,
                visible = report.visible
            )

            val bottomSheet = MessageDetailBottomSheetReport(message) {}
            bottomSheet.show(supportFragmentManager, "ReportDetail")
        }

        recyclerView.adapter = adapter

        loadReportsFromServer()

        // ✅ 필터 버튼 클릭
        btnFilter.setOnClickListener {
            val dialog = FilterBottomSheetDialog { disasters, province, city, district, period ->

                // ✅ 선택된 필터 요약 표시
                val resultText = buildString {
                    if (disasters.isNotEmpty()) append("${getString(R.string.filter_disaster, disasters.joinToString(", "))}  ")
                    if (!province.isNullOrBlank()) append("${getString(R.string.filter_region, "$province $city $district")}  ")
                    if (!period.isNullOrBlank()) append(getString(R.string.filter_period, period))
                    if (isEmpty()) append(getString(R.string.filter_view_all))
                }
                selectedDisastersTextView.text = resultText

                // ✅ 재난 유형 코드 변환
                val filteredCodes = disasters
                    .map { mapDisasterToCode(it).toString() }
                    .filter { it != "-1" }

                Log.d("필터", "✅ 선택된 코드: $filteredCodes")
                Log.d("필터", "✅ 지역: $province $city $district, 기간: $period")

                // ✅ 기간 계산
                val now = OffsetDateTime.now()
                val filterTime = when (period) {
                    getString(R.string.period_1month) -> now.minusMonths(1)
                    getString(R.string.period_1week) -> now.minusWeeks(1)
                    getString(R.string.period_1day) -> now.minusDays(1)
                    else -> null // 전체 기간
                }

                // ✅ 필터링
                val filteredReports = reportList.filter { report ->
                    var match = true

                    // 1️⃣ 재난 유형 필터 — 선택 없으면 전체 허용
                    if (filteredCodes.isNotEmpty()) {
                        match = match && (report.small_type in filteredCodes)
                    }

                    // 2️⃣ 지역 필터 — 선택 없으면 전국 허용
                    if (!province.isNullOrBlank()) {
                        match = match && report.report_location?.contains(province) == true
                    }
                    if (!city.isNullOrBlank()) {
                        match = match && report.report_location?.contains(city) == true
                    }
                    if (!district.isNullOrBlank()) {
                        match = match && report.report_location?.contains(district) == true
                    }

                    // 3️⃣ 기간 필터 — 선택 없으면 전체 기간 허용
                    if (filterTime != null && !report.report_time.isNullOrBlank()) {
                        try {
                            val reportTime = OffsetDateTime.parse(
                                report.report_time.replace(" ", "T") + "+09:00"
                            )
                            match = match && (reportTime.isAfter(filterTime) || reportTime.isEqual(filterTime))
                        } catch (e: Exception) {
                            Log.e("필터", "❌ 시간 파싱 오류: ${report.report_time}", e)
                        }
                    }

                    match
                }

                adapter.updateData(filteredReports)
            }

            dialog.show(supportFragmentManager, "FilterBottomSheet")
        }
    }

    // ✅ 서버 데이터 로드
    private fun loadReportsFromServer() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val localUserId = prefs.getString("user_id", null)

        Log.d("ReportHistory", "📌 SharedPreferences user_id = $localUserId")

        if (localUserId.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.error_no_login_info), Toast.LENGTH_SHORT).show()
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

    // ✅ 재난 이름 → 코드 변환
    private fun mapDisasterToCode(type: String): Int {
        return when (type) {
            "태풍" -> 31
            "호우" -> 32
            "홍수" -> 33
            "강풍" -> 34
            "대설" -> 35
            "폭염" -> 41
            "한파" -> 42
            "지진" -> 51
            "산불" -> 61
            "일일화재" -> 62
            "감염병", "미세먼지" -> 11
            else -> -1
        }
    }
}
