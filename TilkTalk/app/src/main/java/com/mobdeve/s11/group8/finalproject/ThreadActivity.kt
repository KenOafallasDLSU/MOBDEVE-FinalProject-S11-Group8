package com.mobdeve.s11.group8.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ThreadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        val t = Thread(
            "1",
            "username",
            "display name",
            "this is the last message",
            "Friday, 8:00 PM"
        )

        print(t.date)
        t.date = "Friday, 7:00 PM"
        print(t.date)
    }
}