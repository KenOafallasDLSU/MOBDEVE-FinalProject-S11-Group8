package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class ThreadActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var threadAdapter: ThreadAdapter
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var tvThreadAvatarLetter: TextView
    private lateinit var pBar : ProgressBar
    private lateinit var tvNoChats : TextView
    private lateinit var tvTitle : TextView
    private lateinit var tvTitleIcon : ImageView

    private lateinit var profileId: String
    private lateinit var etEmail: EditText
    private lateinit var letter : String
    private var color by Delegates.notNull<Int>()

    private lateinit var threadIds: ArrayList<String>

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var userId: String
    private val userCallRef = FirebaseDatabase.getInstance().reference.child("USERS").child(userId).child("callHandler")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        initRecyclerView()
        initFirebase()
        initData()
        listenToCalls()
    }

    private fun listenToCalls() {
        userCallRef.child("incoming").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val receivingIntent = Intent(this@ThreadActivity, ReceivingActivity::class.java)
                startActivity(receivingIntent)
            }
        })
    }

    private fun searchEmails(email : String){

        pBar.visibility = View.VISIBLE
        this.reference.orderByChild(Collections.email.name).equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        for (data in snapshot.children) profileId = data.key.toString()

                        if (userId != profileId) {
                            val thread = Thread(arrayOf(userId, profileId).toCollection(ArrayList<String>()))
                            val newThreadKey = database.getReference(Collections.threads.name).push().key

                            if (newThreadKey != null) {
                                database.getReference(Collections.threads.name).child(newThreadKey)
                                .setValue(thread).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        pBar.visibility = View.GONE
                                        val intent = Intent(this@ThreadActivity, ChatActivity::class.java)
                                        intent.putExtra(Keys.THREAD_ID_KEY.name, newThreadKey.toString())
                                        startActivity(intent)
                                    } else {
                                        pBar.visibility = View.GONE
                                        Toast.makeText(this@ThreadActivity,"Oh no! Something went wrong :(", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        } else {
                            pBar.visibility = View.GONE
                            Toast.makeText(this@ThreadActivity, "Hmm, you can't add yourself :/", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        pBar.visibility = View.GONE
                        Toast.makeText(this@ThreadActivity, "User does not exist :/", Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    pBar.visibility = View.GONE
                    Toast.makeText(this@ThreadActivity, "Oh no! Something went wrong :(", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child(Keys.USERS.name)
        user = FirebaseAuth.getInstance().currentUser!!
        userId = user.uid

        pBar.visibility = View.VISIBLE
        this.reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pBar.visibility = View.GONE
                letter = snapshot.child(Collections.name.name).value.toString().get(0).toString()
                color = resources.getIntArray(R.array.appcolors)[(snapshot.value.toString().length) % 5]
                tvThreadAvatarLetter.setText(letter)
                tvThreadAvatarLetter.background.setTint(color)
                tvTitle.setTextColor(color)
                tvTitleIcon.setColorFilter(color)
            }
            override fun onCancelled(error: DatabaseError) {
                pBar.visibility = View.GONE
                Toast.makeText(this@ThreadActivity, "Oh no! Something went wrong :(", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initData(){

        pBar.visibility = View.VISIBLE
        this.reference.child(userId).child(Collections.threads.name).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pBar.visibility = View.GONE
                var list : ArrayList<String> = ArrayList()
                for (data in snapshot.children) list.add(data.key.toString())
                threadIds = list.toCollection(ArrayList<String>())
                if (threadIds.size == 0) { tvNoChats.visibility = View.VISIBLE }
                else { tvNoChats.visibility = View.GONE }

                threadAdapter.submitList(threadIds)
                threadAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                pBar.visibility = View.GONE
                Toast.makeText(this@ThreadActivity, "Error loading data.", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initRecyclerView(){
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter(this)
        rvThreads.adapter = threadAdapter

        tvTitle = findViewById(R.id.tv_thread_app_name)
        tvTitleIcon = findViewById(R.id.iv_thread_app_icon)
        tvNoChats = findViewById(R.id.tv_threads_no_chats)
        pBar = findViewById(R.id.pb_thread)
        tvThreadAvatarLetter = findViewById(R.id.tv_thread_avatar_letter)
        tvThreadAvatarLetter.setOnClickListener { v ->
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        etEmail = findViewById(R.id.et_thread_email)
        btnAdd = findViewById(R.id.btn_thread_add)
        btnAdd.setOnClickListener { v ->
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty() && userId.isNotEmpty()){
                searchEmails(email)
            } else {
                Toast.makeText(this,"Email cannot be blank :P", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Keys.THREAD_ID_KEY.name, threadIds[position])
        startActivity(intent);
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
}