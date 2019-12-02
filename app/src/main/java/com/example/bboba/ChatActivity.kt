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
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    val myemail : String = "uiop527"
    val youremail : String = "thkthk97"
    val myname = "영환"
    val yourname = "태형"

    private val chatData = ArrayList<Chatting_Element>()
    private val database = FirebaseDatabase.getInstance()
    private val chatRef = database.getReference("CHAT")

    private val userRef = chatRef.child("$myemail-$youremail")
    private val toBottomofview = LinearLayoutManager(this)


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val context = this

        //상대방의 이미지를 띄움, 현재는 나의 이미지
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response) {
                //파이어베이스 데이터 받기
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
                            adapter = MyChatAdapter(chatData)
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                }
        })
    }
}

