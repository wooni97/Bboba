package com.example.bboba

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_detail_req.*
import kotlinx.android.synthetic.main.material_cardview.view.*
import kotlin.collections.ArrayList

class ReqCardAdapter(val requestLists:ArrayList<Prints_Request>): RecyclerView.Adapter<ReqCardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReqCardAdapter.ViewHolder { //뷰홀더를 생성
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent, parent.context)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list, parent, false)
        //return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return requestLists.size
    }

    override fun onBindViewHolder(holder: ReqCardAdapter.ViewHolder, position: Int) {
        val data: Prints_Request = requestLists[position]
        holder.bind(data)
    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup, val context: Context) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.material_cardview, parent, false)) {
        fun bind(data: Prints_Request) {
            UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                override fun onFailure(errorResult: ErrorResult?) {
                    Log.d("example", "aaabb=실패")
                }
                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.d("example", "aaabb=세션 닫힘")
                }

                override fun onSuccess(result: MeV2Response?) {
                }
            })
            if(data.picture_location!="") Glide.with(context).load(data.picture_location).transform(RoundedCorners(20)).into(itemView.appImage)
            else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(itemView.appImage)
            itemView.cardTitle.text = data.name
            itemView.total_page.text = data.total_page + " page"
            itemView.cardText.text = data.detail_request
            itemView.location.text = data.location_name
            itemView.date.text = data.date
            itemView.cardButton.setOnClickListener {
                val detailIntent = Intent(context, DetailViewActivity::class.java)
                detailIntent.putExtra("request_data", data)
                context.startActivity(detailIntent)
            }
        }
    }
}