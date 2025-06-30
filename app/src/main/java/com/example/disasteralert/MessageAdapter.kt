package com.example.disasteralert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    private val messageList: List<Message>,
    private val onMessageClick: (Message) -> Unit
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSenderTime: TextView = itemView.findViewById(R.id.tvSenderTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)

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
    }

    override fun getItemCount(): Int = messageList.size
}
