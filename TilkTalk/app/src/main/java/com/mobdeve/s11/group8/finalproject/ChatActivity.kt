package com.mobdeve.s11.group8.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {
    private lateinit var rvChat: RecyclerView
    private lateinit var chatManager: RecyclerView.LayoutManager
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<Chat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        this.initRecyclerView()
    }

    private fun initRecyclerView() {
        this.rvChat = findViewById(R.id.rv_chat)

        //init manager
        this.chatManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        this.rvChat.layoutManager = this.chatManager

        //init adapter
        this.initData()
        this.chatAdapter = ChatAdapter(this.chatList)
        this.rvChat.adapter = this.chatAdapter
    }

    private fun initData() {
    }
}