package com.mobdeve.s11.group8.finalproject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Thread constructor(
    var id: String,
    var users: ArrayList<String>,
    var chats: ArrayList<String>,
    var updated: Calendar,
) {
    fun getOtherUser(id : String) : String {
        if (users[0] == id){ return users[1] }
        return users[0]
    }
    fun getLastChat() : String {
        return chats.last()
    }
    fun getLastUpdated(): String {
        val dateTimeFormat = SimpleDateFormat("MMM d, yyyy h:mm a")
        return dateTimeFormat.format(this.updated.time)
    }
}