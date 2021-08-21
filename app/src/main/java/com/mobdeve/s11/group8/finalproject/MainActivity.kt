package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPassword = findViewById<EditText>(R.id.et_login_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            if(etEmail.text.trim().isNotEmpty() && etPassword.text.trim().isNotEmpty()) {
                val chatIntent = Intent(this, ThreadActivity::class.java)
                startActivity(chatIntent)
            }
            else {
                Toast.makeText(this, "Please don't leave any fields blank.", Toast.LENGTH_SHORT).show()
            }
        }

        val btnCreate = findViewById<TextView>(R.id.tv_login_register)
        btnCreate.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}