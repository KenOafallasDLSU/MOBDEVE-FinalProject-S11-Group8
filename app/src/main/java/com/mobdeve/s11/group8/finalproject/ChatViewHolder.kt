package com.mobdeve.s11.group8.finalproject

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val clItemChatBubble: ConstraintLayout = itemView.findViewById(R.id.cl_item_chat_bubble)
    private val tvItemChatBody: TextView = itemView.findViewById(R.id.tv_item_chat_body)
    private val tvItemChatDate: TextView = itemView.findViewById(R.id.tv_item_chat_date)

    fun setItemChatBody(body: String) {
        this.tvItemChatBody.text = body
    }

    fun setItemChatDate(date: String) {
        this.tvItemChatDate.text = date
    }

    fun setItemChatBubbleColor(sender: String) {
        this.clItemChatBubble.background.setTint(itemView.getResources().getIntArray(R.array.appcolors)[sender.length%5])
    }
}