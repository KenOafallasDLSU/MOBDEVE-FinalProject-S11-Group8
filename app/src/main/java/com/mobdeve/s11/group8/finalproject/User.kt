package com.mobdeve.s11.group8.finalproject

class User {
    var email: String
        private set
    var username: String
        private set
    var password: String
        private set

    constructor(email: String, username: String, password: String) {
        this.email = email
        this.username = username
        this.password = password
    }
}