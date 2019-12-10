package com.example.bboba

import android.content.ClipData
import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
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
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    val context=this

    //email을 크기를 비교하여 db에 항상 일관되게 저장

    private val chatData = ArrayList<Chatting_Element>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //키보드 올라오면 화면 조정
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)



        //상대방의 이미지를 띄움, 현재는 나의 이미지
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response) {
                //파이어베이스 데이터 받기
                val myemail_address = result.kakaoAccount.email
                val myId = myemail_address.substring(0,myemail_address.indexOf('@'))
                val yourId : String = intent.getStringExtra("op_chatId")!!
                //val myname = result.kakaoAccount.profile.nickname
                val yourname = intent.getStringExtra("op_chatName")
                val requestProfileLink: String? = intent.getStringExtra("requestProfileLink")//널 가능(프로필 사진이 없으면 널이 들어간다. 후에 널이면 빈 사진을 띄운다)

                val database = FirebaseDatabase.getInstance()
                val chatRef = database.getReference("CHAT")
                val pathString = if(myId.compareTo(yourId)>0){ //아이디 비교를 통해 하나의 경로로 설정
                    myId+"-"+yourId
                } else {
                    yourId+"-"+myId
                }
                val userRef = chatRef.child(pathString)


                activity_partner_name.text = yourname
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
                        chatData.clear()
                        for (hashData in p0.children) {
                            chatData.add(Chatting_Element(hashData.child("chat").value.toString(), hashData.child("username").value.toString()))
                        }
                        chatting_recyclerview.apply {
                            layoutManager = LinearLayoutManager(context?:return)
                            adapter = MyChatAdapter(chatData, requestProfileLink)
                        }
                        chatting_recyclerview.scrollToPosition(MyChatAdapter(chatData,requestProfileLink).itemCount-1)
                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                }
        })
    }
}

