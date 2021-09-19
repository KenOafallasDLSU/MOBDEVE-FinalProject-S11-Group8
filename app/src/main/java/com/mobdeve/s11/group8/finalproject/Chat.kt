package com.mobdeve.s11.group8.finalproject

import java.text.SimpleDateFormat
import java.util.*

class Chat(
    val senderId: String,
    val receiverId: String,
    val body: String,
    val dateTimeSent: Calendar
) {
    // gets date formatted in h:mm a
    // example - 7:12 AM
    fun getTimeString(): String{
        val timeFormat = SimpleDateFormat("h:mm a")
        return timeFormat.format(this.dateTimeSent.time)
    }

    // gets date formatted in EEE h:mm a
    // example - Mon 7:12 AM
    fun getDayOfWeekTimeString(): String{
        val dayOfWeekTimeFormat = SimpleDateFormat("EEE h:mm a")
        return dayOfWeekTimeFormat.format(this.dateTimeSent.time)
    }

    // gets date formatted in MMM d, h:mm a
    // example - Jul 12, 7:12 AM
    fun getDayMonthTimeString(): String{
        val dayMonthTimeFormat = SimpleDateFormat("MMM d, h:mm a")
        return dayMonthTimeFormat.format(this.dateTimeSent.time)
    }

    // gets date formatted in MMM d, yyyy h:mm a
    // example Jul 12, 2012 7:12 AM
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