package com.example.disasteralert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.disasteralert.api.ReportDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

class ReportAdapter(
    private val items: MutableList<ReportDetail> = mutableListOf()
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    private val smallTypeMap = mapOf(
        "31" to "태풍", "32" to "호우", "33" to "홍수", "34" to "강풍", "35" to "대설",
        "41" to "폭염", "42" to "한파", "51" to "지진", "61" to "화재", "71" to "미세먼지",
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = items[position]
        Log.d("어댑터", "📌 바인딩 위치: $position / ${report.report_location} / ${report.report_content}")

        holder.tvLocationTime.text = "${report.report_location} • ${formatDateTime(report.report_time)}"
        holder.tvCustomTag.text = "#${smallTypeMap[report.small_type] ?: "기타"}"
        holder.tvContent.text = report.report_content
        holder.tvRecTags.text = ""
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ReportDetail>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun formatDateTime(iso: String): String {
        return try {
            val parsed = LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME)
            parsed.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        } catch (e: Exception) {
            iso
        }
    }
}
