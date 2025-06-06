package com.example.disasteralert

import android.os.Bundle

// 네비게이션 화면 설정을 위한 메인
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // BaseActivity가 제공하는 공통 하단바 세팅
        setupBottomNavigation(
            navViewId = R.id.bottom_navigation,
            currentActivity = this::class.java.simpleName   // "MainActivity"
        )
    }
}
