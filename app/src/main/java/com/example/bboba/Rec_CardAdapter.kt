package com.example.bboba

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.material_cardview.view.*

class Rec_CardAdapter(val requestLists:ArrayList<RequestList>): RecyclerView.Adapter<Rec_CardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Rec_CardAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list, parent, false)
        //return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return requestLists.size
    }

    override fun onBindViewHolder(holder: Rec_CardAdapter.ViewHolder, position: Int) {
        val data: RequestList = requestLists[position]
        holder.bind(data)
    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.material_cardview, parent, false)) {
        fun bind(data: RequestList) {
            itemView.cardTitle.text = data.user_name
            itemView.cardSubTitle.text = data.total_page + " page"
            itemView.cardText.text = data.detail_req
        }
    }
}