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

    private lateinit var btnFilter: MaterialButton
    private lateinit var tvSelectedFilters: TextView
    private val deleteRequestSet = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        setupBottomNavigation(R.id.bottom_navigation, "MessageListActivity")

        btnFilter = findViewById(R.id.btn_filter)
        tvSelectedFilters = findViewById(R.id.tv_selected_disasters)
        val rvMessageList = findViewById<RecyclerView>(R.id.rv_message_list)
        rvMessageList.layoutManager = LinearLayoutManager(this)

        // 🔽 RTD 이벤트 호출
        RetrofitClient.rtdService.getRtdEvents().enqueue(object : Callback<RtdResponse> {
            override fun onResponse(call: Call<RtdResponse>, response: Response<RtdResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val rtdEvents = response.body()!!.results
                    val messageList = mutableListOf<Message>()

                    for (event in rtdEvents) {
                        val formattedTime = try {
                            val parsedTime = OffsetDateTime.parse(event.time)
                            parsedTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                        } catch (e: Exception) {
                            try {
                                val fallback = event.time.replace(" ", "T") + "+09:00"
                                OffsetDateTime.parse(fallback)
                                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
                            } catch (e2: Exception) {
                                Log.e("TimeParsing", "시간 파싱 실패: ${event.time}")
                                "시간 알 수 없음"
                            }
                        }

                        when (event.type) {
                            "rtd" -> {
                                val fullContent = event.rtd_details?.joinToString("\n") ?: "내용 없음"
                                val title = event.rtd_details?.firstOrNull()?.substringBefore(":")
                                    ?.plus(": ...") ?: "RTD 알림"

                                messageList.add(
                                    Message(
                                        sender = event.rtd_loc ?: "발신자 없음",
                                        sentTime = formattedTime,
                                        title = title,
                                        content = fullContent,
                                        category = "서버 발송",
                                        id = event.id,  // ✅ ID 추가
                                        visible = event.visible ?: true // ✅ visible 필드 추가
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
                                        id = event.id,  // ✅ ID 추가
                                        visible = event.visible ?: true // ✅ visible 필드 추가
                                    )
                                )
                            }

                            else -> {
                                Log.w("UnknownType", "알 수 없는 type: ${event.type}")
                            }
                        }
                    }

                    Log.d("MESSAGE_COUNT", "🟢 메시지 수: ${messageList.size}")
                    rvMessageList.adapter = MessageAdapter(messageList) { message ->
                        val bottomSheet = if (message.category == "제보") {
                            MessageDetailBottomSheetReport(message) { handleDeleteRequest(it) }
                        } else {
                            MessageDetailBottomSheet(message) { handleDeleteRequest(it) }
                        }
                        bottomSheet.show(supportFragmentManager, "MessageDetail")
                    }

                } else {
                    Toast.makeText(this@MessageListActivity, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RtdResponse>, t: Throwable) {
                Log.e("RTD_FETCH", "요청 실패: ${t.localizedMessage}")
                Toast.makeText(this@MessageListActivity, "데이터 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }  //todo 살릴 때 이 부분 삭제
        //TODO 정보 유형 선택 부분 메시지 필터 바텀 시트 다이얼로그 안에 추가 필요)
//        btnFilter.setOnClickListener {
//            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
//                val infoText = if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
//                val disasterText = if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${disasterTypes.joinToString(", ")}"
//                tvSelectedFilters.text = "$infoText\n$disasterText"
//            }
//            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
//        }
//    }

    private fun handleDeleteRequest(msg: Message) {
        val userId = "sample_user_123"  // ✅ 실제 앱에서는 SharedPreferences에서 불러오는 걸 권장
        val key = "$userId:${msg.title}"
        if (deleteRequestSet.add(key)) {
            Log.d("DeleteRequest", "해제 요청: $key")
            Toast.makeText(this, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "이미 해제 요청을 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
