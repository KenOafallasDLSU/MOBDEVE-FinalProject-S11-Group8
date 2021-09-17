package com.mobdeve.s11.group8.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReceivingActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private val peerId: String = "TilkTolk" + userId

    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child(Keys.USERS.name)
    private val userCallRef = usersRef.child(userId).child("callHandler")

    private lateinit var ivUser: ImageView
    private lateinit var tvIcon: TextView
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
        this.tvIcon = findViewById(R.id.tv_receiving_icon)
        this.tvDesc = findViewById(R.id.tv_receiving_desc)
        this.ibAccept = findViewById(R.id.ib_receiving_accept)
        this.ibReject = findViewById(R.id.ib_receiving_reject)

        var desc = "nameee"
        tvIcon.text = desc[0].toString()
        tvDesc.text = "$desc wants to video call"

        this.ibAccept.setOnClickListener {
            //go to call
            val callIntent = Intent(this, VideoActivity::class.java)
            startActivity(callIntent)
        }

        this.ibReject.setOnClickListener {
            userCallRef.child("incoming").setValue(null)
            userCallRef.child("callAccepted").setValue(false)

            finish()
        }
    }
}