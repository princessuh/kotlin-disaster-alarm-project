package com.example.disasteralert

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.disasteralert.R
import com.example.disasteralert.api.ReportDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReportAdapter(
    private val items: List<ReportDetail>
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    // 소분류 코드 → 문자열 매핑
    private val smallTypeMap = mapOf(
        "31" to "태풍",
        "32" to "호우",
        "33" to "홍수",
        "34" to "강풍",
        "35" to "대설",
        "41" to "폭염",
        "42" to "한파",
        "51" to "지진",
        "61" to "화재",
        "71" to "미세먼지",
        "11" to "사용자제보"
    )

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocationTime: TextView = itemView.findViewById(R.id.tvLocationTime)
        val tvCustomTag: TextView = itemView.findViewById(R.id.tvCustomTag)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvRecTags: TextView = itemView.findViewById(R.id.tvRecommendedTags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_post, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = items[position]

        // 위치 + 시간
        holder.tvLocationTime.text = "${report.report_location} • ${formatDateTime(report.report_time)}"

        // #소분류 태그
        val tagText = smallTypeMap[report.small_type] ?: "기타"
        holder.tvCustomTag.text = "#$tagText"

        // 본문
        holder.tvContent.text = report.report_content

        // 추천 태그 사용 안함
        holder.tvRecTags.text = ""
    }

    override fun getItemCount(): Int = items.size

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateTime(iso: String): String {
        return try {
            val parsed = LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME)
            parsed.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        } catch (e: Exception) {
            iso  // 실패 시 원본 그대로 출력
        }
    }
}
