package com.example.disasteralert

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton

// 메시지 내역 화면

class MessageListActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var tvSelectedFilters: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        // 네비게이션 바
        setupBottomNavigation(R.id.bottom_navigation, "MessageListActivity")

        btnFilter = findViewById(R.id.btn_filter)
        tvSelectedFilters = findViewById(R.id.tv_selected_disasters) // id 수정 반영

        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
                val infoText = if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
                val disasterText = if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${disasterTypes.joinToString(", ")}"
                tvSelectedFilters.text = "$infoText\n$disasterText"
            }
            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }
    }
}
