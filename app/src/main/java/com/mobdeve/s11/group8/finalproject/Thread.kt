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

    fun getOtherUser(id : String) : String {
        if (users[0] == id){ return users[1] }
        return users[0]
    }
    fun getLastChat() : String? {
        return chats?.last()?.body
    }
    fun getLastUpdated(): String? {
        return this.chats?.last()?.getDateTimeString()
    }
}