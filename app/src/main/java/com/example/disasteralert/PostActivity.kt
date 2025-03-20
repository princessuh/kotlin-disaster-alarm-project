package com.example.disasteralert

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.widget.CheckBox

class PostActivity : AppCompatActivity() {

    private lateinit var tvLocationTime: TextView
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var disasterCheckBoxes: List<CheckBox>
    private lateinit var etCustomTag: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var btnSubmit: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        etTitle = findViewById(R.id.et_title)
        etContent = findViewById(R.id.et_content)
        tvLocationTime = findViewById(R.id.tv_location_time)
        etCustomTag = findViewById(R.id.et_custom_tag)
        chipGroup = findViewById(R.id.chip_group)
        btnSubmit = findViewById(R.id.btn_submit)
        disasterCheckBoxes = listOf(
            findViewById(R.id.cb_typhoon),
            findViewById(R.id.cb_weather),
            findViewById(R.id.cb_earthquake),
            findViewById(R.id.cb_epidemic),
            findViewById(R.id.cb_fire),
            findViewById(R.id.cb_fine_dust)
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 및 시간 자동 설정
        setLocationAndTime()

        // 체크박스 UI 숨기기
        disasterCheckBoxes.forEach { checkBox ->
            checkBox.setButtonDrawable(android.R.color.transparent) // 기본 체크박스 제거
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                updateCheckBoxStyle(checkBox, isChecked)
            }
        }

        // 추천 태그 추가
        val recommendedTags = listOf("재난", "화재", "지진", "태풍", "홍수")
        for (tag in recommendedTags) {
            addChip(tag)
        }

        // 사용자 입력 태그 추가
        etCustomTag.setOnEditorActionListener { _, _, _ ->
            val tagText = etCustomTag.text.toString().trim()
            if (tagText.isNotEmpty()) {
                addChip(tagText)
                etCustomTag.text.clear()
            }
            true
        }

        // 등록 버튼 클릭 이벤트
        btnSubmit.setOnClickListener {
            Toast.makeText(this, "게시물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 위치 및 시간 설정
    private fun setLocationAndTime() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val time = System.currentTimeMillis()
                tvLocationTime.text = "위치: ${location.latitude}, ${location.longitude} | 시간: $time"
            } else {
                tvLocationTime.text = "위치 정보를 가져올 수 없습니다."
            }
        }
        setContentView(R.layout.activity_post)
    }

    // 태그 추가 함수
    private fun addChip(tag: String) {
        val chip = Chip(this)
        chip.text = tag
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

    // 체크박스 스타일 업데이트 함수 (전체 체크박스도 변경 가능)
    private fun updateCheckBoxStyle(checkBox: CheckBox, isChecked: Boolean) {
        checkBox.setBackgroundResource(R.drawable.checkbox_selector)
        checkBox.setTextColor(if (isChecked) Color.parseColor("#007AFF") else Color.parseColor("#757575"))
    }
}
