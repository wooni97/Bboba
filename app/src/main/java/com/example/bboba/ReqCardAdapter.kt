package com.example.bboba

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.material_cardview.view.*
import kotlin.collections.ArrayList

class ReqCardAdapter(val requestLists:ArrayList<Prints_Request>): RecyclerView.Adapter<ReqCardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //뷰홀더 생성
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent, parent.context)
    }
    override fun getItemCount(): Int {
        return requestLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Prints_Request = requestLists[position]
        holder.bind(data)
    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup, val context: Context) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.material_cardview, parent, false)) {
        fun bind(data: Prints_Request) { //데이터들을 담는다
            if(data.picture_location!="") Glide.with(context).load(data.picture_location).transform(RoundedCorners(20)).into(itemView.appImage)
            else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(itemView.appImage)
            itemView.cardTitle.text = data.name
            itemView.total_page.text = data.total_page + " page"
            itemView.cardText.text = data.detail_request
            itemView.location.text = data.location_name
            itemView.date.text = data.date
            itemView.cardButton.setOnClickListener {
                val detailIntent = Intent(context, DetailViewActivity::class.java)
                detailIntent.putExtra("request_data", data) //상세보기를 누르면 그 데이터를 detailActivity로 넘겨준다
                context.startActivity(detailIntent)
            }
        }
    }
}