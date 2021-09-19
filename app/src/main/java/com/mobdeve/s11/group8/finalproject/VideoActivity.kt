package com.mobdeve.s11.group8.finalproject

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas


class VideoActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser!!
    private val userId: String = user.uid
    private lateinit var channel: String
    private lateinit var agoraToken: String

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
            agoraToken = intent.getStringExtra(Keys.CONNECTION_ID.name).toString()
            channel = intent.getStringExtra(Keys.PARTNER_ID.name).toString()

            Log.d("AgoraToken", agoraToken)

            initComponents()
            initializeAndJoinChannel()

        } else {
            channel = userId
            val url = "https://tilktalk-key-server.herokuapp.com/access_token?channel=${channel}&uid=${userId}"
            Log.d("RequestUrl", url)

            val requestQueue = Volley.newRequestQueue(this)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    agoraToken = response.getString("token")
                    userCallRef.child("connectionID").setValue(agoraToken)
                    userCallRef.child("callAccepted").setValue(true)

                    Log.d("AgoraToken", agoraToken)

                    initComponents()
                    initializeAndJoinChannel()
                },
                Response.ErrorListener { error ->
                    val toast = Toast.makeText(
                        applicationContext,
                        "Failed to make connection",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            )

            requestQueue.add(jsonObjectRequest)
        }
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
        mRtcEngine!!.joinChannel(agoraToken, channel, "", 0)
    }

    private fun setupRemoteVideo(uid: Int) {
        Log.d("REMOTE", "INIT")
        val remoteContainer = findViewById(R.id.fl_video_remote) as FrameLayout

        if (remoteContainer.childCount >= 1) {
            return
        }

        val remoteFrame: SurfaceView = RtcEngine.CreateRendererView(baseContext)
        remoteFrame.setZOrderOnTop(true)
        remoteFrame.setZOrderMediaOverlay(false)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    // exit handlers
    override fun onBackPressed() {
        usersRef.child(peerId).child("callHandler").setValue(null)
        usersRef.child(userId).child("callHandler").setValue(null)
        finish()
    }

    override fun onDestroy() {
        userCallRef.setValue(null)
        usersRef.child(peerId).child("callHandler").setValue(null)
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