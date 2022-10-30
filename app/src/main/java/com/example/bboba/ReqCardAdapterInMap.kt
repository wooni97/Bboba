package com.example.bboba

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.material_cardview_map.view.*
import kotlin.collections.ArrayList

class ReqCardAdapterInMap(val requestLists:ArrayList<Prints_Request>): RecyclerView.Adapter<ReqCardAdapterInMap.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {//생성된 뷰홀더에 데이터를 binding
        val data: Prints_Request = requestLists[position]
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReqCardAdapterInMap.ViewHolder { //뷰홀더를 생성
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent, parent.context)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list, parent, false)
        //return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return requestLists.size
    }



    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup, val context: Context) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.material_cardview_map, parent, false)) {
        fun bind(data: Prints_Request) {
            itemView.map_cardview_total_page.text = data.total_page + " page"
            itemView.map_cardview_location.text = data.location_name
            itemView.map_cardview_date.text = data.date
            itemView.map_cardview_cardButton.setOnClickListener {
                val detailIntent = Intent(context, DetailViewActivity::class.java)
                detailIntent.putExtra("request_data", data)
                detailIntent.putExtra("fragmentNumber", 2)
                context.startActivity(detailIntent)
            }
        }
    }
}