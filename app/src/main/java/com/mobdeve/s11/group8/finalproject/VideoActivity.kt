package com.mobdeve.s11.group8.finalproject

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import java.util.*

class VideoActivity : AppCompatActivity() {

    // gets user from Firebase auth
    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private lateinit var connectionId: String

    // sets Firebase references
    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child(Keys.USERS.name)
    private val userCallRef = usersRef.child(userId).child("callHandler")

    // layout views
    private lateinit var ibCam: ImageButton
    private lateinit var ibEnd: ImageButton
    private lateinit var ibMic: ImageButton

    // for cam and mic toggles
    private var isCamOn: Boolean = true
    private var isMicOn: Boolean = true

    private lateinit var mRtcEngine: RtcEngine

    // sets up remote video when another user joins the call
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                Log.d("REMOTE", uid.toString())
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uid)
            }
        }

        // if the call partner left, end the call
        override fun onUserOffline(uid: Int, reason: Int) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        if(intent.extras != null) {
            // if user is calling, gets connectionid sent by receiver
            connectionId = intent.getStringExtra(Keys.CONNECTION_ID.name).toString()
            Log.d("Calling", connectionId)

        } else {
            // if user is receiving, sends connectionid to caller and status that call is accepted
            connectionId = userId
            userCallRef.child("connectionID").setValue(connectionId)
            userCallRef.child("callAccepted").setValue(true)
            Log.d("Calling", "Receiver")
        }

        initComponents()
        initializeAndJoinChannel()
    }

    // starts the RTC engine for video calls and starts the user's local video
    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_id), mRtcEventHandler)
        } catch (e: Exception) {

        }

        Log.d("LOCAL", "INIT")

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine!!.enableVideo()

        val localContainer = findViewById(R.id.fl_video_local) as FrameLayout

        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame: SurfaceView = RtcEngine.CreateRendererView(baseContext)
        localContainer.addView(localFrame)

        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        // Join the channel with token.
        mRtcEngine!!.joinChannel(getString(R.string.agora_token), getString(R.string.agora_channel), "", 0)
    }

    // starts the remote video obtained from the call partner's stream
    private fun setupRemoteVideo(uid: Int) {
        Log.d("REMOTE", "INIT")
        val remoteContainer = findViewById(R.id.fl_video_remote) as FrameLayout

        val remoteFrame: SurfaceView = RtcEngine.CreateRendererView(baseContext)
        remoteFrame.setZOrderOnTop(true)
        remoteFrame.setZOrderMediaOverlay(false)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    // exit handlers
    override fun onBackPressed() {
        usersRef.child(connectionId).child("callHandler").setValue(null)
        userCallRef.setValue(null)
        finish()
    }

    override fun onDestroy() {
        userCallRef.setValue(null)
        usersRef.child(connectionId).child("callHandler").setValue(null)
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

            //actually toggle cam
            mRtcEngine.muteLocalVideoStream(!isCamOn)
            Log.d("VIDEO", (!isCamOn).toString())
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
            mRtcEngine.muteLocalAudioStream(!isMicOn)
            Log.d("AUDIO", (!isMicOn).toString())
        }
    }
}