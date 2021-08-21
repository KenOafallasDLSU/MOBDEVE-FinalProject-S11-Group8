package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.et_register_email)
        val etName = findViewById<EditText>(R.id.et_register_name)
        val etPassword = findViewById<EditText>(R.id.et_register_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        btnRegister.setOnClickListener {
            if(etEmail.text.trim().isNotEmpty() && etName.text.trim().isNotEmpty() && etPassword.text.trim().isNotEmpty()) {
                if(isValidEmail(etEmail.text.trim().toString())) {
                    val chatIntent = Intent(this, ThreadActivity::class.java)
                    startActivity(chatIntent)
                }
                else {
                    Toast.makeText(this, "Please enter a valid e-mail.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, "Please don't leave any fields blank.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isValidEmail(str: String): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }
}