package com.example.bboba

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.material_chat_mine.view.*

class MyChatAdapter(val chatlists:ArrayList<Chatting_Element>, val requestProfileLink: String?): RecyclerView.Adapter<MyChatAdapter.ViewHolder>() {
    var nparent: ViewGroup?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        nparent=parent
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater,parent,parent.context, requestProfileLink)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int){
        val data: Chatting_Element = chatlists[position]

        UserManagement.getInstance().me(object:MeV2ResponseCallback(){
            override fun onSuccess(result: MeV2Response) {
                val userRealId = result.kakaoAccount.email.substring(0,result.kakaoAccount.email.indexOf('@'))//아이디 추출
                val profileLink = result.kakaoAccount.profile.profileImageUrl?:""
                if(data.username==userRealId){
                    holder.bind(data,1,profileLink)
                }
                else{
                    holder.bind(data,2,profileLink)
                }
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
        })
    }

    override fun getItemCount(): Int {
        return chatlists.size
    }
    class ViewHolder(inflater: LayoutInflater, parent : ViewGroup, val context: Context, val requestProfileLink: String?):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.material_chat_mine, parent, false)){
        fun bind(data: Chatting_Element, type: Int, link:String){
            itemView.chat_mine.text = data.chat
            if(type==1) {
                if(link!="") Glide.with(context).load(link).transform(RoundedCorners(20)).into(itemView.partner_image_m)
                else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(itemView.partner_image_m)
            }
            else{
                if(requestProfileLink!=null) Glide.with(context).load(requestProfileLink).transform(RoundedCorners(20)).into(itemView.partner_image_m)
                else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(itemView.partner_image_m)
            }
        }
    }
}