package com.mobdeve.s11.group8.finalproject

class ThreadDataHelper{

    companion object{

        fun initData(): ArrayList<Thread>{
            val list = ArrayList<Thread>()
            list.add(
                Thread(
                    "thread1id",
                    arrayOf("user1id", "user2id").toCollection(ArrayList<String>()),
                    arrayOf("chatid1", "chatid2", "chatid3").toCollection(ArrayList<String>()),
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "thread2id",
                    arrayOf("user2id", "user1id").toCollection(ArrayList<String>()),
                    arrayOf("chatid1", "chatid2", "chatid3").toCollection(ArrayList<String>()),
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "thread3id",
                    arrayOf("user3id", "user1id").toCollection(ArrayList<String>()),
                    arrayOf("chatid4", "chatid2", "chatid5").toCollection(ArrayList<String>()),
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "thread4id",
                    arrayOf("user4id", "user2id").toCollection(ArrayList<String>()),
                    arrayOf("chatid1", "chatid2", "chatid3").toCollection(ArrayList<String>()),
                    "Friday, 8:00 PM"
                )
            )
            return list
        }
    }
}