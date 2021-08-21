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

    fun setItemChatBubbleFormat(senderIsUser: Boolean) {
        if (senderIsUser) {
//            val constraintSet = ConstraintSet()
//            constraintSet.clone(this.clItemChatBubble)
//            constraintSet.connect(R.id.cl_item_chat_bubble, ConstraintSet.START, R.id.gl_item_chat_right_limit, ConstraintSet.START, 0)
//            constraintSet.connect(R.id.cl_item_chat_bubble, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
//            constraintSet.setHorizontalBias(R.id.cl_item_chat_bubble,1.0f)
//            constraintSet.applyTo(this.clItemChatBubble)

            this.clItemChatBubble.background.setTint(itemView.getResources().getIntArray(R.array.appcolors)["Michaela".length%5])
        }
        else {
            this.clItemChatBubble.background.setTint(itemView.getResources().getIntArray(R.array.appcolors)["Thea".length%5])
        }
    }
}