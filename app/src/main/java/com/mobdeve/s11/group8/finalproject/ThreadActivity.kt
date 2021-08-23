package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ThreadActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var threadAdapter: ThreadAdapter
    private lateinit var btnAdd : FloatingActionButton
    private var threads: List<Thread> = ArrayList()

    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    private var user: FirebaseUser? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        btnAdd = findViewById(R.id.btn_thread_add)
        btnAdd.setOnClickListener { v ->
            // TODO: look for user with email,
            // create new thread if exists
            // do not store thread in db yet
        }

        initRecyclerView()
        initFirebase()
        initData()
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
        reference = database!!.getReference().child(Collections.users.name)
        user =  FirebaseAuth.getInstance().currentUser
        userId = user?.uid ?: ""
        // get threads from currently logged in user
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
    }

    private fun initData(){
        this.threads = ThreadDataHelper.initData()
        threadAdapter.submitList(threads)
    }

    private fun initRecyclerView(){
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter(this)
        rvThreads.adapter = threadAdapter
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Keys.THREAD_ID_KEY.name, threads[position].id)
        startActivity(intent);
    }

    override fun onResume() {
        super.onResume()
        // TODO: update view
    }
}