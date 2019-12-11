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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {     //생성된 viewholder와 material_chat_mine들을 holder와 연결해준다
        nparent=parent
        val inflater = LayoutInflater.from(parent.context)  //xml파일을 view객체로 만들기위해서 inflater를 선언
        return ViewHolder(inflater,parent,parent.context, requestProfileLink)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int){
        val data: Chatting_Element = chatlists[position]

        UserManagement.getInstance().me(object:MeV2ResponseCallback(){
            override fun onSuccess(result: MeV2Response) {  //email을 받아 @를 제외하고 아이디를 추출한다
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

    override fun getItemCount(): Int {  //리스트의 사이즈를 아이템의 숫자로 받는다
        return chatlists.size
    }

    //뷰홀더를 생성하고 뷰홀더는 material_chat_mine view를 묶어둔다
    //안의 데이터는 카카오톡 프로필사진과 채팅 내용이다
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