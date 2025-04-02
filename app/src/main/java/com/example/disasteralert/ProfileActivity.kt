package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging //토큰 확인용


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // UI 요소 초기화
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvUserID = findViewById<TextView>(R.id.tv_user_id)
        val btnSettings = findViewById<Button>(R.id.btn_settings)
        val btnEditProfile = findViewById<Button>(R.id.btn_edit_profile)
        val btnReportHistory = findViewById<Button>(R.id.btn_report_history)
        val btnMessageList = findViewById<Button>(R.id.btn_message_list)
        val ivProfile = findViewById<ImageView>(R.id.iv_profile) // 프로필 이미지

        // 기본 프로필 이미지 설정
        ivProfile.setImageResource(R.mipmap.ic_launcher) // 없으면 기본 이미지로 설정

        // 임시 사용자 정보 설정
        tvName.text = getString(R.string.default_name)
        tvUserID.text = getString(R.string.default_user_id)

        // 설정 화면으로 이동
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // 개인정보 수정 화면으로 이동
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        // 제보 내역 화면으로 이동
        btnReportHistory.setOnClickListener {
            val intent = Intent(this, ReportHistoryActivity::class.java)
            startActivity(intent)
        }

        // 메시지 목록 화면으로 이동
        btnMessageList.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
            startActivity(intent)
        }

        // DEBUG ONLY: FCM 토큰 디버깅용 표시
        val tvToken = findViewById<TextView>(R.id.tv_fcm_token)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    tvToken.text = "🔧 FCM Token (DEBUG):\n$token"
                } else {
                    tvToken.text = "FCM 토큰을 가져오지 못했습니다"
                }
            }

    }
}
