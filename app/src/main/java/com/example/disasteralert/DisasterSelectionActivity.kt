package com.example.disasteralert

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DisasterSelectionActivity : AppCompatActivity() {

    private lateinit var cbAll: CheckBox
    private lateinit var disasterCheckBoxes: List<CheckBox>
    private lateinit var tvSkip: TextView
    private lateinit var btnComplete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_selection)

        // 체크박스 초기화
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

        // 체크박스 UI 숨기기
        disasterCheckBoxes.forEach { checkBox ->
            checkBox.setButtonDrawable(android.R.color.transparent) // 기본 체크박스 제거
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                updateCheckBoxStyle(checkBox, isChecked)
                updateAllCheckBox()
            }
        }

        // "전체" 체크박스 UI 숨기기
        cbAll.setButtonDrawable(android.R.color.transparent)

        // "전체" 체크박스 클릭 시 모든 항목 선택/해제 + 스타일 업데이트
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            disasterCheckBoxes.forEach { checkBox ->
                checkBox.setOnCheckedChangeListener(null) // 리스너 임시 해제
                checkBox.isChecked = isChecked
                updateCheckBoxStyle(checkBox, isChecked) // 스타일 업데이트 추가
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    updateCheckBoxStyle(checkBox, isChecked)
                    updateAllCheckBox()
                }
            }
            updateCheckBoxStyle(cbAll, isChecked) //전체 체크박스 스타일 변경
        }

        // 선택 완료 버튼 클릭 시 프로필 화면으로 이동 (🩷추후 메인 이동으로 변경 필요)
        btnComplete.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java) // 이동할 화면 설정
            startActivity(intent)
            finish()
        }

        // 건너뛰기 버튼 클릭 시 프로필 화면으로 이동 (🩷추후 메인 이동으로 변경 필요)
        tvSkip.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java) // 이동할 화면 설정
            startActivity(intent)
            finish()
        }
    }

    // 체크박스 스타일 업데이트 함수 (전체 체크박스도 변경 가능)
    private fun updateCheckBoxStyle(checkBox: CheckBox, isChecked: Boolean) {
        checkBox.setBackgroundResource(R.drawable.checkbox_selector)
        checkBox.setTextColor(if (isChecked) Color.parseColor("#007AFF") else Color.parseColor("#757575"))
    }

    // "전체" 체크박스 상태 업데이트 (무한 루프 방지 적용)
    private fun updateAllCheckBox() {
        cbAll.setOnCheckedChangeListener(null) // 리스너 해제
        cbAll.isChecked = disasterCheckBoxes.all { it.isChecked }

        // ⭐️ 모든 체크박스 스타일 업데이트 추가
        disasterCheckBoxes.forEach { checkBox ->
            updateCheckBoxStyle(checkBox, checkBox.isChecked)
        }

        // ⭐️ 전체 체크박스 스타일도 업데이트
        updateCheckBoxStyle(cbAll, cbAll.isChecked)

        cbAll.setOnCheckedChangeListener { _, isChecked ->
            disasterCheckBoxes.forEach { checkBox ->
                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = isChecked
                updateCheckBoxStyle(checkBox, isChecked) // ⭐️ 스타일 업데이트 추가
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    updateCheckBoxStyle(checkBox, isChecked)
                    updateAllCheckBox()
                }
            }
            updateCheckBoxStyle(cbAll, isChecked) // ⭐️ 전체 체크박스 스타일 업데이트 추가
        }
    }
}
