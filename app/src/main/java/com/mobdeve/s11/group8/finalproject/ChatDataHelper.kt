package com.mobdeve.s11.group8.finalproject

import java.util.*
import kotlin.collections.ArrayList

class ChatDataHelper{
    companion object {
        fun initData(): ArrayList<Chat>{
            val list = ArrayList<Chat>()
            list.add(
                Chat(
                    "0",
                    "2",
                    "1",
                    "Thanks for hiring me boss!!!",
                    GregorianCalendar(2021,7,17,20,21,0)
                )
            )
            list.add(
                Chat(
                    "1",
                    "1",
                    "2",
                    "Meet me at these coordinates tonight 02:00 sharp",
                    GregorianCalendar(2021,7,21,20,32,0)
                )
            )
            list.add(
                Chat(
                    "2",
                    "2",
                    "1",
                    "Okii noted c:",
                    GregorianCalendar(2021,7,21,21,21,0)
                )
            )
            list.add(
                Chat(
                    "3",
                    "2",
                    "1",
                    "What should I bring againn??",
                    GregorianCalendar(2021,7,21,21,23,0)
                )
            )
            list.add(
                Chat(
                    "4",
                    "1",
                    "2",
                    "Bring a shovel and a flashlight. Don't let anyone follow you",
                    GregorianCalendar(2021,7,21,21,23,0)
                )
            )
            list.add(
                Chat(
                    "5",
                    "2",
                    "1",
                    "rightt i'll be there!!",
                    GregorianCalendar(2021,7,21,22,0,0)
                )
            )
            return list
        }
    }
}
