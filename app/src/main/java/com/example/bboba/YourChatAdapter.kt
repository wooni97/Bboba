package com.example.bboba

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.material_chat_mine.view.*
import kotlinx.android.synthetic.main.material_chat_yours.view.*

class YourChatAdapter (val chatlists:ArrayList<Chatting_Element>): RecyclerView.Adapter<YourChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater,parent,parent.context)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int){
        val data: Chatting_Element = chatlists[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return chatlists.size
    }

    class ViewHolder(inflater: LayoutInflater, parent : ViewGroup, val context: Context):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.material_chat_yours, parent, false)){
        fun bind(data: Chatting_Element){
            itemView.chat_partner.text = data.chat
        }
    }
}