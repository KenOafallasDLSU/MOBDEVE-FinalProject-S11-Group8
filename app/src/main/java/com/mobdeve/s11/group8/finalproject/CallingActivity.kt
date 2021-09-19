package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CallingActivity : AppCompatActivity() {

    private lateinit var tvIcon: TextView
    private lateinit var tvDesc: TextView
    private lateinit var ibEnd: ImageButton

    private lateinit var partnerId: String
    private lateinit var userId: String

    // gets references from Firebase
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child(Keys.USERS.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        //get partner ID from intent
        partnerId = intent.getStringExtra(Keys.CALL_PARTNER.name).toString()
        userId = FirebaseAuth.getInstance().uid.toString();

        initComponents();

        listenIfCallAccepted()
    }

    private fun initComponents() {
        this.tvIcon = findViewById(R.id.tv_calling_icon)

        this.tvDesc = findViewById(R.id.tv_calling_desc)
        this.ibEnd = findViewById(R.id.ib_calling_end)

        var desc = intent.getStringExtra(Keys.PARTNER_NAME.name).toString()
        this.tvIcon.text = desc[0].toString()
        this.tvDesc.text = "Calling $desc..."

        // removes callHandler from database when call is dropped
        this.ibEnd.setOnClickListener {
            usersRef.child(partnerId).child("callHandler").setValue(null)
            usersRef.child(userId).child("callHandler").setValue(null)
            finish()
        }
    }

    // listens for partner to accept the call
    // listens to connectionID on accept, ends activity on reject
    private fun listenIfCallAccepted() {
        usersRef.child(partnerId).child("callHandler").child("callAccepted").addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() == "true") {
                    listenForConnectionID()

                } else if(snapshot.value.toString() == "false") {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Call Ended",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    usersRef.child(partnerId).child("callHandler").setValue(null)
                    finish()
                }
            }
        })
    }

    // listens on Firebase for partner to open their channel and send connectionID
    // expected nonnull value
    // starts video activity on data change
    private fun listenForConnectionID() {
        usersRef.child(partnerId).child("callHandler").child("connectionID").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                //start the call
                val callIntent = Intent(this@CallingActivity, VideoActivity::class.java)
                callIntent.putExtra(Keys.CONNECTION_ID.name, snapshot.value.toString())
                startActivity(callIntent)
                finish()
            }
        })
    }

}