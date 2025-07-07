package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView



open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun setupBottomNavigation(navViewId: Int, currentActivity: String) {
        val bottomNavigationView = findViewById<BottomNavigationView>(navViewId) ?: run {
            Log.e(TAG, "BottomNavigationView not found for ID: $navViewId")
            return
        }

        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        // 특정 액티비티에서 네비게이션 바 숨기기
        if (currentActivity in listOf("LoginActivity", "RegisterActivity", "DisasterSelectionActivity")) {
            bottomNavigationView.visibility = View.GONE
            Log.d(TAG, "Hiding BottomNavigationView for $currentActivity")
            return
        } else {
            bottomNavigationView.visibility = View.VISIBLE
            Log.d(TAG, "Showing BottomNavigationView for $currentActivity")
        }

        // 메뉴 항목 상태 디버깅
        bottomNavigationView.menu.let { menu ->
            Log.d(TAG, "Menu items count: ${menu.size()}")
            for (i in 0 until menu.size()) {
                Log.d(TAG, "Menu item $i: ${menu.getItem(i).itemId}, visible: ${menu.getItem(i).isVisible}")
            }
        }

        // 현재 액티비티에 따라 선택된 항목 설정
        when (currentActivity) {
            "MainMapActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_home
            "MessageListActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_news
            "ReportHistoryActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_report
            "ProfileActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_profile
            else -> Log.w(TAG, "Unknown activity: $currentActivity")
        }

        // 네비게이션 항목 클릭 처리
        bottomNavigationView.setOnItemSelectedListener { item ->
            Log.d(TAG, "Selected navigation item: ${item.itemId}")
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (currentActivity != "MainMapActivity") {
                        startActivity(Intent(this, MainMapActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                        finish()
                    }
                    true
                }
                R.id.navigation_news -> {
                    if (currentActivity != "MessageListActivity") {
                        startActivity(Intent(this, MessageListActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                        finish()
                    }
                    true
                }
                R.id.navigation_report -> {
                    if (currentActivity != "ReportHistoryActivity") {
                        startActivity(Intent(this, ReportHistoryActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                        finish()
                    }
                    true
                }
                R.id.navigation_profile -> {
                    if (currentActivity != "ProfileActivity") {
                        startActivity(Intent(this, ProfileActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation) ?: return
        val currentActivity = this::class.java.simpleName
        Log.d(TAG, "onNewIntent: $currentActivity")
        if (currentActivity in listOf("LoginActivity", "RegisterActivity", "DisasterSelectionActivity")) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
            when (currentActivity) {
                "MainMapActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_home
                "MessageListActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_news
                "ReportHistoryActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_report
                "ProfileActivity" -> bottomNavigationView.selectedItemId = R.id.navigation_profile
            }
        }
    }
}