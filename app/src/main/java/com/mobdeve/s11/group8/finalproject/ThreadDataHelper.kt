package com.mobdeve.s11.group8.finalproject

import java.util.*
import kotlin.collections.ArrayList

class ThreadDataHelper{

    companion object{

        fun initData(): ArrayList<Thread>{
            val list = ArrayList<Thread>()
            list.add(
                Thread(
                    "thread1id",
                    arrayOf("user1id", "user2id").toCollection(ArrayList<String>()),
                    arrayOf("chatid1", "chatid2", "chatid3").toCollection(ArrayList<String>()),
                    GregorianCalendar(2021,7,17,20,21,0)
                )
            )
            list.add(
                Thread(
                    "thread2id",
                    arrayOf("user2id", "user1id").toCollection(ArrayList<String>()),
                    arrayOf("chatid1", "chatid2", "chatid3").toCollection(ArrayList<String>()),
                    GregorianCalendar(2021,7,17,20,21,0)
                )
            )
            list.add(
                Thread(
                    "thread3id",
                    arrayOf("user3id", "user1id").toCollection(ArrayList<String>()),
                    arrayOf("chatid4", "chatid2", "chatid5").toCollection(ArrayList<String>()),
                    GregorianCalendar(2021,7,17,20,21,0)
                )
            )
            list.add(
                Thread(
                    "thread4id",
                    arrayOf("user4id", "user2id").toCollection(ArrayList<String>()),
                    arrayOf("chatid1", "chatid2", "chatid3").toCollection(ArrayList<String>()),
                    GregorianCalendar(2021,7,17,20,21,0)
                )
            )
            return list
        }
    }
}