package com.mobdeve.s11.group8.finalproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private var chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid

    companion object {
        private const val LEFT_LAYOUT: Int = 0
        private const val RIGHT_LAYOUT: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        //inflate template
        val inflater = LayoutInflater.from (parent.context)
        val layout = if (viewType == 0) R.layout.item_chat_left else R.layout.item_chat_right
        val view = inflater.inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatHolder: ChatViewHolder = holder as ChatViewHolder
        chatHolder.setItemChatBody(this.chatList.get(position).body)

        //Time formatting
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_YEAR)
        val currentWeek = now.get(Calendar.WEEK_OF_YEAR)
        val currentYear = now.get(Calendar.YEAR)
        if (currentYear != this.chatList.get(position).getYear())
            chatHolder.setItemChatDate(this.chatList.get(position).getDateTimeString())
        else if (currentWeek != this.chatList.get(position).getWeekOfYear())
            chatHolder.setItemChatDate(this.chatList.get(position).getDayMonthTimeString())
        else if (currentDay != this.chatList.get(position).getDayOfYear())
            chatHolder.setItemChatDate(this.chatList.get(position).getDayOfWeekTimeString())
        else
            chatHolder.setItemChatDate(this.chatList.get(position).getTimeString())
    }

    override fun getItemCount(): Int {
        return this.chatList.size
    }

    // gets layout type, left when sender is partner, right when sender is user
    override fun getItemViewType(position: Int): Int {
        if (this.userId == this.chatList.get(position).senderId)
            return RIGHT_LAYOUT
        else
            return LEFT_LAYOUT
    }
}