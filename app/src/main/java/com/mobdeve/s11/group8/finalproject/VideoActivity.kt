package com.mobdeve.s11.group8.finalproject

import android.Manifest
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
import android.widget.FrameLayout

import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas

class VideoActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private val peerId: String = "TilkTolk" + userId
    private lateinit var partnerId: String

    private var isPeerConnected = false
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child(Keys.USERS.name)
    private val userCallRef = usersRef.child(userId).child("callHandler")

    private lateinit var ibCam: ImageButton
    private lateinit var ibEnd: ImageButton
    private lateinit var ibMic: ImageButton

    private var isCamOn: Boolean = true
    private var isMicOn: Boolean = true

    private lateinit var mRtcEngine: RtcEngine
    private val appID: String = "404730d6397245638ec44e95a901c5e7"
    private val primaryCert: String = "02f6a90909f844f5838e168c837b5a2f"
    private lateinit var channelName: String

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        if(intent.extras != null) {
            val connectionId: String = intent.getStringExtra(Keys.CONNECTION_ID.name).toString()
            Log.d("PeerJS", connectionId)

            // go to channel of person you're calling
            channelName = connectionId
        } else {
            userCallRef.child("connectionID").setValue(peerId)
            userCallRef.child("callAccepted").setValue(true)
            Log.d("PeerJS", "Receiver")

            // go to your own channel
            channelName = peerId
        }

        initComponents()
        initializeAndJoinChannel()
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, appID, mRtcEventHandler)
        } catch (e: Exception) {

        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine!!.enableVideo()

        val localContainer = findViewById(R.id.fl_video_local) as FrameLayout

        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame = RtcEngine.CreateRendererView(baseContext)
        localContainer.addView(localFrame)

        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        // Join the channel without token.
        mRtcEngine!!.joinChannel(null, channelName, "", 0)
    }

    private fun setupRemoteVideo(uid: Int) {
        val remoteContainer = findViewById(R.id.fl_video_remote) as FrameLayout

        val remoteFrame = RtcEngine.CreateRendererView(baseContext)
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
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

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()

        super.onDestroy()
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

            //actually turn off cam
            mRtcEngine.muteLocalVideoStream(isCamOn)
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

            //actually toggle mic
            mRtcEngine.muteLocalAudioStream(isMicOn)
        }
    }
}