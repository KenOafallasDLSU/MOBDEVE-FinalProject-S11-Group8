package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ThreadActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var threadAdapter: ThreadAdapter
    private lateinit var btnAdd : FloatingActionButton
    private var threads: List<Thread> = ArrayList()

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
        initData()
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