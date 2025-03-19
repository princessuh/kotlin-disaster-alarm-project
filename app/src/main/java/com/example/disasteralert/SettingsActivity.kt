package com.example.disasteralert

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchPush: Switch
    private lateinit var seekbarRadius: SeekBar
    private lateinit var tvRadiusValue: TextView
    private lateinit var cbAll:CheckBox
    private lateinit var disasterCheckBoxes: List<CheckBox>
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPrefs = getSharedPreferences("Settings", MODE_PRIVATE)

        // UI 요소 초기화
        switchPush = findViewById(R.id.switch_push)
        seekbarRadius = findViewById(R.id.seekbar_radius)
        tvRadiusValue = findViewById(R.id.tv_radius_value)
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

        // 기존 설정 불러오기
        switchPush.isChecked = sharedPrefs.getBoolean("push_notifications", true)
        seekbarRadius.progress = sharedPrefs.getInt("news_radius", 10)
        tvRadiusValue.text = "반경: ${seekbarRadius.progress}km"

        // 체크박스 설정 로드
        disasterCheckBoxes.forEachIndexed { index, checkBox ->
            checkBox.isChecked = sharedPrefs.getBoolean("disaster_$index", true)
        }

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

        // 이벤트 리스너 설정
        switchPush.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("push_notifications", isChecked).apply()
        }

        seekbarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvRadiusValue.text = "반경: ${progress}km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                sharedPrefs.edit().putInt("news_radius", seekBar.progress).apply()
            }
        })
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
