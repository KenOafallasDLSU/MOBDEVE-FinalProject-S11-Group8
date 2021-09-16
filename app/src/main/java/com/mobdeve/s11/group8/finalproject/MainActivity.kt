package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvCreate: TextView
    private lateinit var pbLogin: ProgressBar

    // Permissions
    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    val requestCode = 1

    // Firebase
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isPermissionGranted()) {
            askPermission()
        }

        this.initFirebase()
        this.initComponents()

        if (this.mAuth.currentUser != null) {
            startActivity(Intent(this, ThreadActivity::class.java))
            finish()
        }
    }

    private fun isPermissionGranted(): Boolean {
        permissions.forEach {
            if(ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    private fun initComponents() {
        this.etEmail = findViewById(R.id.et_login_email)
        this.etPassword = findViewById(R.id.et_login_password)
        this.btnLogin = findViewById(R.id.btn_login)
        this.tvCreate = findViewById(R.id.tv_login_register)
        this.pbLogin = findViewById(R.id.pb_login)

        this.btnLogin.setOnClickListener {
            var email = etEmail.text.toString().trim()
            var password = etPassword.text.toString().trim()
            if(!checkEmpty(email, password)) {
                signIn(email, password)
            }
        }

        this.tvCreate.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
            finish()
        }
    }

    private fun checkEmpty(email: String, password: String): Boolean {
        var hasEmpty: Boolean = false;

        if(email.isEmpty()) {
            this.etEmail.error = "Required field"
            this.etEmail.requestFocus()
            hasEmpty = true
        }

        if(password.isEmpty()) {
            this.etPassword.error = "Required field"
            this.etPassword.requestFocus()
            hasEmpty = true
        }

        return hasEmpty
    }

    private fun signIn(email: String, password: String) {
        this.pbLogin.visibility = View.VISIBLE

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val chatIntent = Intent(this, ThreadActivity::class.java)
                    startActivity(chatIntent)
                    finish()
                } else {
                    failedLogin();
                }
            }
    }

    private fun failedLogin() {
        this.pbLogin.visibility = View.GONE
        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
    }
}