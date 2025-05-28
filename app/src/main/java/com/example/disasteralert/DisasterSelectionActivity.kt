package com.example.disasteralert

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DisasterSelectionActivity : AppCompatActivity() {
    /** UI 요소 정의 */
    private lateinit var cbAll: CheckBox
    private lateinit var disasterCheckBoxes: List<CheckBox>
    private lateinit var tvSkip: TextView
    private lateinit var btnComplete: Button
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_selection)

        sharedPrefs = getSharedPreferences("Settings", MODE_PRIVATE)

        cbAll = findViewById(R.id.cb_all)
        disasterCheckBoxes = listOf(
            findViewById(R.id.cb_typhoon),
            findViewById(R.id.cb_weather),
            findViewById(R.id.cb_news),
            findViewById(R.id.cb_earthquake),
            findViewById(R.id.cb_epidemic),
            findViewById(R.id.cb_special_report),
            findViewById(R.id.cb_fire),
            findViewById(R.id.cb_fine_dust),
            findViewById(R.id.cb_report)
        )
        tvSkip = findViewById(R.id.tv_skip)
        btnComplete = findViewById(R.id.btn_complete)

        /** 체크 박스 스타일 & 리스너 설정 */
        disasterCheckBoxes.forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent) // 기본 체크박스 숨기기
            cb.setOnCheckedChangeListener { _, isChecked ->
                updateCheckBoxStyle(cb, isChecked)
                updateAllCheckBox()
            }
        }

        cbAll.setButtonDrawable(android.R.color.transparent)
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            applyAllCheckBoxes(isChecked)
        }

        /** 버튼 이벤트 */
        btnComplete.setOnClickListener {
            saveSelections()
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        tvSkip.setOnClickListener {
            applyAllCheckBoxes(true)  // 건너뛰기 = 전부 수신
            saveSelections()
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    // ────────────────────────────────────────────────────────
    // 체크박스 유틸리티
    // ────────────────────────────────────────────────────────
    /** 개별 체크박스 스타일 적용 */
    private fun updateCheckBoxStyle(cb: CheckBox, isChecked: Boolean) {
        cb.setBackgroundResource(R.drawable.checkbox_selector)
        cb.setTextColor(if (isChecked) Color.parseColor("#007AFF") else Color.parseColor("#757575"))
    }

    /** 모든 체크박스 & '전체' 체크박스 상태/스타일을 한꺼번에 동기화 */
    private fun applyAllCheckBoxes(isChecked: Boolean) {
        // 개별 체크박스 처리
        disasterCheckBoxes.forEach { cb ->
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = isChecked
            updateCheckBoxStyle(cb, isChecked)
            cb.setOnCheckedChangeListener { _, checked ->
                updateCheckBoxStyle(cb, checked)
                updateAllCheckBox()
            }
        }
        // 전체 체크박스 처리
        cbAll.setOnCheckedChangeListener(null)
        cbAll.isChecked = isChecked
        updateCheckBoxStyle(cbAll, isChecked)
        cbAll.setOnCheckedChangeListener { _, checked -> applyAllCheckBoxes(checked) }
    }

    /** 개별 변경 시 '전체' 체크박스 상태를 갱신 */
    private fun updateAllCheckBox() {
        cbAll.setOnCheckedChangeListener(null)
        cbAll.isChecked = disasterCheckBoxes.all { it.isChecked }
        updateCheckBoxStyle(cbAll, cbAll.isChecked)
        cbAll.setOnCheckedChangeListener { _, checked -> applyAllCheckBoxes(checked) }
    }

    /** 현재 선택값을 SharedPreferences("Settings") 에 저장 */
    private fun saveSelections() {
        sharedPrefs.edit().apply {
            disasterCheckBoxes.forEachIndexed { idx, cb ->
                putBoolean("disaster_$idx", cb.isChecked)
            }
            apply()
        }
    }
}
