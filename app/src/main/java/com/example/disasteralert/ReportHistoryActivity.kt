package com.example.disasteralert

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ReportHistoryActivity : AppCompatActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var selectedDisastersTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        // ✅ UI 요소 초기화

        btnFilter = findViewById(R.id.btn_filter)
        selectedDisastersTextView = findViewById(R.id.tv_selected_disasters)


        // ✅ 필터 팝업 띄우기
        btnFilter.setOnClickListener {
            val dialog = FilterBottomSheetDialog { filtered ->
                val result = if (filtered.isEmpty()) {
                    "적용된 필터: 없음"
                } else {
                    "적용된 필터: ${filtered.joinToString(", ")}"
                }
                selectedDisastersTextView.text = result

                // 💡 이 아래에 RecyclerView나 리스트 필터링 로직 넣을 수 있어
            }

            dialog.show(supportFragmentManager, "FilterBottomSheet")

        }
    }
}
