package com.example.bboba

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_detail_view.*
import java.util.*

class DetailViewActivity: AppCompatActivity() {
    var listenerNum = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)

        val context = this
        val fragmentNumber = intent.getIntExtra("fragmentNumber", 1) // 맵에서 넘어온 것이면 맵을, 리스트에서 넘어온 것이면 리스트를 띄우기 위해서 만듦

        //각 뷰들에 대한 데이터 채워넣기
        val requestData: Prints_Request = intent.getParcelableExtra<Prints_Request>("request_data")!! //널 값이 들어오지 않는다(값이 있어야 이 화면으로 넘어올 수 있다)
        var op_name = requestData.name //채팅 상대방 이름
        var op_id = requestData.email.substring(0, requestData.email.indexOf('@'))//채팅 상대방 id
        val calendar = Calendar.getInstance()
        val date = requestData.date
        calendar.set(date.substring(0,4).toInt(), date.substring(5,7).toInt(),date.substring(8,10).toInt()) //DB의 date에 저장된 문자열을 쪼개서 달력 날짜 설정(요일을 구하기 위해)
        val day_num = calendar.get(Calendar.DAY_OF_WEEK)
        val day_name = when(day_num) {
            1->"일"
            2->"월"
            3->"화"
            4->"수"
            5->"목"
            6->"금"
            else->"토"
        }
        detail_profile_name.text = requestData!!.name
        detail_user_email.text = requestData.email
        detail_edit_total.text = requestData.total_page
        detail_edit_request.text = requestData.detail_request
        detail_edit_date.text = "${requestData.date} ($day_name)"
        detail_edit_time.text = requestData.time
        detail_spinner_location.text = requestData.location_name
        //사진 채우기
        if(requestData.picture_location!="") Glide.with(context).load(requestData.picture_location).transform(RoundedCorners(20)).into(detail_req_profile)
        else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(detail_req_profile)
        //체크박스
        if(requestData.print_fb == "true") {
            detail_print_fb.isChecked = true
        }
        if(requestData.print_color == "true") {
            detail_color_print.isChecked = true
        }
        //여러장 찍기
        detail_spinner_location.setOnClickListener {//장소를 클릭하면 장소에 대한 지도가 나온다
            val dialogFragment = LocationViewrDialog(this, requestData.locationx.toDouble(), requestData.locationy.toDouble())
            val fragmentManager = supportFragmentManager
            dialogFragment.show(fragmentManager, null)
        }

        UserManagement.getInstance().me(object: MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response) {
                if (requestData.email == result.kakaoAccount.email) {
                    detail_request_button.text = "매칭 대기중"
                    detail_request_button.isEnabled = false //자신의 게시글 && 매칭 전 (매칭 대기중 상태) -> 클릭 불가하게 함
                    to_chat.visibility = View.GONE //자신의 글에 자신이 들어가면 매칭 전이라면 채팅의 필요가 없으므로 버튼을 없앤다
                }
                val database = FirebaseDatabase.getInstance()
                val ref = database.getReference("PRINTS_REQUEST")
                val dateRef = ref.child("date").child(requestData.date)

                dateRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(DateData: DataSnapshot) {
                        for(data in DateData.children) {
                            if(data.child("email").value==requestData.email && data.child("detail_request").value == requestData.detail_request
                                && data.child("per_page").value == requestData.per_page && data.child("print_fb").value == requestData.print_fb
                                && data.child("print_color").value == requestData.print_color && requestData.email == result.kakaoAccount.email && data.child("is_selected").value=="1") {
                                //내 글 && 매칭된 글이면
                                detail_request_button.text = "매칭 취소"
                                detail_request_button.isEnabled = true
                                listenerNum=2 //리스너를 다르게 하기 위해서 설정(1=매칭하기, 2=매칭 취소하기)
                                op_id = data.child("matcher").child("user_email").value as String //채팅 상대방 정보
                                op_name = data.child("matcher").child("user_name").value as String
                            }
                            if(data.child("matcher/user_email").value==result.kakaoAccount.email){ //나의 제공에서 봤을 시(제공자가 글을 들어오면) 매칭 완료라고 뜨게 함
                                detail_request_button.text = "매칭 완료"
                                detail_request_button.isEnabled = false
                                op_id = requestData.email //채팅 상대방 정보
                                op_name = requestData.name
                            }
                        }
                    }
                })

                //리스너 등록을 밖에서 하려고 하면, api들의 받아오는 속도가 느려서 lateinit 변수들의 초기화가 이루어 지지 않음
                //api 정보 받는 부분 안에다 작성
                //챗 인텐트 생성
                val chatIntent = Intent(context, ChatActivity::class.java)
                chatIntent.putExtra("op_chatName", op_name)
                chatIntent.putExtra("op_chatId", op_id)
                chatIntent.putExtra("requestProfileLink", requestData.picture_location)
                //채팅 버튼 리스너 등록
                to_chat.setOnClickListener{
                    startActivity(chatIntent)
                }

                return
            }
        })

        class DetReqClickListener: View.OnClickListener { //상태에 따라 다른 리스너를 받기 위해 클래스로 만듬(익명 클래스에서는 하나의 버튼에 두 개의 리스너를 분기처리 하지 못 함)
            val builder = AlertDialog.Builder(context)
            var matcherEmail = ""
            fun check(data: DataSnapshot):Boolean{ //글이 내가 볼 글과 일치하는지를 판단해주는 함수
                if(data.child("email").value==requestData.email && data.child("detail_request").value == requestData.detail_request
                    && data.child("per_page").value == requestData.per_page && data.child("print_fb").value == requestData.print_fb
                    && data.child("print_color").value == requestData.print_color) return true
                return false
            }
            override fun onClick(v: View) {
                //제공자 입장 시작
                if(listenerNum==1){ //제공자 입장에서 매칭하기를 원할 때
                    builder.setTitle("매칭 선택")
                        .setMessage("이 요청글과 매칭하시겠습니까?")
                        .setPositiveButton("선택하기", DialogInterface.OnClickListener { dialog, id ->
                            val database = FirebaseDatabase.getInstance()
                            val ref = database.getReference("PRINTS_REQUEST")
                            val dateRef = ref.child("date").child(requestData.date)
                            val id = requestData.email.substring(0, requestData.email.indexOf('@'))
                            val idRef = ref.child("id").child(id)
                            val phoneRef = database.getReference("Phone").child(id)
                            //id로 넣기
                            idRef.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(IdData: DataSnapshot) {
                                    //카카오 세션
                                    UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                                        override fun onFailure(errorResult: ErrorResult?) {
                                        }
                                        override fun onSessionClosed(errorResult: ErrorResult?) {
                                        }
                                        override fun onSuccess(result: MeV2Response?) {
                                            if(result!=null){
                                                matcherEmail = result.kakaoAccount.email
                                                for(data in IdData.children) {
                                                    if(check(data) && data.child("is_selected").value!=1) { //요청글 찾음
                                                        val childUpdates = HashMap<String, Any>()
                                                        val matcher_info = HashMap<String, Any>()
                                                        matcher_info["/matcher/user_email"] = matcherEmail
                                                        matcher_info["/matcher/user_name"] = result.kakaoAccount.profile.nickname
                                                        childUpdates["/is_selected"] = "1" //매칭 되었음
                                                        //매칭자 정보 추가하기
                                                        data.ref.updateChildren(childUpdates)
                                                        data.ref.updateChildren(matcher_info)

                                                        //나의 제공에 쓸 데이터 파이어베이스에 저장하기(한 번만 저장하면 되므로 date에서는 안 하고 id에서 함)
                                                        val matRef = database.getReference("Matching_Info")
                                                        val userRealId = matcherEmail.substring(0,matcherEmail.indexOf('@'))//아이디 추출
                                                        val req_Info = Prints_Request(data.child("name").value as String, data.child("email").value as String,data.child("total_page").value as String,
                                                            data.child("detail_request").value as String,data.child("date").value as String,data.child("time").value as String,
                                                            data.child("locationx").value as String,data.child("locationy").value as String,data.child("location_name").value as String,
                                                            data.child("per_page").value as String,data.child("print_fb").value as String,data.child("print_color").value as String,
                                                            data.child("picture_location").value as String)
                                                        val hashRef = matRef.child(userRealId).push()
                                                        hashRef.setValue(req_Info)
                                                        hashRef.updateChildren(childUpdates)
                                                        hashRef.updateChildren(matcher_info)
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    })
                                }
                            })
                            //날짜로 넣기
                            dateRef.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(DateData: DataSnapshot) {
                                    //카카오 세션
                                    UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                                        override fun onFailure(errorResult: ErrorResult?) {
                                        }
                                        override fun onSessionClosed(errorResult: ErrorResult?) {
                                        }
                                        override fun onSuccess(result: MeV2Response?) {
                                            if(result!=null) {
                                                matcherEmail = result.kakaoAccount.email
                                                //카카오 세션정보 받는 시간이 있어서 안에다 써주어야 한다. 아니면 정보를 받지 못한 채로 실행된다
                                                for(data in DateData.children){
                                                    if(check(data) && data.child("is_selected").value!=1) { //요청글 찾음
                                                        val childUpdates = HashMap<String, Any>()
                                                        val matcher_info = HashMap<String, Any?>()
                                                        matcher_info["/matcher/user_email"] = matcherEmail
                                                        matcher_info["/matcher/user_name"] = result.kakaoAccount.profile.nickname
                                                        childUpdates["/is_selected"] = "1" //매칭 되었음
                                                        //매칭자 정보 추가하기
                                                        data.ref.updateChildren(childUpdates)
                                                        data.ref.updateChildren(matcher_info)
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    })
                                    detail_request_button.text = "매칭중"
                                    detail_request_button.isEnabled = false
                                }
                            })

                            //매칭 성공시 문자 보내기
                            phoneRef.addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(p0: DataSnapshot) {
                                    val phoneNumber = p0.child("phone_number").value as String
                                    val message: String = "<뽀바> 매칭이 완료되었습니다."
                                    try {
                                        val smsManager: SmsManager = SmsManager.getDefault()
                                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                                        Toast.makeText(context, "요청자에게 메세지를 전송했습니다.", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "요청자에게의 메세지 전송이 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                            //화면전환
                            val nextIntent = Intent(context, MainActivity::class.java)
                            nextIntent.putExtra("fragmentNumber", fragmentNumber)
                            nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 뒤로가기 버튼 눌렀을때 액티비티 스택에 쌓여있는 전의 화면을 불러오는데 이를 없애서 뒤로가기를 계속 눌렀을 때 중복 화면을 없앤다.
                            startActivity(nextIntent)
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
                        })
                }
                //제공자 입장 끝
                //요청자 입장 시작
                else{ //요청자 입장에서 매칭 취소를 원할 때
                    builder.setTitle("매칭 취소")
                        .setMessage("제공자와의 매칭을 취소하시겠습니까?")
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, id ->
                            val database = FirebaseDatabase.getInstance()
                            val match_ref = database.getReference("Matching_Info")
                            val date_ref = database.getReference("PRINTS_REQUEST").child("date").child(requestData.date)
                            val id_ref = database.getReference("PRINTS_REQUEST").child("id")
                            UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                                override fun onFailure(errorResult: ErrorResult?) {
                                }
                                override fun onSessionClosed(errorResult: ErrorResult?) {
                                }
                                override fun onSuccess(result: MeV2Response?) {
                                    if (result != null) {
                                        //Matching_Info 삭제
                                        match_ref.addListenerForSingleValueEvent(object:ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {
                                            }
                                            override fun onDataChange(idData: DataSnapshot) {
                                                for(oneIdData in idData.children) {
                                                    for(data in oneIdData.children){
                                                        if(check(data)) {
                                                            data.ref.setValue(null)
                                                            return
                                                        }
                                                    }
                                                }
                                            }
                                        })
                                        //date에 있는 매칭 정보
                                        date_ref.addListenerForSingleValueEvent(object:ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {
                                            }
                                            override fun onDataChange(onedayData: DataSnapshot) {
                                                for(data in onedayData.children) {
                                                    if(check(data)) {
                                                        data.child("is_selected").ref.setValue(null)
                                                        data.child("matcher").ref.setValue(null)
                                                    }
                                                }
                                            }
                                        })
                                        val email = result.kakaoAccount.email
                                        val userRealId = email.substring(0,email.indexOf('@'))//아이디 추출
                                        id_ref.child(userRealId).addListenerForSingleValueEvent(object:ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {
                                            }
                                            override fun onDataChange(oneIdData: DataSnapshot) {
                                                for(data in oneIdData.children) {
                                                    if(check(data)) {
                                                        data.child("is_selected").ref.setValue(null)
                                                        data.child("matcher").ref.setValue(null)
                                                    }
                                                }
                                            }
                                        })
                                    }
                                }
                            })
                            //화면전환
                            val nextIntent = Intent(context, MainActivity::class.java)
                            nextIntent.putExtra("fragmentNumber", fragmentNumber)
                            nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 뒤로가기 버튼 눌렀을때 액티비티 스택에 쌓여있는 전의 화면을 불러오는데 이를 없애서 뒤로가기를 계속 눌렀을 때 중복 화면을 없앤다.
                            startActivity(nextIntent)
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
                        })
                }
                builder.create()
                builder.show()
            }
        }
        //매칭하기 버튼 클릭
        val dbtnListener = DetReqClickListener()
        detail_request_button.setOnClickListener(dbtnListener)
    }
}