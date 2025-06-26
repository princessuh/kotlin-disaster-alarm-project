package com.example.disasteralert

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.DisasterItem
import kotlinx.coroutines.launch

// 메시지 내역 화면

class MessageListActivity : BaseActivity() {

    private lateinit var btnFilter: MaterialButton
    private lateinit var tvSelectedFilters: TextView
    private lateinit var rvMessageList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        // 네비게이션 바
        setupBottomNavigation(R.id.bottom_navigation, "MessageListActivity")

        btnFilter = findViewById(R.id.btn_filter)
        tvSelectedFilters = findViewById(R.id.tv_selected_disasters) // id 수정 반영
        rvMessageList = findViewById<RecyclerView>(R.id.rv_message_list)
        rvMessageList.layoutManager = LinearLayoutManager(this)

        // 필터 클릭 시 동작
        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
                val infoText =
                    if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
                val disasterText = if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${
                    disasterTypes.joinToString(", ")
                }"
                tvSelectedFilters.text = "$infoText\n$disasterText"
            }
            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }

        // API로 메시지 로딩
        fetchMessages()
    }

    private fun fetchMessages() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.disasterApi.searchDisaster(
                    rtdLoc = null,
                    regioncode = null,
                    rtdCode = null,
                    fromTime = null,
                    toTime = null,
                    days = 3,
                    sort = "desc"
                )

                if (response.isSuccessful) {
                    val result = response.body()
                    val items = result?.results ?: emptyList()

                    val messageList = items.map { item ->
                        val sender = if (item.type == "report") item.report_by ?: "익명 제보" else "재난알림"
                        val title = when (item.type) {
                            "report" -> "[제보] ${item.middle_type ?: "제보"}"
                            "rtd" -> "[재난] 코드 ${item.rtd_code}"
                            else -> "알림"
                        }
                        val content = item.rtd_details ?: item.content ?: "내용 없음"
                        val category = if (item.type == "report") "제보" else "특보"

                        Message(
                            sender = sender,
                            sentTime = item.time.replace('T', ' '),
                            title = title,
                            content = content,
                            category = category
                        )
                    }

                    rvMessageList.adapter = MessageAdapter(messageList)

                } else {
                    Toast.makeText(this@MessageListActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MessageListActivity, "연결 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}