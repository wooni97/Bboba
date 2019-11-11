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
import com.google.android.gms.maps.model.LatLng
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
    //Seekbar
    override fun onProgressChanged(seekBar: SeekBar, progress: Int,fromUser: Boolean) {
        val d_page = progress+1
        this.div_page = d_page.toString()
        progressView.text = "$d_page 장 "
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

    //변수
    val locations = arrayOf("과학관", "전자관", "기계관", "강의동", "학생회관")
    lateinit var name: String
    lateinit var total_page: String
    lateinit var detail_request: String
    lateinit var date: String
    lateinit var time: String
    lateinit var locationx: String
    lateinit var locationy: String
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
                Log.d("example", "aaabb=실패")
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=세션 닫힘")
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
        val edit_date = findViewById<TextView>(R.id.edit_date)
        edit_date.setOnClickListener { view ->
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            var day_name = "일"
            val format = SimpleDateFormat("yyyy-MM-dd")

            val date_listener = object: DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    val str_date = "$year-${month+1}-$dayOfMonth"
                    val dt_date = format.parse(str_date)
                    val st_date = format.format(dt_date)
                    calendar.set(year, month, dayOfMonth)
                    val day_num = calendar.get(Calendar.DAY_OF_WEEK)
                    day_name = when(day_num) {
                        1->"일"
                        2->"월"
                        3->"화"
                        4->"수"
                        5->"목"
                        6->"금"
                        else->"토"
                    }
                    edit_date.text = "$st_date ($day_name)"
                    date = st_date
                    return
                }
            }
            val builder = DatePickerDialog(this, date_listener, year, month, day)
            builder.show()
        }
        //시간
        val edit_time = findViewById<TextView>(R.id.edit_time)
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
            val dialogFragment = LocationPickerDialog(context)
            val fragmentManager = supportFragmentManager
            dialogFragment.show(fragmentManager, null)
            location_name="한국항공대학교"
        }

        request_button.setOnClickListener { //요청하기 버튼 클릭
            total_page = findViewById<EditText>(R.id.edit_total).text.toString()
            detail_request = findViewById<EditText>(R.id.edit_request).text.toString()
            print_fb = findViewById<CheckBox>(R.id.print_fb).isChecked.toString()
            color_print = findViewById<CheckBox>(R.id.color_print).isChecked.toString()
            val pr = Prints_Request(name, userEmail, total_page, detail_request, date, time, locationx, locationy, location_name, div_page, print_fb, color_print, picture_location)
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
}