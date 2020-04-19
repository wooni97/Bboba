package com.example.bboba

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.database.FirebaseDatabase
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_request.*
import java.text.SimpleDateFormat
import java.util.*

class RequestActivity : AppCompatActivity(),SeekBar.OnSeekBarChangeListener {
    val context = this

    //변수 선언
    lateinit var name: String
    lateinit var totalPage: String
    lateinit var detailRequest: String
    //lateinit을 안 쓰고 var 변수명 = null 을 쓰는 이유 : 입력을 받지 않고 글쓰기를 할 때 null여부를 판단하여 처리한다
    //lateinit을 쓰면 초기화 하지 않았다는 에러가 발생한다
    var date: String? = null
    var time: String? = null
    var locationx: String? = null
    var locationy: String? = null
    lateinit var location_name: String
    var div_page: String = "1" //모아찍기
    lateinit var print_fb: String //양면인쇄
    lateinit var color_print: String //컬러인쇄
    lateinit var userEmail: String //유저 카카오 계정 이메일
    lateinit var userRealId: String //이메일에서 아이디 추출
    var picture_location: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        seekbarView.setOnSeekBarChangeListener(this)

        //카카오 api에서 정보 받아오기
        UserManagement.getInstance().me(object: MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
            }
            override fun onSuccess(result: MeV2Response?) {
                if(result!=null) {
                    picture_location = result.kakaoAccount.profile.profileImageUrl?:"" //프로필 이미지가 없으면 null이 들어감
                    name = result.kakaoAccount.profile.nickname
                    userEmail = result.kakaoAccount.email?:""
                    if(picture_location!="") Glide.with(context).load(picture_location).transform(RoundedCorners(20)).into(request_profile)
                    else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(request_profile)
                    req_profile_name.text = name
                    req_profile_email.text = userEmail
                }
            }
        })

        //수령시간 선택
        //날짜
        edit_date.setOnClickListener { view ->
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            var dayName = "일"
            val format = SimpleDateFormat("yyyy-MM-dd")

            val dateListener = object: DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    val strDate = "$year-${month+1}-$dayOfMonth"
                    val dtDate: Date = format.parse(strDate)!! //널이 들어갈 수 없다
                    val stDate = format.format(dtDate)
                    calendar.set(year, month, dayOfMonth)
                    val dayNum = calendar.get(Calendar.DAY_OF_WEEK)
                    dayName = when(dayNum) {
                        1->"일"
                        2->"월"
                        3->"화"
                        4->"수"
                        5->"목"
                        6->"금"
                        else->"토"
                    }
                    edit_date.text = "$stDate ($dayName)"
                    date = stDate
                    return
                }
            }
            val builder = DatePickerDialog(this, dateListener, year, month, day)
            builder.show()
        }
        //시간
        edit_time.setOnClickListener { view->
            val nowtime = Calendar.getInstance()
            val hour = nowtime.get(Calendar.HOUR)
            val minute = nowtime.get(Calendar.MINUTE)

            val timeListener = object: TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    edit_time.text = "$hourOfDay 시 $minute 분"
                    time = edit_time.text.toString()
                    return
                }
            }
            val builder = TimePickerDialog(this, timeListener, hour, minute, false)
            builder.show()
        }
        location_select.setOnClickListener {//지도 선택 버튼 클릭
            location_select.text = "한국항공대학교"
            val dialogFragment = LocationPickerDialog(context)
            val fragmentManager = supportFragmentManager
            dialogFragment.show(fragmentManager, null)
        }

        request_button.setOnClickListener { //요청하기 버튼 클릭
            totalPage = findViewById<EditText>(R.id.edit_total).text.toString()
            detailRequest = findViewById<EditText>(R.id.edit_request).text.toString()
            print_fb = findViewById<CheckBox>(R.id.print_fb).isChecked.toString()
            color_print = findViewById<CheckBox>(R.id.color_print).isChecked.toString()
            if(total_page==null || detail_request==null || date==null ||  time==null || locationx==null){ //빈 칸이 있으면 멈춘다
                Toast.makeText(this, "내용을 모두 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            location_name = findViewById<TextView>(R.id.location_select).text.toString()
            val pr = Prints_Request(name, userEmail, totalPage, detailRequest, date!!, time!!, locationx!!, locationy!!, location_name, div_page, print_fb, color_print, picture_location)
            //Firebase 데이터 삽입
            //Firebase 변수
            val database = FirebaseDatabase.getInstance()
            userRealId = userEmail.substring(0,userEmail.indexOf('@'))//아이디 추출
            val myRef = database.getReference("PRINTS_REQUEST").child("id").child("$userRealId")//유저정보로 저장
            val dateRef = database.getReference("PRINTS_REQUEST").child("date").child("$date")//날짜정보로 저장
            myRef.push().setValue(pr)
            dateRef.push().setValue(pr)
            val nextIntent = Intent(this, ComRequestActivity::class.java)
            startActivity(nextIntent)
        }
    }

    //Seekbar 관련 함수
    override fun onProgressChanged(seekBar: SeekBar, progress: Int,fromUser: Boolean) {
        val perPage = progress+1
        this.div_page = perPage.toString()
        progressView.text = "$perPage 장 "
    }
    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }
    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
    val progressView: TextView by lazy{
        this.per_page
    }
    val seekbarView: SeekBar by lazy{
        this.page_seekbar
    }
}