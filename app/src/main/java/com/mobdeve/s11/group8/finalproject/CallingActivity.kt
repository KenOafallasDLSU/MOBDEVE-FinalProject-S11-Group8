package com.mobdeve.s11.group8.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CallingActivity : AppCompatActivity() {

    private lateinit var tvDesc: TextView
    private lateinit var ibEnd: ImageButton

    private lateinit var partnerId: String

    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child("USERS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        //get partner ID from intent
        partnerId = intent.getStringExtra(Keys.CALL_PARTNER.name).toString()

        initComponents();

        listenIfCallAccepted()
    }

    private fun initComponents() {
        this.tvDesc = findViewById(R.id.tv_calling_desc)
        this.ibEnd = findViewById(R.id.ib_calling_end)

        this.ibEnd.setOnClickListener {
            finish()
        }

        // for VideoActivity testing
        var tvTest: TextView = findViewById(R.id.tv_test)
        tvTest.setOnClickListener {
            val chatIntent = Intent(this, VideoActivity::class.java)
            startActivity(chatIntent)
            finish()
        }
    }

    private fun listenIfCallAccepted() {
        usersRef.child(partnerId).child("callHandler").child("callAccepted").addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() == "true") {
                    //listenForConnectionID()

                } else if(snapshot.value.toString() == "false") {
                    usersRef.child(partnerId).child("callHandler").setValue(null)
                    finish()
                }
            }
        })
    }

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
            }
        })
    }

}