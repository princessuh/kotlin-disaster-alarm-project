package com.example.disasteralert

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

// 설정 화면 - 회원가입 중 기입한 내용을 변경할 때 사용

class SettingsActivity : BaseActivity() {

    private lateinit var switchPush: Switch
    private lateinit var seekbarRadius: SeekBar
    private lateinit var tvRadiusValue: TextView
    private lateinit var cbAll: CheckBox
    private lateinit var disasterCheckBoxes: List<CheckBox>
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var saveBtn: Button
    private lateinit var spinnerLanguage: Spinner

    private var isLanguageInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPrefs = getSharedPreferences("Settings", MODE_PRIVATE)

        // UI 요소 초기화
        switchPush = findViewById(R.id.switch_push)
        seekbarRadius = findViewById(R.id.seekbar_radius)
        tvRadiusValue = findViewById(R.id.tv_radius_value)
        cbAll = findViewById(R.id.cb_all)
        spinnerLanguage = findViewById(R.id.spinner_language)

        // 언어 설정 스피너 초기화
        setupLanguageSpinner()

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
        tvRadiusValue.text = getString(R.string.radius_format, seekbarRadius.progress)

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
                tvRadiusValue.text = getString(R.string.radius_format, progress)
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

    private fun setupLanguageSpinner() {
        // 1. 현재 설정된 언어 가져오기
        val currentLanguage = LocaleHelper.getLanguage(this)

        // 2. 스피너에 표시할 언어 목록 (strings.xml 리소스 사용: "한국어", "English")
        // LocaleHelper의 인덱스 순서(0: ko, 1: en)와 일치해야 합니다.
        val languages = listOf(
            getString(R.string.language_korean),
            getString(R.string.language_english)
        )

        // 3. 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        spinnerLanguage.adapter = adapter

        // 4. 현재 언어에 맞게 스피너 선택
        spinnerLanguage.setSelection(LocaleHelper.getLanguageIndex(currentLanguage))

        // 5. 선택 리스너 설정
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 초기화 시 자동 호출 방지
                if (!isLanguageInitialized) {
                    isLanguageInitialized = true
                    return
                }

                val selectedLanguage = LocaleHelper.getLanguageFromIndex(position)

                // 선택된 언어가 현재 언어와 다를 경우에만 변경 진행
                if (selectedLanguage != LocaleHelper.getLanguage(this@SettingsActivity)) {
                    // 언어 설정 저장 및 Context 업데이트
                    LocaleHelper.setLocale(this@SettingsActivity, selectedLanguage)

                    // 변경된 언어 리소스를 즉시 적용하기 위해 액티비티 재생성
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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