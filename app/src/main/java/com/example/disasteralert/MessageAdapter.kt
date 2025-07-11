package com.example.disasteralert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat


class MessageAdapter(
    private val messageList: List<Message>,
    private val onMessageClick: (Message) -> Unit
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSenderTime: TextView = itemView.findViewById(R.id.tvSenderTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvStatusChip: TextView = itemView.findViewById(R.id.tvStatusChip)

        init {
            itemView.setOnClickListener {
                val message = messageList[adapterPosition]
                onMessageClick(message)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.tvSenderTime.text = "${message.sender} • ${message.sentTime}"
        holder.tvTitle.text = message.title
        holder.tvContent.text = message.content
        holder.tvCategory.text = message.category

        updateStatusChip(holder.tvStatusChip, message.visible)

        if (message.visible) {
            holder.tvStatusChip.text = "진행 중"
        } else {
            holder.tvStatusChip.text = "해제"
        }
        holder.tvStatusChip.setBackgroundResource(R.drawable.chip_border)
        holder.tvStatusChip.visibility = View.VISIBLE
    }

    private fun updateStatusChip(chip: TextView, isActive: Boolean) {
        val bg = chip.background?.mutate() as? android.graphics.drawable.GradientDrawable ?: return
        val context = chip.context

        if (isActive) {
            chip.text = "진행 중"
            chip.setTextColor(ContextCompat.getColor(context, R.color.red_60))
            bg.setStroke(2, ContextCompat.getColor(context, R.color.red_60))
        } else {
            chip.text = "해제"
            chip.setTextColor(ContextCompat.getColor(context, R.color.grey_60))
            bg.setStroke(2, ContextCompat.getColor(context, R.color.grey_60))
        }

        chip.background = bg
        chip.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int = messageList.size
}
