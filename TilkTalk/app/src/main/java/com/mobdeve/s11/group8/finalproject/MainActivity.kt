package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
//            val chatIntent = Intent(this, ThreadActivity::class.java)
            val chatIntent = Intent(this, RegisterActivity::class.java) // for testing hehe
            startActivity(chatIntent)
        }

        val registerButton = findViewById<TextView>(R.id.tv_login_register)
        registerButton.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}