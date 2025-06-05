package com.example.disasteralert

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// 설정 화면 - 회원가입 중 기입한 내용을 변경할 때 사용

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchPush: Switch
    private lateinit var seekbarRadius: SeekBar
    private lateinit var tvRadiusValue: TextView
    private lateinit var cbAll: CheckBox
    private lateinit var disasterCheckBoxes: List<CheckBox>
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var saveBtn: Button

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
        saveBtn = findViewById(R.id.btn_save)

        // 기존 설정 불러오기
        switchPush.isChecked = sharedPrefs.getBoolean("push_notifications", true)
        seekbarRadius.progress = sharedPrefs.getInt("news_radius", 10)
        tvRadiusValue.text = "반경: ${seekbarRadius.progress}km"

        // 체크박스 설정 로드 & 스타일 적용
        disasterCheckBoxes.forEachIndexed { index, checkBox ->
            checkBox.isChecked = sharedPrefs.getBoolean("disaster_$index", true)
            updateCheckBoxStyle(checkBox, checkBox.isChecked)
        }

        // "전체" 체크박스 상태 업데이트 (초기 스타일 적용)
        cbAll.isChecked = disasterCheckBoxes.all { it.isChecked }
        updateCheckBoxStyle(cbAll, cbAll.isChecked)

        // 체크박스 UI 및 값 저장 설정
        disasterCheckBoxes.forEachIndexed { index, checkBox ->
            checkBox.setButtonDrawable(android.R.color.transparent)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                updateCheckBoxStyle(checkBox, isChecked)
                // ✅ 변경 즉시 저장
                sharedPrefs.edit().putBoolean("disaster_$index", isChecked).apply()
                updateAllCheckBox()
            }
        }

        // "전체" 체크박스 UI 설정
        cbAll.setButtonDrawable(android.R.color.transparent)
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            setAllCheckBoxes(isChecked)
        }

        // 스위치 / 반경 SeekBar 저장 로직 (기존 유지)
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

        // 저장 버튼 클릭 이벤트 (프로필 화면으로 이동)
        saveBtn.setOnClickListener {
            saveAllDisasterSelections() // 혹시 모를 누락 대비
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    /** 모든 체크박스 상태를 SharedPreferences 에 저장 */
    private fun saveAllDisasterSelections() {
        val editor = sharedPrefs.edit()
        disasterCheckBoxes.forEachIndexed { index, cb ->
            editor.putBoolean("disaster_$index", cb.isChecked)
        }
        editor.apply()
    }

    // 체크박스 스타일 업데이트 함수 (전체 체크박스도 변경 가능)
    private fun updateCheckBoxStyle(checkBox: CheckBox, isChecked: Boolean) {
        checkBox.setBackgroundResource(
            if (isChecked) R.drawable.bg_checkbox_selected else R.drawable.bg_checkbox_unselected
        )
        checkBox.setTextColor(
            if (isChecked) Color.parseColor("#007AFF") else Color.parseColor("#757575")
        )
    }

    // "전체" 체크박스 상태 업데이트
    private fun updateAllCheckBox() {
        cbAll.setOnCheckedChangeListener(null)
        cbAll.isChecked = disasterCheckBoxes.all { it.isChecked }
        updateCheckBoxStyle(cbAll, cbAll.isChecked)
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            setAllCheckBoxes(isChecked)
        }
    }

    // "전체 선택/해제" 처리 + 즉시 저장
    private fun setAllCheckBoxes(isChecked: Boolean) {
        disasterCheckBoxes.forEachIndexed { index, checkBox ->
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = isChecked
            updateCheckBoxStyle(checkBox, isChecked)
            // ✅ 일괄 저장
            sharedPrefs.edit().putBoolean("disaster_$index", isChecked).apply()
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                updateCheckBoxStyle(checkBox, isChecked)
                sharedPrefs.edit().putBoolean("disaster_$index", isChecked).apply()
                updateAllCheckBox()
            }
        }
        updateCheckBoxStyle(cbAll, isChecked)
        saveAllDisasterSelections() // 최종 저장 안전망
    }
}