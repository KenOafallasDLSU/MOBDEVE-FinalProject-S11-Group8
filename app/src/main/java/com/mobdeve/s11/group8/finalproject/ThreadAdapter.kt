package com.mobdeve.s11.group8.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

interface OnItemClickListener{
    fun onItemClick(position : Int)
}

class ThreadAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<Thread> = ArrayList()

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

    fun submitList(threads : List<Thread>){
        this.items = threads
    }

    // Thread View Holder
    class ThreadViewHolder constructor(itemView : View) : RecyclerView.ViewHolder(itemView){
        val avatarLetter : TextView = itemView.findViewById(R.id.tv_thread_item_avatar_letter)
        val displayName : TextView = itemView.findViewById(R.id.tv_thread_item_name)
        val textMessage : TextView = itemView.findViewById(R.id.tv_thread_item_text)
        val date : TextView = itemView.findViewById(R.id.tv_thread_item_time)
        val avatarBackground : CircleImageView = itemView.findViewById(R.id.iv_thread_item_avatar)
        val item : CardView = itemView.findViewById(R.id.cv_item_thread)

        fun bind(thread : Thread, listener : OnItemClickListener) {
            avatarLetter.setText(thread.getOtherUser("user1id")[0].toString())
            textMessage.setText(thread.getLastChat())
            displayName.setText(thread.getOtherUser("user1id"))
            date.setText(thread.getLastUpdated())

            val color: Int = itemView.getResources().getIntArray(R.array.appcolors)[thread.getOtherUser("").length % 5]
            avatarBackground.background.setTint(color)

            this.item.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }
    }
}