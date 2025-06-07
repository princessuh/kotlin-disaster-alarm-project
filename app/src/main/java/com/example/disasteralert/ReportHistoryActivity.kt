package com.example.disasteralert

import android.os.Bundle
import android.widget.*
import com.google.android.material.button.MaterialButton
import android.content.Intent
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


// 제보 내역 리스트

class ReportHistoryActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var selectedDisastersTextView: TextView
    private lateinit var fabReport: ExtendedFloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        // 네비게이션 바
        setupBottomNavigation(R.id.bottom_navigation, "ReportHistoryActivity")

        // UI 요소 초기화

        btnFilter = findViewById(R.id.btn_filter)
        selectedDisastersTextView = findViewById(R.id.tv_selected_disasters)
        fabReport = findViewById(R.id.fabReport)


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
}