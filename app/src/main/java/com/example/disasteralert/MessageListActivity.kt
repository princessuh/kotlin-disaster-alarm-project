package com.example.disasteralert

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.disasteralert.api.RtdEvent
import com.example.disasteralert.api.RtdResponse
import com.example.disasteralert.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class MessageListActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnFilter: MaterialButton
    private lateinit var tvSelectedFilters: TextView
    private val deleteRequestSet = mutableSetOf<String>()
    private val messageList = mutableListOf<Message>()

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        setupBottomNavigation(R.id.bottom_navigation, "MessageListActivity")

        btnFilter = findViewById(R.id.btn_filter)
        tvSelectedFilters = findViewById(R.id.tv_selected_disasters)
        recyclerView = findViewById(R.id.rv_message_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MessageAdapter(messageList) { message ->
            val bottomSheet = if (message.category == "제보") {
                MessageDetailBottomSheetReport(message) { handleDeleteRequest(it) }
            } else {
                MessageDetailBottomSheet(message) { handleDeleteRequest(it) }
            }
            bottomSheet.show(supportFragmentManager, "MessageDetail")
        }
        recyclerView.adapter = adapter

        loadRtdMessages()
        setupFilterButton()
    }

    /** ✅ RTD 메시지 불러오기 */
    private fun loadRtdMessages() {
        RetrofitClient.rtdService.getRtdEvents().enqueue(object : Callback<RtdResponse> {
            override fun onResponse(call: Call<RtdResponse>, response: Response<RtdResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    messageList.clear()
                    val rtdEvents = response.body()!!.results

                    for (event in rtdEvents) {
                        val formattedTime = try {
                            OffsetDateTime.parse(event.time)
                                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                        } catch (e: Exception) {
                            "시간 알 수 없음"
                        }

                        when (event.type) {
                            "rtd" -> {
                                val fullContent = event.rtd_details?.joinToString("\n") ?: "내용 없음"
                                val title = event.rtd_details?.firstOrNull()?.substringAfter(": ")
                                    ?.plus("") ?: "RTD 알림"

                                messageList.add(
                                    Message(
                                        sender = event.rtd_loc ?: "발신자 없음",
                                        sentTime = formattedTime,
                                        title = title,
                                        content = fullContent,
                                        category = "서버 발송",
                                        id = event.id,
                                        visible = event.visible ?: true
                                    )
                                )
                            }

                            "report" -> {
                                messageList.add(
                                    Message(
                                        sender = event.report_location ?: "제보 위치 없음",
                                        sentTime = formattedTime,
                                        title = "제보: ${event.middle_type ?: "?"}-${event.small_type ?: "?"}",
                                        content = event.content ?: "내용 없음",
                                        category = "제보",
                                        id = event.id,
                                        visible = event.visible ?: true
                                    )
                                )
                            }
                        }
                    }

                    adapter.notifyDataSetChanged()
                    Log.d("MessageList", "✅ 메시지 ${messageList.size}개 로드 완료")
                } else {
                    Toast.makeText(this@MessageListActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RtdResponse>, t: Throwable) {
                Log.e("RTD_FETCH", "요청 실패: ${t.localizedMessage}")
                Toast.makeText(this@MessageListActivity, "데이터 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** ✅ 필터 버튼 */
    private fun setupFilterButton() {
        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { disasters, province, city, district, period ->

                // 필터 텍스트 요약
                val summary = buildString {
                    if (disasters.isNotEmpty()) append("재난: ${disasters.joinToString(", ")}  ")
                    if (!province.isNullOrBlank()) append("지역: $province $city $district  ")
                    if (!period.isNullOrBlank()) append("기간: $period")
                    if (isEmpty()) append("전체 보기")
                }
                tvSelectedFilters.text = summary

                // 기간 계산
                val now = OffsetDateTime.now()
                val filterTime = when (period) {
                    "1개월" -> now.minusMonths(1)
                    "1주일" -> now.minusWeeks(1)
                    "1일" -> now.minusDays(1)
                    else -> null
                }

                // 필터 적용
                val filteredList = messageList.filter { msg ->
                    var match = true

                    if (disasters.isNotEmpty()) {
                        match = match && disasters.any { d -> msg.title.contains(d) }
                    }
                    if (!province.isNullOrBlank()) {
                        match = match && msg.sender.contains(province)
                    }
                    if (!city.isNullOrBlank()) {
                        match = match && msg.sender.contains(city)
                    }
                    if (!district.isNullOrBlank()) {
                        match = match && msg.sender.contains(district)
                    }

                    if (filterTime != null) {
                        try {
                            val msgTime = OffsetDateTime.parse(
                                msg.sentTime.replace("/", "-").replace(" ", "T") + "+09:00"
                            )
                            match = match && msgTime.isAfter(filterTime)
                        } catch (_: Exception) {}
                    }

                    match
                }

                adapter.updateData(filteredList)
            }

            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }
    }

    /** ✅ 해제 요청 처리 */
    private fun handleDeleteRequest(msg: Message) {
        val userId = "sample_user_123"
        val key = "$userId:${msg.id}"
        if (deleteRequestSet.add(key)) {
            Toast.makeText(this, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "이미 해제 요청을 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
