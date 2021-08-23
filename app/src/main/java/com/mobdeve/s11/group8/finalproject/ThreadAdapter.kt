package com.mobdeve.s11.group8.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

interface OnItemClickListener{
    fun onItemClick(position : Int)
}

class ThreadAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ThreadViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_thread, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ThreadViewHolder -> {
                holder.bind(this.items.get(position), this.listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(threads : List<String>){
        this.items = threads
    }

    class ThreadViewHolder constructor(itemView : View) : RecyclerView.ViewHolder(itemView){

        private var senderName: String? = null
        private var lastUpdated: String? = null
        private var lastChat: String? = null
        private var otherId : String? = null

        val avatarLetter : TextView = itemView.findViewById(R.id.tv_thread_item_avatar_letter)
        val displayName : TextView = itemView.findViewById(R.id.tv_thread_item_name)
        val textMessage : TextView = itemView.findViewById(R.id.tv_thread_item_text)
        val date : TextView = itemView.findViewById(R.id.tv_thread_item_time)
        val avatarBackground : CircleImageView = itemView.findViewById(R.id.iv_thread_item_avatar)
        val item : CardView = itemView.findViewById(R.id.cv_item_thread)

        private var database: FirebaseDatabase? = null
        private var reference: DatabaseReference? = null
        private var user: FirebaseUser? = null
        private var userId: String? = null

        fun bind(threadId : String, listener : OnItemClickListener) {

            this.item.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

            initFirebase()

            // retrieve thread from database
            database!!.reference.child(Collections.threads.name).child(threadId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val tempusers = snapshot.child("users").value as ArrayList<String>
                        otherId = if (tempusers[0] == userId){ tempusers[1] } else { tempusers[0] }

                        lastUpdated = snapshot.child("lastUpdated").value.toString()
                        lastChat = snapshot.child("lastChat").value.toString()

                        // retrieve other user's name from database
                        reference?.child(otherId!!)?.child("name")?.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                senderName = snapshot.value.toString()
                                updateComponents()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                senderName = "user"
                            }
                        })

                        updateComponents()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        println(error)
                    }
                })
        }

        fun updateComponents(){
            displayName.setText(senderName)
            avatarLetter.setText(senderName?.get(0)?.toString())
            textMessage.setText(lastChat)
            date.setText(lastUpdated)
            val color: Int = itemView.resources
                .getIntArray(R.array.appcolors)[(senderName?.length
                ?: 0) % 5]
            avatarBackground.background.setTint(color)
        }

        fun initFirebase(){
            database = FirebaseDatabase.getInstance()
            reference = database!!.reference.child(Keys.USERS.name)
            user =  FirebaseAuth.getInstance().currentUser
            userId = user?.uid
        }
    }
}