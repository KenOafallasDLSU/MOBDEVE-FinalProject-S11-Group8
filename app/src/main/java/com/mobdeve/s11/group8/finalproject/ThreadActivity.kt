package com.mobdeve.s11.group8.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.FieldPosition

class ThreadActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var threadAdapter: ThreadAdapter
    private lateinit var btnAdd : FloatingActionButton
    private var items: List<Thread> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        btnAdd = findViewById(R.id.btn_thread_add)
        btnAdd.setOnClickListener { v ->
            // TODO: Navigate to chat activity if exists; else, create toast
        }

        initRecyclerView()
        initData()
    }

    private fun initData(){
        this.items = ThreadDataHelper.initData()
        threadAdapter.submitList(items)
    }

    private fun initRecyclerView(){
        val rvThreads : RecyclerView = findViewById(R.id.rv_thread_list)
        rvThreads.layoutManager = LinearLayoutManager(this@ThreadActivity)
        threadAdapter = ThreadAdapter(this)
        rvThreads.adapter = threadAdapter
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, items[position].displayName, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // TODO: update view
    }
}