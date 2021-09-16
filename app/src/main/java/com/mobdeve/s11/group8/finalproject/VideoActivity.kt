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
import android.util.Log

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
        Log.d("PATH", wvVideo.getUrl().toString())

        wvVideo.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                callJSFunction("javascript:init(\"${peerId}\")")

                if(intent.extras != null) {
                    val connectionId: String = intent.getStringExtra(Keys.CONNECTION_ID.name).toString()
                    Log.d("PeerJS", connectionId)
                    startVideoCall(connectionId)
                } else {
                    Log.d("PeerJS", "Receiver")
                }
            }
        }
    }

    private fun startVideoCall(connectionId : String) {
        callJSFunction("javascript:startCall(\"${connectionId}\")")
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