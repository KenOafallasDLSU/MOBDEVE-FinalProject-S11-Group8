package com.mobdeve.s11.group8.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {
    private lateinit var rvChat: RecyclerView
    private lateinit var chatManager: RecyclerView.LayoutManager
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<Chat>

    private lateinit var etChatInput: EditText
    private lateinit var clChatHead: ConstraintLayout
    private lateinit var ivChatAvatar: ImageView
    private lateinit var tvChatAvatarLetter: TextView
    private lateinit var tvChatName: TextView
    private lateinit var clChatFoot: ConstraintLayout

    private lateinit var ibChatBack: ImageButton
    private lateinit var ibChatCall: ImageButton
    private lateinit var ibChatSend: ImageButton

    private var user1 = "Thea"
    private var user2 = "Michaela"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        this.etChatInput = findViewById(R.id.et_chat_input)
        this.clChatHead = findViewById(R.id.cl_chat_head)
        this.ivChatAvatar = findViewById(R.id.iv_chat_avatar)
        this.tvChatAvatarLetter = findViewById(R.id.tv_chat_avatar_letter)
        this.tvChatName = findViewById(R.id.tv_chat_name)
        this.clChatFoot = findViewById(R.id.cl_chat_foot)

        this.initChatThreadDesign()
        this.initRecyclerView()
        this.initChatBack()
        this.initChatCall()
        this.initChatSend()
    }

    private fun initChatThreadDesign() {
        val color1: Int = getResources().getIntArray(R.array.appcolors)[user1.length%5]
        val color2: Int = getResources().getIntArray(R.array.appcolors)[user2.length%5]
        this.clChatHead.background.setTint(color1)
        this.ivChatAvatar.background.setTint(color2)
        this.tvChatAvatarLetter.text = user2[0].toString()
        this.tvChatName.text = user2
        this.clChatFoot.background.setTint(color1)
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
        this.chatList = ChatDataHelper.initData()
    }

    private fun initChatBack() {
        this.ibChatBack = findViewById(R.id.ib_chat_back)
        this.ibChatBack.setOnClickListener{
            finish()
        }
    }

    private fun initChatCall() {
        this.ibChatCall = findViewById(R.id.ib_chat_call)
        this.ibChatCall.setOnClickListener {

        }
    }

    private fun initChatSend() {
        this.ibChatSend = findViewById(R.id.ib_chat_send)
        this.ibChatSend.setOnClickListener {

        }
    }
}