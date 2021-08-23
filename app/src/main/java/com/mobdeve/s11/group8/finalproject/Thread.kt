package com.mobdeve.s11.group8.finalproject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Thread constructor(
    var users: ArrayList<String>,
    var chats: ArrayList<Chat>,
) {
    fun getOtherUser(id : String) : String {
        if (users[0] == id){ return users[1] }
        return users[0]
    }
    fun getLastChat() : String {
        return chats.last().body
    }
    fun getLastUpdated(): String {
        return this.chats.last().getDateTimeString()
    }
}