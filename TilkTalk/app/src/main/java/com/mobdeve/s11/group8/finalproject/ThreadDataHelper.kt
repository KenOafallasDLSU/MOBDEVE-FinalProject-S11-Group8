package com.mobdeve.s11.group8.finalproject

class ThreadDataHelper{

    companion object{

        fun initData(): ArrayList<Thread>{
            val list = ArrayList<Thread>()
            list.add(
                Thread(
                    "1",
                    "username132323",
                    "A User One",
                    "This is user one's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "2",
                    "username2321",
                    "F User Two",
                    "This is user two's last message.",
                    "Friday, 8:00 PM"
                )
            )

            list.add(
                Thread(
                    "3",
                    "username34",
                    "B User Three",
                    "This is user three's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "4",
                    "username4323",
                    "C User Four",
                    "This is user four's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "5",
                    "username5",
                    "D User Five",
                    "This is user five's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "6",
                    "username653545",
                    "E User Six",
                    "This is user six's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "7",
                    "username743243",
                    "F User Seven",
                    "This is user seven's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "8",
                    "username84324324",
                    "G User Eight",
                    "This is user eight's last message.",
                    "Friday, 8:00 PM"
                )
            )
            list.add(
                Thread(
                    "9",
                    "username933331",
                    "H User Nine",
                    "This is user nine's last message.",
                    "Friday, 8:00 PM"
                )
            )
            return list
        }
    }
}