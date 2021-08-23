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
    private lateinit var threadIds: List<String>

    private  var profileId:String? = null
    private var etEmail : EditText? = null

    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null
    private var user: FirebaseUser? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        initRecyclerView()
        initFirebase()
        initData()
    }

    private fun searchEmails(email : String){
        reference?.orderByChild(Collections.email.name)?.equalTo(email)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // look for data in the database
                    for (data in snapshot.children) {
                        profileId = data.key
                    }

                    if (userId != profileId) {
                        val thread = Thread(
                            arrayOf(userId.toString(), profileId.toString()).toCollection(ArrayList<String>()),
                            arrayOf(
                                Chat(
                                    userId.toString(),
                                    profileId.toString(),
                                    "",
                                    Calendar.getInstance()
                                )
                            ).toCollection(ArrayList<Chat>()),
                        )

                        var newThreadKey = database?.getReference(Collections.threads.name)?.push()?.key
                        if (newThreadKey != null) {
                            database?.getReference(Collections.threads.name)?.child(newThreadKey)
                                ?.setValue(thread)?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@ThreadActivity,"Added thread with " + profileId.toString(), Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@ThreadActivity, ChatActivity::class.java)
                                        intent.putExtra(Keys.THREAD_ADD_NEW_KEY.name, newThreadKey.toString())
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this@ThreadActivity,"Failed to add thread", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }

                    } else {
                        Toast.makeText(this@ThreadActivity, "You can't add yourself.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ThreadActivity, "Cannot find user.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
        reference = database!!.getReference().child(Keys.USERS.name)
        user =  FirebaseAuth.getInstance().currentUser
        userId = user?.uid
    }

    private fun initData(){
        // get threads from currently logged in user
        this.threadIds = arrayOf("-Mhln9-lImhb5B0SiAI-", "-MhluSWS_6b8wKOGCsta", "-MhnGb6a7SP7en7oCjV2").toCollection(ArrayList<String>());
        threadAdapter.submitList(threadIds)
    }

    private fun initRecyclerView(){
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter(this)
        rvThreads.adapter = threadAdapter

        etEmail = findViewById(R.id.et_thread_email)
        btnAdd = findViewById(R.id.btn_thread_add)
        btnAdd.setOnClickListener { v ->
            var email = etEmail?.text.toString().trim()
            if (email != "" && userId != ""){
                searchEmails(email)
            } else {
                Toast.makeText(this,"Email cannot be blank.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Keys.THREAD_ID_KEY.name, threadIds[position])
        Toast.makeText(this, threadIds[position], Toast.LENGTH_SHORT).show()
        startActivity(intent);
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
}