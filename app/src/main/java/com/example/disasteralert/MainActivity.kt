package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

// 네비게이션 바 - 수정 예정

class MainActivity : AppCompatActivity() {
    /** UI 요소 정의 */
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // 특정 화면에서는 하단 바 숨기기
        val currentActivity = intent.getStringExtra("CURRENT_ACTIVITY")
        if (currentActivity in listOf("LoginActivity", "RegisterActivity", "DisasterSelectionActivity")) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }

        // 하단 바 버튼 클릭 시 다른 액티비티로 이동
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home, R.id.navigation_news -> {
                    val intent = Intent(this, Login::class.java)
                    intent.putExtra("CURRENT_ACTIVITY", "LoginActivity")
                    startActivity(intent)
                    finish()
                }
                R.id.navigation_report -> {
                    val intent = Intent(this, PostActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }
}
