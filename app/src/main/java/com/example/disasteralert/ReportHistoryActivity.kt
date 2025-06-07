package com.example.disasteralert

import android.os.Bundle
import android.widget.*
import com.google.android.material.button.MaterialButton
import android.content.Intent
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager


// 제보 내역 리스트

class ReportHistoryActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var selectedDisastersTextView: TextView
    private lateinit var fabReport: ExtendedFloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private val reportList = mutableListOf<Report>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        // 네비게이션 바
        setupBottomNavigation(R.id.bottom_navigation, "ReportHistoryActivity")

        // UI 요소 초기화

        btnFilter = findViewById(R.id.btn_filter)
        selectedDisastersTextView = findViewById(R.id.tv_selected_disasters)
        fabReport = findViewById(R.id.fabReport)

        recyclerView = findViewById(R.id.recyclerReportHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadDummyReports()
        recyclerView.adapter = ReportAdapter(reportList)


        // 필터 팝업 띄우기
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

        // FAB 클릭 시 PostActivity로 이동
        fabReport.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
    }
    // 더미 데이터 로드 (나중에 실제 DB/API로 교체)
    private fun loadDummyReports() {
        reportList.apply {
            add(
                Report(
                    locationTime = "서울 강남구 • 2024/11/14 07:30",
                    customTag = "#호우",
                    content = "출근길에 봤는데, 구름이 끼어있는게 곧 비가 올 것 같아요",
                    recommendedTags = listOf("풍수해")
                )
            )
            // TODO: 추가 항목
        }
    }
}