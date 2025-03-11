package com.example.disasteralert

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ReportHistoryActivity : AppCompatActivity() {

    private lateinit var toggleButton: ToggleButton
    private lateinit var disasterListLayout: LinearLayout
    private lateinit var btnFilter: Button
    private lateinit var selectedDisastersTextView: TextView
    private lateinit var disasterButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_history)

        // UI 요소 초기화
        toggleButton = findViewById(R.id.toggle_disaster)
        disasterListLayout = findViewById(R.id.disaster_list_layout)
        btnFilter = findViewById(R.id.btn_filter)
        selectedDisastersTextView = findViewById(R.id.tv_selected_disasters)

        disasterButtons = listOf(
            findViewById(R.id.btn_typhoon),
            findViewById(R.id.btn_weather),
            findViewById(R.id.btn_earthquake),
            findViewById(R.id.btn_epidemic),
            findViewById(R.id.btn_fire),
            findViewById(R.id.btn_fine_dust)
        )

        // 버튼 클릭 시 색상 변경 및 선택 여부 토글
        val selectedDisasters = mutableSetOf<String>()

        disasterButtons.forEach { button ->
            button.setOnClickListener {
                if (selectedDisasters.contains(button.text.toString())) {
                    // 선택 해제 (회색)
                    selectedDisasters.remove(button.text.toString())
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_50))
                    button.setTextColor(Color.WHITE)
                } else {
                    // 선택 (파란색)
                    selectedDisasters.add(button.text.toString())
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_50))
                    button.setTextColor(Color.WHITE)
                }
            }
        }

        // 필터 버튼 클릭 시 선택한 재난 목록 표시
        btnFilter.setOnClickListener {
            val displayText = if (selectedDisasters.isEmpty()) "적용된 필터: 없음"
            else "적용된 필터: ${selectedDisasters.joinToString(", ")}"
            selectedDisastersTextView.text = displayText
        }

        // 토글 버튼 클릭 시 목록 보이기/숨기기
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            disasterListLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }
}
