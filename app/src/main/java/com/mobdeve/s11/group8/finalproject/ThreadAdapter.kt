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

// click listener interface
interface OnItemClickListener{
    fun onItemClick(position : Int)
}

class ThreadAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    // list of threads
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

            // initialize views
            tvAvatarLetter = itemView.findViewById(R.id.tv_thread_item_avatar_letter)
            tvDisplayName = itemView.findViewById(R.id.tv_thread_item_name)
            tvTextMessage  = itemView.findViewById(R.id.tv_thread_item_text)
            tvDate = itemView.findViewById(R.id.tv_thread_item_time)
            ivAvatarBackground = itemView.findViewById(R.id.iv_thread_item_avatar)
            cvItem = itemView.findViewById(R.id.cv_item_thread)
            pBar = itemView.findViewById(R.id.pb_thread_item)

            // initialize click listeners for every item in the rev=cycler view
            this.cvItem.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

            // retrieve thread from database
            pBar.visibility = View.VISIBLE
            database.reference.child(Collections.threads.name).child(threadId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        // if snapshot is not empty
                        if (snapshot.hasChildren()){

                            // get name of peer
                            val temp = snapshot.child(Collections.users.name).value as ArrayList<String>
                            otherId = if (temp[0] == userId){ temp[1] } else { temp[0] }

                            // get last message and last message update time with peer
                            lastUpdated = snapshot.child(Collections.lastUpdated.name).value.toString()
                            lastChat = snapshot.child(Collections.lastChat.name).value.toString()

                            // retrieve other user's name from database
                            reference.child(otherId!!).child(Collections.dname.name).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    pBar.visibility = View.GONE
                                    senderName = snapshot.value.toString()
                                    updateComponents()
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    pBar.visibility = View.GONE

                                    // this is the default name if failed to retrieve sender name
                                    senderName = "user"
                                }
                            })

                            pBar.visibility = View.GONE
                            updateComponents()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        pBar.visibility = View.GONE
                        println(error)
                    }
                })
        }

        fun updateComponents(){
            // update thread or chat based on data of peer
            tvDisplayName.text = senderName
            tvAvatarLetter.text = senderName?.get(0)?.toString()
            tvTextMessage.text = lastChat
            tvDate.text = lastUpdated
            val color: Int = itemView.resources.getIntArray(R.array.appcolors)[(senderName?.length ?: 0) % 5]
            ivAvatarBackground.background.setTint(color)
        }

        fun initFirebase(){
            // initialize firebase
            database = FirebaseDatabase.getInstance()
            reference = database.reference.child(Keys.USERS.name)
            user = FirebaseAuth.getInstance().currentUser!!
            userId = user.uid
        }
    }
}