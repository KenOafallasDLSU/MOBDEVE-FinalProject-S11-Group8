package com.mobdeve.s11.group8.finalproject
import kotlin.collections.ArrayList

class Thread {
    var users : ArrayList<String>
    var chats: ArrayList<Chat>? = null

    constructor(users: ArrayList<String>, chats: ArrayList<Chat>) {
        this.users = users
        this.chats = chats
    }
    constructor(users: ArrayList<String>) {
        this.users = users
    }

    // will return the ID of the peer
    fun getOtherUser(id : String) : String {
        if (users[0] == id){ return users[1] }
        return users[0]
    }

    // will return last message
    fun getLastChat() : String? {
        return chats?.last()?.body
    }

    // will return the date time of last message
    fun getLastUpdated(): String? {
        return this.chats?.last()?.getDateTimeString()
    }
}