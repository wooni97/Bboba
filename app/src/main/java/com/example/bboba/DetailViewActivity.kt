package com.example.bboba

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_detail_view.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*

class DetailViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)
        val context = this
        val fragmentNumber = intent.getIntExtra("fragmentNumber", 1) // 맵에서 넘어온 것이면 맵을, 리스트에서 넘어온 것이면 리스트를 띄우기 위해서 만듦
        //각 뷰들에 대한 데이터 채워넣기
        val requestData = intent.getParcelableExtra<Prints_Request>("request_data")
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
        if(requestData.picture_location!="") Glide.with(context).load(requestData.picture_location).transform(RoundedCorners(20)).into(detail_req_profile)
        else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(detail_req_profile)
        if(requestData.print_fb == "true") {
            detail_print_fb.isChecked = true
        }
        if(requestData.print_color == "true") {
            detail_color_print.isChecked = true
        }
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

        detail_spinner_location.setOnClickListener {//장소를 클릭하면 장소에 대한 지도가 나온다
            val dialogFragment = LocationViewrDialog(this, requestData.locationx.toDouble(), requestData.locationy.toDouble())
            val fragmentManager = supportFragmentManager
            dialogFragment.show(fragmentManager, null)
        }

        detail_request_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("매칭 선택")
                .setMessage("이 요청글과 매칭하시겠습니까?")
                .setPositiveButton("선택하기", DialogInterface.OnClickListener { dialog, id ->
                    val database = FirebaseDatabase.getInstance()
                    val ref = database.getReference("PRINTS_REQUEST")
                    val dateRef = ref.child("date")
                    val idRef = ref.child("id")
                    dateRef.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(eachDateData: DataSnapshot) {
                            val oneDateData = eachDateData.child(requestData.date)
                            for(data in oneDateData.children){
                                if(data.child("email").value==requestData.email && data.child("detail_request").value == requestData.detail_request
                                    && data.child("per_page").value == requestData.per_page && data.child("print_fb").value == requestData.print_fb
                                    && data.child("print_color").value == requestData.print_color && data.child("is_selected").value!=1) { //요청글 찾음
                                    val childUpdates = HashMap<String, Any>()
                                    childUpdates["/is_selected"] = "1" //매칭 되었음
                                    //매칭자 정보 추가하기
                                    data.ref.updateChildren(childUpdates)
                                    break
                                }
                            }
                            detail_request_button.text = "매칭중"
                            detail_request_button.isEnabled = false
                            Toast.makeText(this@DetailViewActivity, "10초 안에 인증을 완료해주세요", Toast.LENGTH_SHORT).show()

                        }
                    })
                    val handler = Handler()
                    handler.postDelayed(object: Runnable {
                        override fun run() {
                            dateRef.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(eachDateData: DataSnapshot) {
                                    val oneDateData = eachDateData.child(requestData.date)
                                    for(data in oneDateData.children){
                                        if(data.child("email").value==requestData.email && data.child("detail_request").value == requestData.detail_request
                                            && data.child("per_page").value == requestData.per_page && data.child("print_fb").value == requestData.print_fb
                                            && data.child("print_color").value == requestData.print_color && data.child("is_selected").value!=1) { //요청글 찾음
                                            val childUpdates = HashMap<String, Any?>()
                                            childUpdates["/is_selected"] = null //매칭 풀림. null집어넣으면 db에서 사라진다
                                            //매칭자 정보 추가하기
                                            data.ref.updateChildren(childUpdates)
                                            break
                                        }
                                    }
                                    detail_request_button.text = "매칭하기"
                                    detail_request_button.isEnabled = true
                                    Toast.makeText(this@DetailViewActivity, "인증시간이 초과되어서 매칭이 해제되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            })
                        }
                    }, 10000)//10초  (디버깅 편하게 일단 10초로 설정)
                    //val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    //화면전환
                    val nextIntent = Intent(context, GiveMainActivity::class.java)
                    nextIntent.putExtra("fragmentNumber", fragmentNumber)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 뒤로가기 버튼 눌렀을때 액티비티 스택에 쌓여있는 전의 화면을 불러오는데 이를 없애서 뒤로가기를 계속 눌렀을 때 중복 화면을 없앤다.
                    startActivity(nextIntent)
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
                })
            builder.create()
            builder.show()
        }
    }
}