package com.example.bboba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.prints_request_list.view.*

class RecyclerViewAdapter(val requsestList:ArrayList<MainActivity.Prints_Request>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prints_request_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return requsestList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(requsestList[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(data : MainActivity.Prints_Request){
            //이미지 표시
            //데이터 표시
            itemView.user_name.text = data.user_name
            itemView.detail_req.text = data.detail_req
        }
    }
}