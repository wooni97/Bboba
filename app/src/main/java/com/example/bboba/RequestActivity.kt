package com.example.bboba

<<<<<<< HEAD
import android.app.DatePickerDialog
import android.app.TimePickerDialog
=======
import android.accounts.AccountManager.get
>>>>>>> 0e08667bd103ff4da57bffa39b5509347feb7e6b
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
<<<<<<< HEAD
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
=======
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
>>>>>>> 0e08667bd103ff4da57bffa39b5509347feb7e6b
import kotlinx.android.synthetic.main.activity_request.*
import java.text.SimpleDateFormat
import java.util.*

<<<<<<< HEAD
=======

/*
fun main(args: Array<String>){
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
    val formatted = current.format(formatter)

    println("Current: $formatted")
}
*/

>>>>>>> 0e08667bd103ff4da57bffa39b5509347feb7e6b
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
    var total_page: String? = null
    var detail_request: String? = null
    var date: String? = null
    var time: String? = null
    var locationx: String? = null
    var locationy: String? = null
    var location_name: String = "한국항공대학교"
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

<<<<<<< HEAD
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
                    val dt_date: Date = format.parse(str_date)!! //널이 들어갈 수 없다
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
=======
        progressView = this.progress
        seekbarView = this.seekBar
        seekbarView!!.setOnSeekBarChangeListener(this)

        Done.setOnClickListener {
            val tz = TimeZone.getTimeZone("Asia/Seoul")
            val gc = GregorianCalendar(tz)
            var year = gc.get(GregorianCalendar.YEAR).toString()
            var month = (gc.get(GregorianCalendar.MONTH) + 1).toString()
            var day = gc.get(GregorianCalendar.DATE).toString()
            var hour = gc.get(GregorianCalendar.HOUR).toString()
            var min = gc.get(GregorianCalendar.MINUTE).toString()
            var sec = gc.get(GregorianCalendar.SECOND).toString()

            val num_page: String = Page_input.text.toString()
            val time: String? = Time_input.text.toString()
            val location: String? = Location_input.text.toString()

            val layout_num_page = findViewById(R.id.Page_input) as EditText
            val state_both_side = findViewById<CheckBox>(R.id.both_side)

            val name = "$hour 시"
            val tel = "$min 분"
            val d_req = "$state_both_side"
            //val check = "$state_both_side"
            val page ="$layout_num_page"
            val user = RequestList(name, page, tel, d_req)
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("prints_request").child("test2")
            myRef.setValue(user)


            //값이 하나라도 null이라면 toast를 띄우고 싶음
            //아직 안 null
            //log에는 공백으로 찍히는데 이게 null이라는 값을 가지는건지 모르겠음
            //null이 정말 null을 의미하는가
            if (num_page.equals(null) || time.equals(null) || location.equals(null)) {
                Toast.makeText(this, "상세 조건을 기입하세요", Toast.LENGTH_SHORT).show()
            } else {
                val nextintent = Intent(this, ComRequestActivity::class.java)
                //val time: LocalDateTime = LocalDateTime.now()
                //time을 불러와도 시간을 표시해주지만 now에서 빨간줄, error는 아니지만 warning이뜸

>>>>>>> 0e08667bd103ff4da57bffa39b5509347feb7e6b

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

<<<<<<< HEAD
        request_button.setOnClickListener { //요청하기 버튼 클릭
            total_page = findViewById<EditText>(R.id.edit_total).text.toString()
            detail_request = findViewById<EditText>(R.id.edit_request).text.toString()
            print_fb = findViewById<CheckBox>(R.id.print_fb).isChecked.toString()
            color_print = findViewById<CheckBox>(R.id.color_print).isChecked.toString()
            if(total_page==null || detail_request==null || date==null ||  time==null || locationx==null){ //빈 칸이 있으면 멈춘다
                Toast.makeText(this, "내용을 모두 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
=======
                Log.d("Time","현재 시간은 " + year + " 년 " + month + " 월 " + day + " 일 " + hour + " 시 " + min + " 분 " + sec + " 초 ")
                Log.d("Checkbox of both side","checkbox is" + state_both_side)
>>>>>>> 0e08667bd103ff4da57bffa39b5509347feb7e6b
            }
            val pr = Prints_Request(name, userEmail, total_page!!, detail_request!!, date!!, time!!, locationx!!, locationy!!, location_name, div_page, print_fb, color_print, picture_location)
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