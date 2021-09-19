package com.mobdeve.s11.group8.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Matcher
import java.util.regex.Pattern

class ReceivingActivity : AppCompatActivity() {

    // gets user info from Firebase auth
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private val peerId: String = "TilkTolk" + userId
    private lateinit var partnerId: String
    private lateinit var partnerName: String

    // gets Firebase db references
    private var rootRef = FirebaseDatabase.getInstance().reference
    private var usersRef = rootRef.child(Keys.USERS.name)
    private var userCallRef = usersRef.child(userId).child("callHandler")

    // layout views
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

        //look for user in db
        this.partnerId = intent.getStringExtra(Keys.CALL_PARTNER.name).toString()

        // gets partner's name
        this.usersRef.child(partnerId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val desc = snapshot.child(Collections.dname.name).value.toString()
                tvDesc.text = desc + " is calling..."
                tvIcon.text = desc[0].toString()
            }
        })

        // when caller drops call before receiver answers,
        // remove call handler and finish this activity
        this.usersRef.child(userId).child("callHandler").child("incoming").addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == null) {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Call Ended",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    usersRef.child(partnerId).child("callHandler").setValue(null)
                    usersRef.child(userId).child("callHandler").setValue(null)
                    finish()
                }
            }
        })

        // on accept button click, starts video activity
        this.ibAccept.setOnClickListener {
            //go to call
            val callIntent = Intent(this, VideoActivity::class.java)
            startActivity(callIntent)
            finish()
        }

        // on reject button click, sets accepted status in db to false
        this.ibReject.setOnClickListener {
            userCallRef.child("incoming").setValue(null)
            userCallRef.child("callAccepted").setValue(false)
            finish()
        }
    }
}