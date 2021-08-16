package com.mobdeve.s11.group8.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ThreadActivity : AppCompatActivity() {

    private lateinit var threadAdapter: ThreadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)
        initRecyclerView()
        initData()
    }

    private fun initData(){
        val data = ThreadDataHelper.initData()
        threadAdapter.submitList(data)
    }

    private fun initRecyclerView(){
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter()
        rvThreads.adapter = threadAdapter
    }
}