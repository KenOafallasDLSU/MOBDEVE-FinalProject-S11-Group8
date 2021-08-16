package com.mobdeve.s11.group8.finalproject

class ThreadDataHelper{

    companion object{

        fun initData(): ArrayList<Thread>{
            val list = ArrayList<Thread>()
            list.add(
                Thread(
                    "1",
                    "username1",
                    "User One",
                    "This is user one's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "2",
                    "username2",
                    "User Two",
                    "This is user two's last message.",
                    "Friday, 8:00 PM"
                )
            )

            list.add(
                Thread(
                    "3",
                    "username3",
                    "User Three",
                    "This is user three's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "4",
                    "username4",
                    "User One",
                    "This is user four's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "5",
                    "username5",
                    "User Five",
                    "This is user five's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "6",
                    "username6",
                    "User Six",
                    "This is user six's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "7",
                    "username7",
                    "User Seven",
                    "This is user seven's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "8",
                    "username8",
                    "User Eight",
                    "This is user eight's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "9",
                    "username9",
                    "User Nine",
                    "This is user nine's last message.",
                    "Friday, 8:00 PM"
                )
            )
            return list
        }
    }
}