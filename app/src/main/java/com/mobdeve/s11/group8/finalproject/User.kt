package com.mobdeve.s11.group8.finalproject

import java.lang.Thread
import java.util.ArrayList

class User(val email: String, val name: String, val password: String) {
    val userId: String? = null
    val threads: ArrayList<Thread> = ArrayList<Thread>()

    fun addThread(thread: java.lang.Thread) {
        threads.add(thread)
    }
}