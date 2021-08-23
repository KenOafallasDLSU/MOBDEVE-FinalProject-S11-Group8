package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ThreadActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var threadAdapter: ThreadAdapter
    private lateinit var btnAdd : FloatingActionButton
    private var threads: List<Thread> = ArrayList()

    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    private var user: FirebaseUser? = null
    private var userId: String? = null

    // for other user
    private val email: String? = null
    private  var profileId:kotlin.String? = null
    private var etEmail : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        btnAdd = findViewById(R.id.btn_thread_add)
        btnAdd.setOnClickListener { v ->
            // TODO: look for user with email,
            // create new thread if exists
            // do not store thread in db yet

            var email = etEmail?.text.toString().trim()
            if (email != "" && userId != ""){
                reference?.orderByChild(Collections.email.name)?.equalTo(email)
                    ?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (data in snapshot.children) {
                                profileId = data.key
                                Toast.makeText(
                                    this@ThreadActivity,
                                    profileId,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (userId != profileId) {
                                val thread = Thread(
                                    arrayOf(userId.toString(), profileId.toString()).toCollection(ArrayList<String>()),
                                    arrayOf(
                                        Chat(
                                            "0",
                                            "2",
                                            "1",
                                            "Thanks for hiring me boss!!!",
                                            GregorianCalendar(2021,7,17,20,21,0)
                                        )
                                    ).toCollection(ArrayList<Chat>()),
                                )
                                database?.getReference(Collections.threads.name)
                                    ?.push()
                                    ?.setValue(thread)?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@ThreadActivity,"Added thread with " + profileId.toString(), Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@ThreadActivity,"Failed to add thread", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    this@ThreadActivity,
                                    "Hmm, you can't add yourself.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@ThreadActivity,
                                "Cannot find user.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(this,"Email cannot be blank.", Toast.LENGTH_SHORT).show()
            }
        }

        initRecyclerView()
        initFirebase()
        //initData()
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
        reference = database!!.getReference().child(Keys.USERS.name)
        user =  FirebaseAuth.getInstance().currentUser
        userId = user?.uid ?: ""
        // get threads from currently logged in user
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
    }

    /***
    private fun initData(){
        this.threads = ThreadDataHelper.initData()
        threadAdapter.submitList(threads)
    }
    ***/

    private fun initRecyclerView(){
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter(this)
        rvThreads.adapter = threadAdapter

        etEmail = findViewById(R.id.et_thread_email)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Keys.THREAD_ID_KEY.name, threads[position].toString())
        startActivity(intent);
    }

    override fun onResume() {
        super.onResume()
        // TODO: update view
    }
}