package com.example.disasteralert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Report 리스트를 RecyclerView 로 그려줄 어댑터
 */
class ReportAdapter(
    private val items: List<Report>
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocationTime : TextView = itemView.findViewById(R.id.tvLocationTime)
        val tvCustomTag    : TextView = itemView.findViewById(R.id.tvCustomTag)
        val tvContent      : TextView = itemView.findViewById(R.id.tvContent)
        val tvRecTags      : TextView = itemView.findViewById(R.id.tvRecommendedTags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = items[position]
        holder.tvLocationTime.text = report.locationTime
        holder.tvCustomTag.text    = report.customTag
        holder.tvContent.text      = report.content
        holder.tvRecTags.text      = report.recommendedTags.joinToString(" · ")
    }

    override fun getItemCount(): Int = items.size
}
