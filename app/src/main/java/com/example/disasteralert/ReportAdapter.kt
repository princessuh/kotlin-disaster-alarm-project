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
import java.time.OffsetDateTime

class ReportAdapter(
    private val items: MutableList<ReportDetail> = mutableListOf(),
    private val onItemClick: (ReportDetail) -> Unit
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
        val tvStatusChip: TextView = itemView.findViewById(R.id.tvStatusChip)

        fun bind(report: ReportDetail) {
            Log.d("ReportAdapter", "📌 바인딩 데이터: time=${report.report_time}, location=${report.report_location}")

            tvLocationTime.text = "${report.report_location} • ${report.report_time}"
            tvCustomTag.text = "#${smallTypeMap[report.small_type] ?: "기타"}"
            tvContent.text = report.report_content
            tvRecTags.text = ""

            if (report.visible) {
                tvStatusChip.text = "진행 중"
            } else {
                tvStatusChip.text = "종결"
            }

            tvStatusChip.setBackgroundResource(R.drawable.chip_border)
            tvStatusChip.visibility = View.VISIBLE

            itemView.setOnClickListener {
                onItemClick(report)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = items[position]
        Log.d("어댑터", "📌 바인딩 위치: $position / ${report.report_location} / ${report.report_content}")
        holder.bind(report)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ReportDetail>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
