package com.example.bboba

import android.content.ClipData
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.database.*
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.UserManagement
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.material_chat_mine.view.*
import kotlinx.android.synthetic.main.material_chat_yours.view.*


class ChatActivity : AppCompatActivity() {

    val myname : String = "cho"
    val yourname : String = "gwon"

    private val chatData = ArrayList<Chatting_Element>()
    private val database = FirebaseDatabase.getInstance()
    private val chatRef = database.getReference("CHAT")
    private val userRef = chatRef.child("$myname-$yourname")
    private val op_userRef = chatRef.child("$yourname-$myname")
    private val toBottomofview = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        
        //채팅창에 있는 대화를 가장 최신으로 업데이트하기위함
        toBottomofview.setStackFromEnd(true)
        chatting_recyclerview.setLayoutManager(toBottomofview)
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        activity_send.setOnClickListener{

            val input_chat = input_message.text.toString()
            if(input_chat.isEmpty()){
                Toast.makeText(this,"Please input the meassage", Toast.LENGTH_SHORT).show()
            }
            val Data = Chatting_Element(input_chat)
            userRef.push().setValue(Data)
                .addOnSuccessListener {
                    input_message.text.clear()      //전송이 성공한다면 edit text칸을 지운다

                    toBottomofview.setStackFromEnd(true)        //채팅창에 있는 대화를 가장 최신으로 계속 갱신
                    chatting_recyclerview.setLayoutManager(toBottomofview)
                }
            op_userRef.push().setValue(Data)

        }






        //상대방의 이미지를 띄움, 현재는 나의 이미지
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Log.d("example", "test=실패")
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("example", "test=세션 닫힘")
            }

            override fun onSuccess(result: MeV2Response?) {
                //파이어베이스 데이터 받기

                userRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        chatData.clear()
                        //Log.d("test", "reading data is succeeded")
                        for (k in p0.children) {
                            //Log.d("test", "reading data ${k.child("chat_my").value as String}")
                            chatData.add(Chatting_Element(k.child("chat_my").value as String))
                        }
                        chatting_recyclerview.apply {
                            layoutManager = LinearLayoutManager(context?:return)
                            adapter = MyChatAdapter(chatData)
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("test","reading data is failed")
                    }
                })


                if (result != null) {
                    val picture_location =
                        result.kakaoAccount.profile.profileImageUrl ?: "" //프로필 이미지가 없으면 null이 들어감
                    val name = result.kakaoAccount.profile.nickname
                    if (picture_location != "") Glide.with(this@ChatActivity).load(
                        picture_location
                    ).transform(RoundedCorners(20)).into(activity_partner_image)
                    else Glide.with(this@ChatActivity).load(R.drawable.blank_profile).transform(
                        RoundedCorners(20)
                    ).into(activity_partner_image)
                    activity_partner_name.text = name
                }
            }
        })
    }
}

