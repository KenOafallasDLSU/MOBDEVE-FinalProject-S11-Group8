package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class CallingActivity : AppCompatActivity() {

    private lateinit var tvDesc: TextView
    private lateinit var ibEnd: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        initComponents();
    }

    private fun initComponents() {
        this.tvDesc = findViewById(R.id.tv_calling_desc)
        this.ibEnd = findViewById(R.id.ib_calling_end)

        this.ibEnd.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
            finish()
        }

        // for VideoActivity testing
        var tvTest: TextView = findViewById(R.id.tv_test)
        tvTest.setOnClickListener {
            val chatIntent = Intent(this, VideoActivity::class.java)
            startActivity(chatIntent)
            finish()
        }
    }
}