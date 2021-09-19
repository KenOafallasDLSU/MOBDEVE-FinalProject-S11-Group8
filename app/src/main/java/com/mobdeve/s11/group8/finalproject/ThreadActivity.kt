package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private lateinit var peerId: String
    private lateinit var etEmail: EditText
    private lateinit var letter : String
    private var color by Delegates.notNull<Int>()

    private lateinit var threadIds: ArrayList<String>

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var userId: String
    private lateinit var userCallRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        initRecyclerView()
        initFirebase()
        updateUserView()
        updateUserThreads()
        listenToCalls()
    }

    private fun listenToCalls() {
        userCallRef.child("incoming").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    Log.d("INCOMING", snapshot.value.toString())

                    val receivingIntent = Intent(this@ThreadActivity, ReceivingActivity::class.java)
                    startActivity(receivingIntent)
                }
            }
        })
    }

    private fun searchEmails(email : String){

        pBar.visibility = View.VISIBLE

        // search for emails matching the email parameter from the database
        this.reference.orderByChild(Collections.email.name).equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // if snapshot is not empty, there is a user with a matching email
                    if (snapshot.hasChildren()) {

                        // we don't have to worry about this, since it is ensured that all emails are unique
                        for (data in snapshot.children) peerId = data.key.toString()

                        // valid if the user did not enter their own email
                        if (userId != peerId) {

                            // create a new thread with peer, then get the key
                            val thread = Thread(arrayOf(userId, peerId).toCollection(ArrayList<String>()))
                            val newThreadKey = database.getReference(Collections.threads.name).push().key

                            if (newThreadKey != null) {

                                // get reference to new thread
                                database.getReference(Collections.threads.name).child(newThreadKey)
                                .setValue(thread).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        pBar.visibility = View.GONE

                                        // create intent going to chat activity
                                        val intent = Intent(this@ThreadActivity, ChatActivity::class.java)

                                        // pass key of newly created thread
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

        // initialize firebase references and values
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child(Keys.USERS.name)
        user = FirebaseAuth.getInstance().currentUser!!
        userId = user.uid
        userCallRef = FirebaseDatabase.getInstance().reference.child(Keys.USERS.name).child(userId).child("callHandler")
    }

    private fun updateUserView() {
        // update layout based on the logged-in user (name, avatar, colors)
        pBar.visibility = View.VISIBLE
        this.reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pBar.visibility = View.GONE
                letter = snapshot.child(Collections.dname.name).value.toString().get(0).toString()
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

    private fun updateUserThreads(){
        // get threads of logged in user
        pBar.visibility = View.VISIBLE
        this.reference.child(userId).child(Collections.threads.name).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                pBar.visibility = View.GONE

                // initialize arraylist and threadIDs from the database
                val list : ArrayList<String> = ArrayList()
                for (data in snapshot.children) list.add(data.key.toString())
                threadIds = list.toCollection(ArrayList<String>())

                // if there are no threads, show "no threads" message
                if (threadIds.size == 0) { tvNoChats.visibility = View.VISIBLE }
                else { tvNoChats.visibility = View.GONE }

                // pass list to threadAdaptor
                threadAdapter.submitList(threadIds)
                threadAdapter.notifyItemRangeChanged(0, threadAdapter.itemCount)
            }
            override fun onCancelled(error: DatabaseError) {
                pBar.visibility = View.GONE
                Toast.makeText(this@ThreadActivity, "Error loading data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initRecyclerView(){

        // initialize recycler view and adapter
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter(this)
        rvThreads.adapter = threadAdapter

        // initialize views
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

        // add email button listener
        btnAdd.setOnClickListener { v ->

            // get text from input field
            val email = etEmail.text.toString().trim()

            // check if not empty
            if (email.isNotEmpty() && userId.isNotEmpty()){
                searchEmails(email)
            } else {
                Toast.makeText(this,"Email cannot be blank :P", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // if item on recycler view is clicked, redirect to associated chat
    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)

        // pass id of chat of associated chat based on adapter position, then open
        intent.putExtra(Keys.THREAD_ID_KEY.name, threadIds[position])
        startActivity(intent);
    }

    override fun onResume() {
        super.onResume()
        updateUserView()
        updateUserThreads()
    }
}