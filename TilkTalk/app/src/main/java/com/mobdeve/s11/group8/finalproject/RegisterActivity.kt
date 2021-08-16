package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener {
//            val chatIntent = Intent(this, ThreadActivity::class.java)
            val chatIntent = Intent(this, RegisterActivity::class.java) // for testing hehe
            startActivity(chatIntent)
        }
    }
}