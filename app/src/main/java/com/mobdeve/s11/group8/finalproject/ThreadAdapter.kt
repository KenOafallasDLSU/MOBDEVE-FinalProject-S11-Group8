package com.mobdeve.s11.group8.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
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

        private var senderName : String? = null
        private var lastUpdated: String? = null
        private var lastChat: String? = null
        private var otherId : String? = null

        private lateinit var tvAvatarLetter : TextView
        private lateinit var tvDisplayName : TextView
        private lateinit var tvTextMessage : TextView
        private lateinit var tvDate : TextView
        private lateinit var ivAvatarBackground : CircleImageView
        private lateinit var cvItem : CardView
        private lateinit var pBar : ProgressBar

        private lateinit var database: FirebaseDatabase
        private lateinit var reference: DatabaseReference
        private lateinit var user: FirebaseUser
        private lateinit var userId: String

        fun bind(threadId : String, listener : OnItemClickListener) {

            initFirebase()

            tvAvatarLetter = itemView.findViewById(R.id.tv_thread_item_avatar_letter)
            tvDisplayName = itemView.findViewById(R.id.tv_thread_item_name)
            tvTextMessage  = itemView.findViewById(R.id.tv_thread_item_text)
            tvDate = itemView.findViewById(R.id.tv_thread_item_time)
            ivAvatarBackground = itemView.findViewById(R.id.iv_thread_item_avatar)
            cvItem = itemView.findViewById(R.id.cv_item_thread)
            pBar = itemView.findViewById(R.id.pb_thread_item)

            this.cvItem.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

            // retrieve thread from database
            pBar.setVisibility(View.VISIBLE)
            database.reference.child(Collections.threads.name).child(threadId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val temp = snapshot.child(Collections.users.name).value as ArrayList<String>
                        otherId = if (temp[0] == userId){ temp[1] } else { temp[0] }

                        lastUpdated = snapshot.child(Collections.lastUpdated.name).value.toString()
                        lastChat = snapshot.child(Collections.lastChat.name).value.toString()

                        // retrieve other user's name from database
                        reference.child(otherId!!).child(Collections.name.name).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                senderName = snapshot.value.toString()
                                updateComponents()
                                pBar.setVisibility(View.GONE)
                            }
                            override fun onCancelled(error: DatabaseError) {
                                senderName = "user"
                                pBar.setVisibility(View.GONE)
                            }
                        })

                        updateComponents()
                        pBar.setVisibility(View.GONE)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        println(error)
                    }
                })
        }

        fun updateComponents(){
            tvDisplayName.setText(senderName)
            tvAvatarLetter.setText(senderName?.get(0)?.toString())
            tvTextMessage.setText(lastChat)
            tvDate.setText(lastUpdated)
            val color: Int = itemView.resources.getIntArray(R.array.appcolors)[(senderName?.length ?: 0) % 5]
            ivAvatarBackground.background.setTint(color)
        }

        fun initFirebase(){
            database = FirebaseDatabase.getInstance()
            reference = database.reference.child(Keys.USERS.name)
            user = FirebaseAuth.getInstance().currentUser!!
            userId = user.uid
        }
    }
}