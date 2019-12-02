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

<<<<<<< HEAD
    val myname : String = "uiop527"
    val yourname : String = "thkthk97"
=======
    val myemail : String = "uiop527"
    val youremail : String = "thkthk97"
>>>>>>> 8d0729019629ee081c05f387f62883d795618b96

    private val chatData = ArrayList<Chatting_Element>()
    private val database = FirebaseDatabase.getInstance()
    private val chatRef = database.getReference("CHAT")
<<<<<<< HEAD
    private val userRef = chatRef.child("$myname-$yourname")
=======
    private val userRef = chatRef.child("$myemail-$youremail")
    private val toBottomofview = LinearLayoutManager(this)
>>>>>>> 8d0729019629ee081c05f387f62883d795618b96

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
<<<<<<< HEAD

        val context = this
=======
        
        //채팅창에 있는 대화를 가장 최신으로 업데이트하기위함
        toBottomofview.setStackFromEnd(true)
        chatting_recyclerview.setLayoutManager(toBottomofview)
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val chat_userid: String? = "thkthk97"                //말한 사람의 id


        activity_send.setOnClickListener{
            val input_chat = input_message.text.toString()

            if(input_chat.isEmpty()){
                Toast.makeText(this,"Please input the meassage", Toast.LENGTH_SHORT).show()
            }

            val Data = Chatting_Element(input_chat,chat_userid)

            userRef.push().setValue(Data)
                .addOnSuccessListener {
                    input_message.text.clear()      //전송이 성공한다면 edit text칸을 지운다

                    toBottomofview.setStackFromEnd(true)        //채팅창에 있는 대화를 가장 최신으로 계속 갱신
                    chatting_recyclerview.setLayoutManager(toBottomofview)
                }
        }
>>>>>>> 8d0729019629ee081c05f387f62883d795618b96


        //상대방의 이미지를 띄움, 현재는 나의 이미지
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response) {
                //파이어베이스 데이터 받기

                activity_send.setOnClickListener{

                    val input_chat = input_message.text.toString()
                    if(input_chat.isEmpty()){
                        Toast.makeText(context, "Please input the meassage", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val userRealId = result.kakaoAccount.email.substring(0,result.kakaoAccount.email.indexOf('@'))//아이디 추출
                    val Data = Chatting_Element(input_chat,userRealId)
                    userRef.push().setValue(Data)
                        .addOnSuccessListener {
                            input_message.text.clear()      //전송이 성공한다면 edit text칸을 지운다
                        }

                }

                userRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
<<<<<<< HEAD
                        chatData.clear()
                        for (hashData in p0.children) {
                            chatData.add(Chatting_Element(hashData.child("chat").value.toString(), hashData.child("username").value.toString()))
                        }
                        chatting_recyclerview.apply {
                            layoutManager = LinearLayoutManager(context?:return)
                            adapter = MyChatAdapter(chatData)
=======
                        if (result != null) {
                            val useremail = result.kakaoAccount.email
                            val userid = useremail.substring(0,useremail.indexOf('@'))     //현재 어플을 실행한 사람

                            chatData.clear()
                            //Log.d("test", "reading data is succeeded")
                            for (k in p0.children) {
                                //Log.d("test", "reading data ${k.child("chat_my").value as String}")
                                chatData.add(
                                    Chatting_Element(
                                        k.child("chat").value as String,
                                        k.child("username").value as String
                                    )
                                )
                            }
                            chatting_recyclerview.apply {
                                if (chat_userid == userid) {            //내가 실행하니까 userid = uiop527
                                    layoutManager = LinearLayoutManager(context ?: return)
                                    adapter = MyChatAdapter(chatData)
                                }else{
                                    layoutManager = LinearLayoutManager(context?: return)
                                    adapter = YourChatAdapter(chatData)
                                }
                            }
>>>>>>> 8d0729019629ee081c05f387f62883d795618b96
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

