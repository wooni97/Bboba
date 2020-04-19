package com.example.bboba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.UserManagement
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    val context=this

    private val chatData = ArrayList<Chatting_Element>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //키보드 올라오면 화면 조정
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        //상대방의 이미지,이름,email,id를 카카오톡으로 받아옴
        //api에서 정보를 받는 부분이 느리기 때문에 api코드 안쪽에 이후의 코드들 작성
        UserManagement.getInstance().me(object : MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response) {
                //파이어베이스 데이터 받기
                val myEmailAddress: String = result.kakaoAccount.email
                val myId: String = myEmailAddress.substring(0,myEmailAddress.indexOf('@')) //이메일에서 아이디 추출
                val yourId : String = intent.getStringExtra("op_chatId")!! //객체가 존재한 상태에서 넘기기 때문에 항상 null이 아니다
                //val myname = result.kakaoAccount.profile.nickname
                val yourname: String = intent.getStringExtra("op_chatName")!! //객체가 존재한 상태에서 넘기기 때문에 항상 null이 아니다
                val requestProfileLink: String = intent.getStringExtra("requestProfileLink") ?: "" //프로필 사진이 없으면 널이 들어온다. 널이면 자동으로 ""를 넣는다

                val database = FirebaseDatabase.getInstance()
                val chatRef = database.getReference("CHAT")
                val pathString = if(myId.compareTo(yourId)>0){ //아이디 크기 비교를 통해 DB 접근시 하나의 경로로 설정(2명간의 단일 경로 설정)
                    myId+"-"+yourId
                } else {
                    yourId+"-"+myId
                }
                val userRef = chatRef.child(pathString) //두 사람간의 채팅 내역이 들어있는 db

                activity_partner_name.text = yourname //상대방의 이름을 위쪽에 표시한다
                activity_send.setOnClickListener{
                    val input_chat = input_message.text.toString()
                    if(input_chat.isEmpty()){ //채팅창에 아무것도 입력하지 않고 전송을 누르면 알림을 띄우고 리스너를 종료한다
                        Toast.makeText(context, "Please input the meassage", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val userRealId = result.kakaoAccount.email.substring(0,result.kakaoAccount.email.indexOf('@'))//아이디 추출
                    val Data = Chatting_Element(input_chat,userRealId) //db에 발신자와 내용을 저장한다
                    userRef.push().setValue(Data)
                        .addOnSuccessListener {
                            input_message.text.clear() //전송이 성공한다면 edit text칸을 지운다
                        }
                }

                userRef.addValueEventListener(object: ValueEventListener { //채팅 내역을 화면에 띄워준다
                    override fun onDataChange(p0: DataSnapshot) {
                        chatData.clear() //같은 채팅의 내역이 쌓이지 않도록 clear해준다
                        for (hashData in p0.children) {
                            chatData.add(Chatting_Element(hashData.child("chat").value.toString(), hashData.child("username").value.toString()))
                        }
                        chatting_recyclerview.apply {       //채팅말풍선의 layout과 adapting해준다
                            layoutManager = LinearLayoutManager(context?:return)
                            adapter = MyChatAdapter(chatData, requestProfileLink) //어댑터에 채팅데이터들과 요청자의 프로필 사진 링크를 보낸다(뒤에서 요청자의 프로필 사진을 받을 방법이 없어서 넘겨준다)
                        }
                        chatting_recyclerview.scrollToPosition(MyChatAdapter(chatData,requestProfileLink).itemCount-1) //채팅을 보내면 가장 밑으로 스크롤한다
                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                }
        })
    }
}

