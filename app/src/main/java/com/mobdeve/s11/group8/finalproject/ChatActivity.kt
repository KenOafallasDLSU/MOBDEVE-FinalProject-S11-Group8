package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var rvChat: RecyclerView
    private lateinit var chatManager: RecyclerView.LayoutManager
    private lateinit var chatAdapter: ChatAdapter
    private val chatList: ArrayList<Chat> = ArrayList()

    private lateinit var etChatInput: EditText
    private lateinit var clChatHead: ConstraintLayout
    private lateinit var ivChatAvatar: ImageView
    private lateinit var tvChatAvatarLetter: TextView
    private lateinit var tvChatName: TextView
    private lateinit var clChatFoot: ConstraintLayout

    private lateinit var ibChatBack: ImageButton
    private lateinit var ibChatCall: ImageButton
    private lateinit var ibChatSend: ImageButton

    // get current user from auth
    private lateinit var user1: String
    private lateinit var user2: String
    private lateinit var currentThread: String

    private val rootRef = FirebaseDatabase.getInstance().reference
    private lateinit var threadRef: DatabaseReference
    private lateinit var chatsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        this.etChatInput = findViewById(R.id.et_chat_input)
        this.clChatHead = findViewById(R.id.cl_chat_head)
        this.ivChatAvatar = findViewById(R.id.iv_chat_avatar)
        this.tvChatAvatarLetter = findViewById(R.id.tv_chat_avatar_letter)
        this.tvChatName = findViewById(R.id.tv_chat_name)
        this.clChatFoot = findViewById(R.id.cl_chat_foot)

        //get thread extra
        intent = getIntent()
        this.currentThread = intent.getStringExtra(Keys.THREAD_ID_KEY.name).toString()

        //get user 1
        this.user1 = "Ellen"
        //get user 2
        this.user2 = "Mica"

        this.threadRef = rootRef.child("threads").child(this.currentThread)
        this.chatsRef = this.threadRef.child("chats")

        this.initChatThreadDesign()
        this.initRecyclerView()
        this.initChatBack()
        this.initChatCall()
        this.initChatSend()
        this.initListener()
    }

    private fun initChatThreadDesign() {
        val color1: Int = resources.getIntArray(R.array.appcolors)[user1.length%5]
        val color2: Int = resources.getIntArray(R.array.appcolors)[user2.length%5]
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
        this.chatAdapter = ChatAdapter(this.chatList)
        this.rvChat.adapter = this.chatAdapter
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
            if(etChatInput.text.toString() != "") {
                val newChat = Chat(user1, user2, etChatInput.text.toString(), Calendar.getInstance())
                val newChatId = threadRef.push().key.toString()
                chatsRef.child(newChatId).child("senderId").setValue(newChat.senderId)
                chatsRef.child(newChatId).child("receiverId").setValue(newChat.receiverId)
                chatsRef.child(newChatId).child("body").setValue(newChat.body)
                chatsRef.child(newChatId).child("dateTimeSent").setValue(newChat.dateTimeSent.timeInMillis)
            } else {
                Toast.makeText(applicationContext,"Enter a message",Toast.LENGTH_SHORT).show()
            }

            etChatInput.setText("")
        }
    }

    private fun initListener() {
        val chatListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Get Post object and use the values to update the UI
                val chatKey = dataSnapshot.key.toString()
                Log.w("ChatActivityXXX", chatKey)
                val newChatRef = chatsRef.child(chatKey)

                newChatRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newCalendar = Calendar.getInstance()
                        newCalendar.timeInMillis = snapshot.child("dateTimeSent").getValue() as Long
                        chatList.add(
                            Chat(
                                snapshot.child("senderId").getValue().toString(),
                                snapshot.child("receiverId").getValue().toString(),
                                snapshot.child("body").getValue().toString(),
                                newCalendar
                            )
                        )
                        chatAdapter.notifyItemChanged(chatList.lastIndex)
                        chatAdapter.notifyItemRangeChanged(chatList.lastIndex, chatAdapter.getItemCount())

                        rvChat.smoothScrollToPosition(chatAdapter.getItemCount()- 1)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ChatActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }

        this.chatsRef.addChildEventListener(chatListener)
    }
}