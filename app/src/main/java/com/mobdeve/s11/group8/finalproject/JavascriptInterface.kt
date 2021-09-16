package com.mobdeve.s11.group8.finalproject

import android.webkit.JavascriptInterface

class JavascriptInterface(val videoActivity: VideoActivity) {

    @JavascriptInterface
    public fun onPeerConnected() {
        videoActivity.onPeerConnected()
    }
}