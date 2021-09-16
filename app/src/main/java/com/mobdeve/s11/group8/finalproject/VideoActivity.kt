package com.mobdeve.s11.group8.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import android.content.Intent

class VideoActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private val peerId: String = "TilkTolk" + userId
    private lateinit var partnerId: String

    private var isPeerConnected = false
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child("USERS")
    private val userCallRef = usersRef.child(userId).child("callHandler")

    private lateinit var ibCam: ImageButton
    private lateinit var ibEnd: ImageButton
    private lateinit var ibMic: ImageButton
    private lateinit var wvVideo: WebView

    private var isCamOn: Boolean = true
    private var isMicOn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        initComponents()
        setupWebView()
    }

    // initializers
    private fun initComponents() {
        this.ibCam = findViewById(R.id.ib_video_cam)
        this.ibEnd = findViewById(R.id.ib_video_end)
        this.ibMic = findViewById(R.id.ib_video_mic)

        this.ibCam.setOnClickListener {
            if(isCamOn) {
                ibCam.setImageResource(R.drawable.offcam)
                isCamOn = false
            }
            else {
                ibCam.setImageResource(R.drawable.oncam)
                isCamOn = true
            }

            callJSFunction("javascript:toggleVideo(\"${this.isCamOn}\")")
        }

        this.ibEnd.setOnClickListener {
            finish()
        }

        this.ibMic.setOnClickListener {
            if(isMicOn) {
                ibMic.setImageResource(R.drawable.offmic)
                isMicOn = false
            }
            else {
                ibMic.setImageResource(R.drawable.onmic)
                isMicOn = true
            }

            callJSFunction("javascript:toggleAudio(\"${this.isMicOn}\")")
        }
    }

    private fun setupWebView() {
        this.wvVideo = findViewById(R.id.wv_video)
        wvVideo.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        wvVideo.settings.javaScriptEnabled = true
        wvVideo.settings.mediaPlaybackRequiresUserGesture = false
        wvVideo.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        wvVideo.loadUrl(filePath)

        wvVideo.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    private fun initializePeer() {
        callJSFunction("javascript:init(\"${this.peerId}\")")
        userCallRef.child("incoming").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                receiveCallRequest(snapshot.value as? String)
            }
        })
    }

    //move to receiving activity
    private fun receiveCallRequest(caller: String?) {
        if (caller == null) return

        //on accept
        userCallRef.child("connectionID").setValue(this.peerId)
        userCallRef.child("callAccepted").setValue(true)
        //move to call

        //on reject
        userCallRef.child("incoming").setValue(false)
    }

    //called when a user initiates a call, move to ChatActivity
    private fun sendCallRequest() {
        if(!isPeerConnected) {
            Toast.makeText(this, "Cannot connect. Please check internet", Toast.LENGTH_LONG).show()
            return
        }

        usersRef.child(partnerId).child("callHandler").child("incoming").setValue(userId)
        usersRef.child(partnerId).child("callHandler").child("callAccepted").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() == "true") {
                    listenForConnectionID()
                }
            }
        })
    }

    //waits for other user to answer call, move to CallingActivity
    private fun listenForConnectionID() {
        usersRef.child(partnerId).child("callHandler").child("connectionID").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                //start the call
                callJSFunction("javascript:startCall(\"${snapshot.value}\")")
            }
        })
    }

    private fun callJSFunction(functionString: String) {
        wvVideo.post { wvVideo.evaluateJavascript(functionString, null) }
    }

    // DO NOT REMOVE
    // USED BY JSINTERFACE
    fun onPeerConnected() {
        this.isPeerConnected = true
    }

    // exit handlers
    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        userCallRef.setValue(null)
        wvVideo.loadUrl("about:blank")
        super.onDestroy()
    }
}