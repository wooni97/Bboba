package com.example.bboba

import android.content.ClipData
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.database.FirebaseDatabase
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.UserManagement
import kotlinx.android.synthetic.main.activity_chat.*
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.GroupAdapter
import kotlinx.android.synthetic.main.material_chat_mine.view.*
import kotlinx.android.synthetic.main.material_chat_yours.view.*


class ChatActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //채팅 로그를 띄우기 위해 adapting을 해야함
        chatting.adapter = adapter

        activity_send.setOnClickListener {
            sending()
        }


        /*
    override fun onCreateViewHolder(parent: ViewGroup, position : Int){
        val data: //채팅창
        holder.bind(chat_data)
    }
    */


        //상대방의 이미지를 띄움, 현재는 나의 이미지
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=실패")
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=세션 닫힘")
            }

            override fun onSuccess(result: MeV2Response?) {
                if (result != null) {
                    val picture_location =
                        result.kakaoAccount.profile.profileImageUrl ?: "" //프로필 이미지가 없으면 null이 들어감
                    val name = result.kakaoAccount.profile.nickname
                    if (picture_location != "") Glide.with(this@ChatActivity).load(
                        picture_location).transform(RoundedCorners(20)).into(activity_partner_image)
                    else Glide.with(this@ChatActivity).load(R.drawable.blank_profile).transform(RoundedCorners(20)
                    ).into(activity_partner_image)
                    activity_partner_name.text = name
                }
            }
        })
    }




    //메세지를 보낼 때 db에 저장할 요소들 = 시간, 보낸 메세지
    //매칭이 되면 이름과 이메일을 통하여 db에 저장


    var myname : String = "cho"
    var yourname : String = "gwon"

    private fun sending(){
        val chat_req = input_message.text.toString()        //요청자의 메세지 내용
        if(chat_req.isEmpty()){
            Toast.makeText(this, "Please input the meassage", Toast.LENGTH_SHORT).show()
            return
        }

        val message = Chatting_Element(chat_req)
        val database = FirebaseDatabase.getInstance()

        val ref = database.getReference("CHAT").child("$myname-$yourname" )
        val ref_op = database.getReference("CHAT").child("$yourname-$myname")

        ref.setValue(message)
            .addOnSuccessListener {
                Log.d( "message test", "Save the message")
                input_message.text.clear()
            }
        ref_op.setValue(message)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("CHAT").child("$myname-$yourname")
        latestMessageRef.setValue(message)

        val latestMessageOpRef = FirebaseDatabase.getInstance().getReference("CHAT").child("$yourname-$myname")
        latestMessageOpRef.setValue(message)

    }
}

class Message_to_giving(val from_message : String, val user : String): ClipData.Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position : Int){

        viewHolder.itemView.chat_mine.text = from_message
    }

    override fun getLayout(): Int {
        return R.layout.material_chat_mine
    }
}

class Message_to_requesting(val to_message : String, val user : String): ClipData.Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position : Int){

        viewHolder.itemView.chat_partner.text = to_message
    }

    override fun getLayout(): Int {
        return R.layout.material_chat_yours
    }
}

