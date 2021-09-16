package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class ReceivingActivity : AppCompatActivity() {

    private lateinit var ivUser: ImageView
    private lateinit var tvUser: TextView
    private lateinit var tvDesc: TextView
    private lateinit var ibAccept: ImageButton
    private lateinit var ibReject: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiving)

        initComponents()
    }

    private fun initComponents() {
        this.ivUser = findViewById(R.id.iv_receiving_icon)
        this.tvUser = findViewById(R.id.tv_receiving_icon)
        this.tvDesc = findViewById(R.id.tv_calling_desc)
        this.ibAccept = findViewById(R.id.ib_receiving_accept)
        this.ibReject = findViewById(R.id.ib_receiving_reject)

        this.ibAccept.setOnClickListener {

        }

        this.ibReject.setOnClickListener {

        }
    }
}