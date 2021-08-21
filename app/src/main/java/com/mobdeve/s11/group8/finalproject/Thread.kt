package com.mobdeve.s11.group8.finalproject

class Thread constructor(
    var id: String,
    var users: ArrayList<String>,
    var chats: ArrayList<String>,
    var updated: String,
) {
    fun getOtherUser(id : String) : String {
        if (users[0] == id){ return users[1] }
        return users[0]
    }
    fun getLastChat() : String {
        return chats.last()
    }
}