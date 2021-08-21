package com.mobdeve.s11.group8.finalproject

import java.text.SimpleDateFormat
import java.util.*

class Chat(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val body: String,
    private val dateTimeSent: Calendar
) {
    fun getTimeString(): String{
        val timeFormat = SimpleDateFormat("h:mm a")
        return timeFormat.format(this.dateTimeSent.time)
    }

    fun getDayOfWeekTimeString(): String{
        val dayOfWeekTimeFormat = SimpleDateFormat("EEE h:mm a")
        return dayOfWeekTimeFormat.format(this.dateTimeSent.time)
    }

    fun getDayMonthTimeString(): String{
        val dayMonthTimeFormat = SimpleDateFormat("MMM d, h:mm a")
        return dayMonthTimeFormat.format(this.dateTimeSent.time)
    }

    fun getDateTimeString(): String{
        val dateTimeFormat = SimpleDateFormat("MMM d, yyyy h:mm a")
        return dateTimeFormat.format(this.dateTimeSent.time)
    }

    fun getDayOfYear(): Int {
        return dateTimeSent.get(Calendar.DAY_OF_YEAR)
    }

    fun getWeekOfYear(): Int {
        return dateTimeSent.get(Calendar.WEEK_OF_YEAR)
    }

    fun getYear(): Int {
        return dateTimeSent.get(Calendar.YEAR)
    }
}