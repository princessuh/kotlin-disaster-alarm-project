package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // UI 요소 초기화
        val tvNickname = findViewById<TextView>(R.id.tv_nickname)
        val tvJoinDate = findViewById<TextView>(R.id.tv_join_date)
        val btnSettings = findViewById<Button>(R.id.btn_settings)
        val btnEditProfile = findViewById<Button>(R.id.btn_edit_profile)
        val btnReportHistory = findViewById<Button>(R.id.btn_report_history)
        val ivProfile = findViewById<ImageView>(R.id.iv_profile) // 프로필 이미지

        // 기본 프로필 이미지 설정
        ivProfile.setImageResource(R.drawable.ic_launcher_foreground) // 없으면 기본 이미지로 설정

        // 임시 사용자 정보 설정
        tvNickname.text = getString(R.string.default_nickname)
        tvJoinDate.text = getString(R.string.default_join_date)

        // 설정 화면으로 이동
        btnSettings.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // 개인정보 수정 화면으로 이동
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        btnReportHistory.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
