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
import java.time.format.DateTimeParseException

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

        // --- 🔽 기존 코드: 서버 API 호출 (주석 처리) ---
        /*
        RetrofitClient.rtdService.getRtdEvents().enqueue(object : Callback<RtdResponse> {
            override fun onResponse(call: Call<RtdResponse>, response: Response<RtdResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val rtdEvents = response.body()!!.results
                    // ... (이하 생략)
                }
            }
            override fun onFailure(call: Call<RtdResponse>, t: Throwable) {
                // ...
            }
        })
        */

        // --- 🔽 새로운 코드: 더미 데이터 생성 및 사용 ---
        val rtdEvents = getDummyRtdEvents() // 더미 데이터 가져오기
        val messageList = mutableListOf<Message>()

        for (event in rtdEvents) {
            // --- 💡 수정된 부분 ---
            val formattedTime = try {
                // 시간 문자열에 시간대 정보(+09:00, KST)를 추가하여 파싱
                val parsedTime = OffsetDateTime.parse(event.time + "+09:00")
                parsedTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
            } catch (e: DateTimeParseException) {
                Log.e("TimeParsing", "시간 파싱 실패: ${event.time}", e)
                "시간 알 수 없음"
            }
            // --- 수정 끝 ---

            when (event.type) {
                "rtd" -> {
                    val fullContent = event.rtd_details?.joinToString("\n") ?: "내용 없음"
                    val title = event.rtd_details?.find { it.startsWith("type:") }?.substringAfter(":")?.trim()?.let { type ->
                        "[${type}] ${event.rtd_loc}"
                    } ?: event.rtd_loc ?: "RTD 알림"

                    messageList.add(
                        Message(
                            sender = event.rtd_loc ?: "발신자 없음",
                            sentTime = formattedTime,
                            title = title,
                            content = fullContent,
                            category = "재난 안전 문자",
                            id = event.id,
                            visible = event.visible ?: true
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
        // --- 새로운 코드 끝 ---


        btnFilter.setOnClickListener {
            val dialog = MessageFilterBottomSheetDialog { infoTypes, disasterTypes ->
                val infoText = if (infoTypes.isEmpty()) "정보유형: 없음" else "정보유형: ${infoTypes.joinToString(", ")}"
                val disasterText = if (disasterTypes.isEmpty()) "재난유형: 없음" else "재난유형: ${disasterTypes.joinToString(", ")}"
                tvSelectedFilters.text = "$infoText\n$disasterText"
            }
            dialog.show(supportFragmentManager, "MessageFilterBottomSheet")
        }
    }

    /**
     * 테스트용 더미 RtdEvent 리스트를 생성하는 함수
     */
    private fun getDummyRtdEvents(): List<RtdEvent> {
        return listOf(
            RtdEvent(
                type = "rtd",
                id = "41835eca-be34-5886-a66e-cda5a6745cdb",
                time = "2025-09-26T14:27:44",
                rtd_loc = "광양시 태인동",
                rtd_details = listOf(
                    "level: 안전안내",
                    "type: 화재",
                    "content: 광양시 태인동 태인2길 146, 타이어 창고 화재에 따라 인근지역 연기 발생으로 태인, 광영, 옥곡, 진월 주민 ▲창문닫기▲마스크 착용 권고▲외출 자제"
                ),
                visible = null,
                report_location = null, middle_type = null, small_type = null, content = null
            ),
            RtdEvent(
                type = "rtd",
                id = "ce0c7a3f-2446-5246-a9ab-774e91865d5c",
                time = "2025-09-25T18:37:42",
                rtd_loc = "북면",
                rtd_details = listOf(
                    "level: 안전안내",
                    "type: 화재",
                    "content: 오늘 18:20분경 북면 한교리60-15 인근 화재발생. 인근주민은 창문을 닫고 연기흡입에 주의하여 주시기 바랍니다."
                ),
                visible = null,
                report_location = null, middle_type = null, small_type = null, content = null
            ),
            RtdEvent(
                type = "rtd",
                id = "09681240-fd28-557a-b49e-a3f249ea9b51",
                time = "2025-09-20T07:10:55",
                rtd_loc = "울릉군",
                rtd_details = listOf(
                    "level: 안전안내",
                    "type: 산사태",
                    "content: 현재 울릉군 관내 산사태 경보 발령. 산림 주변 위험 지역 접근 금지. 대피 명령 시 대피소나 안전지대로 반드시 이동하세요."
                ),
                visible = null,
                report_location = null, middle_type = null, small_type = null, content = null
            ),
            RtdEvent(
                type = "rtd",
                id = "9f7a8863-e569-5dd0-b882-938e8600bf49",
                time = "2025-10-05T03:49:56",
                rtd_loc = "전남 여수시 거문도 남남동쪽 87km 해역",
                rtd_details = listOf(
                    "magnitude: 2.8",
                    "location: 전남 여수시 거문도 남남동쪽 87km 해역 최대진도 Ⅰ 지진피해 없을 것으로 예상됨 =",
                    "latitude: 33.27",
                    "longitude: 127.54"
                ),
                visible = null,
                report_location = null, middle_type = null, small_type = null, content = null
            )
        )
    }

    private fun handleDeleteRequest(msg: Message) {
        val userId = "sample_user_123"
        val key = "$userId:${msg.title}"
        if (deleteRequestSet.add(key)) {
            Log.d("DeleteRequest", "해제 요청: $key")
            Toast.makeText(this, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "이미 해제 요청을 하셨습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}