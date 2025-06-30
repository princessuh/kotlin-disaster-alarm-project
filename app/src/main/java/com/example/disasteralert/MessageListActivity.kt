package com.example.disasteralert

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

// 메시지 내역 화면

class MessageListActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var tvSelectedFilters: TextView

    // 해제 요청을 한 메시지를 추적하기 위한 Set
    private val deleteRequestSet = mutableSetOf<String>()  // key: userId:title

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        setupBottomNavigation(R.id.bottom_navigation, "MessageListActivity")

        btnFilter = findViewById(R.id.btn_filter)
        tvSelectedFilters = findViewById(R.id.tv_selected_disasters)
        val rvMessageList = findViewById<RecyclerView>(R.id.rv_message_list)
        rvMessageList.layoutManager = LinearLayoutManager(this)

        // 🔹 초기 더미 데이터
        val messageList = listOf(
            Message("기상청", "2025/06/25 14:30", "호우주의보 발효", "서울 지역에 강한 비 예보", "특보"),
            Message("재난안전본부", "2025/06/24 18:00", "지진 발생", "경북 지역에서 규모 3.5 지진 발생", "제보"),
            Message("기상청", "2025/06/23 10:00", "미세먼지 경보", "수도권 초미세먼지 농도 매우 나쁨", "기사")
        )

        // 🔹 메시지 클릭 시 상세 보기 띄우기 (MessageListActivity.kt)
        rvMessageList.adapter = MessageAdapter(messageList) { message ->

            // 1. 카테고리에 따라 BottomSheet 선택
            val bottomSheet = if (message.category.trim().equals("제보", ignoreCase = true)) {
                // ▶ 제보일 때
                MessageDetailBottomSheetReport(message) { handleDeleteRequest(it) }
            } else {
                // ▶ 제보 이외(기사·특보 등)일 때
                MessageDetailBottomSheet(message) { handleDeleteRequest(it) }
            }

            // 2. 보여주기
            bottomSheet.show(supportFragmentManager, "MessageDetail")
        }

        // 🔹 필터 클릭 시 동작
        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
                val infoText =
                    if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
                val disasterText =
                    if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${disasterTypes.joinToString(", ")}"
                tvSelectedFilters.text = "$infoText\n$disasterText"
            }
            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }
    }
    /** 삭제(해제) 요청 한 ID당 1회 제한 */
    private fun handleDeleteRequest(msg: Message) {
        val userId = "sample_user_123"           // 실제 로그인 사용자 ID
        val key = "$userId:${msg.title}"         // 동일 제목에 중복 요청 방지
        if (deleteRequestSet.add(key)) {
            Log.d("DeleteRequest", "해제 요청: $key")
            Toast.makeText(this, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "이미 해제 요청을 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
