package com.mobdeve.s11.group8.finalproject

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import java.util.*

class VideoActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private val peerId: String = "TilkTolk" + userId

    private val rootRef = FirebaseDatabase.getInstance().reference
    private val usersRef = rootRef.child(Keys.USERS.name)
    private val userCallRef = usersRef.child(userId).child("callHandler")

    private lateinit var ibCam: ImageButton
    private lateinit var ibEnd: ImageButton
    private lateinit var ibMic: ImageButton

    private var isCamOn: Boolean = true
    private var isMicOn: Boolean = true

    private lateinit var mRtcEngine: RtcEngine

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                Log.d("REMOTE", uid.toString())
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

        } else {
            userCallRef.child("connectionID").setValue(peerId)
            userCallRef.child("callAccepted").setValue(true)
            Log.d("PeerJS", "Receiver")

        }

        initComponents()
        initializeAndJoinChannel()
    }

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

        // Join the channel without token.
        mRtcEngine!!.joinChannel(getString(R.string.agora_token), getString(R.string.agora_channel), "", 0)
    }

    private fun setupRemoteVideo(uid: Int) {
        Log.d("REMOTE", "INIT")
        val remoteContainer = findViewById(R.id.fl_video_remote) as FrameLayout

        if (remoteContainer.childCount >= 1) {
            return
        }

        val remoteFrame: SurfaceView = RtcEngine.CreateRendererView(baseContext)
        remoteFrame.setZOrderOnTop(true)
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
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
            Log.d("VIDEO", isCamOn.toString())
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
            Log.d("AUDIO", isCamOn.toString())
        }
    }
}