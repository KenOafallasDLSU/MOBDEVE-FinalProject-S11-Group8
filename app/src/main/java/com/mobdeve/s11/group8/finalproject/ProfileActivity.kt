package com.mobdeve.s11.group8.finalproject

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvAvatarLetter: TextView
    private lateinit var tvName : TextView
    private lateinit var tvEmail: TextView
    private lateinit var etName: EditText
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button
    private lateinit var pBar : ProgressBar

    private lateinit var name : String
    private lateinit var email : String

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initRecyclerView()
        initFirebase()
        initListeners()
    }

    private fun initListeners() {
        btnEdit.setOnClickListener{ v ->
            val editName = etName.text.toString().trim()
            if(editName.isEmpty()) {
                this.etName.error = "Required field"
                this.etName.requestFocus()
            } else {
                this.reference.child(userId).child(Collections.dname.name).setValue(editName)
                Toast.makeText(this, "Succesfully updated user.", Toast.LENGTH_SHORT).show()
                finish();
                startActivity(intent);
            }
        }
        btnLogout.setOnClickListener{ v ->
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Log Out")
            builder.setMessage("Are you sure you want to log out?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener{dialog, id ->
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Successfully logged out user.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                finish();
                startActivity(intent)
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener{dialog, id ->
                dialog.cancel()
            })
            builder.create().show()
        }
    }

    private fun initFirebase() {
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child(Keys.USERS.name)
        user = FirebaseAuth.getInstance().currentUser!!
        userId = user.uid

        pBar.visibility = View.VISIBLE
        this.reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pBar.visibility = View.GONE
                name = snapshot.child(Collections.dname.name).value.toString()
                email = snapshot.child(Collections.email.name).value.toString()
                val color = resources.getIntArray(R.array.appcolors)[(snapshot.value.toString().length) % 5]

                tvAvatarLetter.setText(name.get(0).toString())
                tvAvatarLetter.background.setTint(color)
                btnEdit.setBackgroundColor(color)
                tvName.setText(name)
                tvEmail.setText(email)
            }
            override fun onCancelled(error: DatabaseError) {
                pBar.visibility = View.GONE
                Toast.makeText(this@ProfileActivity, "Oh no! Something went wrong :(", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initRecyclerView() {
        tvAvatarLetter = findViewById(R.id.tv_profile_avatar_letter)
        tvName = findViewById(R.id.tv_profile_name)
        tvEmail = findViewById(R.id.tv_profile_email)
        etName = findViewById(R.id.et_profile_name)
        pBar = findViewById(R.id.pb_profile)
        btnEdit = findViewById(R.id.btn_profile_edit)
        btnLogout = findViewById(R.id.btn_profile_logout)
    }
}