package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    private lateinit var ibChatBack: ImageButton
    private lateinit var ibChatCall: ImageButton
    private lateinit var ibChatSend: ImageButton

    // get current user from auth
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private lateinit var partnerId: String
    private lateinit var partnerName: String
    private lateinit var currentThread: String

    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child(Keys.USERS.name)
    private val userCallRef = usersRef.child(userId).child("callHandler")
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

        //get thread extra

        this.currentThread = intent.getStringExtra(Keys.THREAD_ID_KEY.name).toString()
        this.threadRef = rootRef.child(Collections.threads.name).child(this.currentThread)
        this.chatsRef = this.threadRef.child(Collections.chats.name)
        Log.w("ThisThread", this.currentThread)

        //get chat partner
        threadRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user1: String = snapshot.child(Collections.users.name).child("0").value.toString()
                val user2: String = snapshot.child(Collections.users.name).child("1").value.toString()
                if (user1 == userId)
                    partnerId = user2
                else
                    partnerId = user1
                Log.w("ThisPartner", partnerId)
                usersRef.child(partnerId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        partnerName = userSnapshot.child(Collections.dname.name).value.toString()
                        Log.w("ThisPartner", partnerName)

                        initChatThreadDesign()
                        initRecyclerView()
                        initChatBack()
                        initChatCall()
                        initChatSend()
                        initListener()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        listenToCalls()
    }

    private fun listenToCalls() {
        userCallRef.child("incoming").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    Log.d("INCOMING", snapshot.value.toString())

                    val receivingIntent = Intent(this@ChatActivity, ReceivingActivity::class.java)
                    startActivity(receivingIntent)
                }
            }
        })
    }

    private fun initChatThreadDesign() {
        val color: Int = resources.getIntArray(R.array.appcolors)[(partnerName?.length ?: 0) % 5]

        this.tvChatName.setTextColor(color)
        this.ivChatAvatar.background.setTint(color)
        this.etChatInput.background.setTint(color)
        this.tvChatAvatarLetter.text = partnerName[0].toString()
        this.tvChatName.text = partnerName
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
            if(chatAdapter.itemCount <= 0) {
                threadRef.removeValue()
            }
            finish()
        }
    }

    private fun initChatCall() {
        this.ibChatCall = findViewById(R.id.ib_chat_call)
        this.ibChatCall.setOnClickListener {
            usersRef.child(partnerId).child("callHandler").child("incoming").setValue(userId)

            val callIntent = Intent(this, CallingActivity::class.java)
            callIntent.putExtra(Keys.CALL_PARTNER.name, partnerId)
            startActivity(callIntent)
        }
    }

    private fun initChatSend() {
        this.ibChatSend = findViewById(R.id.ib_chat_send)
        this.ibChatSend.setOnClickListener {
            if(etChatInput.text.toString() != "") {
                if(chatAdapter.itemCount <= 0) {
                    usersRef.child(userId).child(Collections.threads.name).child(currentThread).setValue(currentThread)
                    usersRef.child(partnerId).child(Collections.threads.name).child(currentThread).setValue(currentThread)
                }
                val newChat = Chat(userId, partnerId, etChatInput.text.toString(), Calendar.getInstance())
                val newChatId = threadRef.push().key.toString()
                val chatHashMap: HashMap<String, String> = HashMap()
                chatHashMap.put("senderId", newChat.senderId)
                chatHashMap.put("receiverId", newChat.receiverId)
                chatHashMap.put("body", newChat.body)
                chatHashMap.put("dateTimeSent", newChat.dateTimeSent.timeInMillis.toString())
                chatsRef.child(newChatId).setValue(chatHashMap)

                val threadHashMap: HashMap<String, String> = HashMap()
                threadHashMap.put(Collections.lastChat.name, newChat.body)
                threadHashMap.put(Collections.lastUpdated.name, newChat.getDateTimeString())
                threadRef.updateChildren(threadHashMap as Map<String, Any>)
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
                        Log.w("Snap", snapshot.toString())
                        newCalendar.timeInMillis = snapshot.child("dateTimeSent").value.toString().toLong()
                        chatList.add(
                            Chat(
                                snapshot.child("senderId").value.toString(),
                                snapshot.child("receiverId").value.toString(),
                                snapshot.child("body").value.toString(),
                                newCalendar
                            )
                        )
                        chatAdapter.notifyItemChanged(chatList.lastIndex)
                        chatAdapter.notifyItemRangeChanged(chatList.lastIndex, chatAdapter.itemCount)

                        rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        this.chatsRef.addChildEventListener(chatListener)
    }
}