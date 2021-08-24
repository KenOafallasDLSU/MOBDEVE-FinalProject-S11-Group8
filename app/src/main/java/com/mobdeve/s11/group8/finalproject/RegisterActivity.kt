package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etName: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var pbRegister: ProgressBar

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        this.initComponents()
        this.initFirebase()
    }

    private fun initFirebase() {
        this.mAuth = FirebaseAuth.getInstance()
        this.database = FirebaseDatabase.getInstance()
        this.reference = FirebaseDatabase.getInstance().getReference(Keys.USERS.name)
    }

    private fun initComponents() {
        this.etEmail = findViewById(R.id.et_register_email)
        this.etName = findViewById(R.id.et_register_name)
        this.etPassword = findViewById(R.id.et_register_password)
        this.btnRegister = findViewById(R.id.btn_register)
        this.tvLogin = findViewById(R.id.tv_register_login)
        this.pbRegister = findViewById(R.id.pb_register)

        this.btnRegister.setOnClickListener {
            var email: String = etEmail.text.toString().trim()
            var name: String = etName.text.toString().trim()
            var password: String = etPassword.text.toString().trim()

            if(!checkEmpty(email, name, password)) {
                // add new user to db
                var user = User(email, name, password);
                storeUser(user)
            }
        }

        this.tvLogin.setOnClickListener {
            val loginIntent = Intent(this, MainActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    private fun checkEmpty(email: String, name: String, password: String): Boolean {
        var hasEmpty: Boolean = false;

        if(email.isEmpty()) {
            this.etEmail.error = "Required field"
            this.etEmail.requestFocus()
            hasEmpty = true
        }

        if(name.isEmpty()) {
            this.etName.error = "Required field"
            this.etName.requestFocus()
            hasEmpty = true
        }

        if(password.isEmpty()) {
            this.etPassword.error = "Required field"
            this.etPassword.requestFocus()
            hasEmpty = true
        }

        if(!isValidEmail(etEmail.text.trim().toString())) {
            this.etEmail.error = "Please enter a valid e-mail"
            this.etName.requestFocus()
            hasEmpty = true
        }

        if(password.length < 6) {
            this.etPassword.error = "Password must be at least 6 characters"
            this.etName.requestFocus()
            hasEmpty = true
        }

        return hasEmpty
    }

    private fun isValidEmail(str: String): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun storeUser(user: User) {
        pbRegister.visibility = View.VISIBLE

        // Register the user to Firebase
        mAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    database.getReference(Keys.USERS.name)
                        .child(mAuth.currentUser!!.uid)
                        .setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                successfulRegistration()
                            } else {
                                failedRegistration()
                            }
                        }
                } else {
                    failedRegistration()
                }
            }
    }

    private fun successfulRegistration() {
        this.pbRegister.visibility = View.GONE
        Toast.makeText(this, "User successfully registered", Toast.LENGTH_SHORT).show();
        val chatIntent = Intent(this, ThreadActivity::class.java)
        startActivity(chatIntent)
        finish()
    }

    private fun failedRegistration() {
        this.pbRegister.visibility = View.GONE
        Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show()
    }
}