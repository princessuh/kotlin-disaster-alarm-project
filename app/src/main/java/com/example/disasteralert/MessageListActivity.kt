package com.example.disasteralert

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        val rvMessageList = findViewById<RecyclerView>(R.id.rv_message_list)
        rvMessageList.layoutManager = LinearLayoutManager(this)

        // 초기 더미 데이터
        val messageList = listOf(
            Message("기상청", "2025/06/25 14:30", "호우주의보 발효", "서울 지역에 강한 비 예보", "특보"),
            Message("재난안전본부", "2025/06/24 18:00", "지진 발생", "경북 지역에서 규모 3.5 지진 발생", "제보"),
            Message("기상청", "2025/06/23 10:00", "미세먼지 경보", "수도권 초미세먼지 농도 매우 나쁨", "기사")
        )
        rvMessageList.adapter = MessageAdapter(messageList)

        // 필터 클릭 시 동작
        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
                val infoText =
                    if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
                val disasterText =
                    if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${
                        disasterTypes.joinToString(
                            ", "
                        )
                    }"
                tvSelectedFilters.text = "$infoText\n$disasterText"
            }
            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }
    }
}