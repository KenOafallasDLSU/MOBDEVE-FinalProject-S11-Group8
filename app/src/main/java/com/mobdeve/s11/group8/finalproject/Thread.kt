package com.mobdeve.s11.group8.finalproject

class Thread constructor(
    var id: String,
    var username: String,
    var displayName: String,
    var text: String,
    var date: String,
) {
    fun getFirstNameCharacter(): Char {
        return this.displayName[0]
    }
    fun getDisplayNameLength() : Int {
        return this.username.length % 5
    }
}