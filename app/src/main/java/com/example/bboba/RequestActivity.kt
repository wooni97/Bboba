package com.example.bboba

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_request.*
import java.text.SimpleDateFormat
import java.util.*

class RequestActivity : AppCompatActivity(),SeekBar.OnSeekBarChangeListener {
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
    lateinit var id: String
    lateinit var total_page: String
    lateinit var detail_request: String
    lateinit var date: String
    lateinit var time: String
    lateinit var locationx: String
    lateinit var locationy: String
    lateinit var location_name: String
    var div_page: String = "1"
    lateinit var print_fb: String
    lateinit var color_print: String
    var picture_location: String = ""//"고치기" 추후 입력

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        seekbarView.setOnSeekBarChangeListener(this)

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
                    time = "$hourOfDay:$minute"
                    return
                }
            }
            val builder = TimePickerDialog(this, timeListener, hour, minute, false)
            builder.show()
        }

        //spinner(장소선택)에 대한 어댑터 선언
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        spinner_location.adapter = spinnerAdapter
        spinner_location.prompt = "수령 장소 선택"
        spinner_location.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> { //과학관
                        locationx = "37.601536"
                        locationy = "126.865027"
                        location_name = "과학관"
                    }
                    1 -> { //전자관
                        locationx = "37.6005286"
                        locationy = "126.862713"
                        location_name = "전자관"
                    }
                    2 -> { //기계관
                        locationx = "37.601341"
                        locationy = "126.864493"
                        location_name = "기계관"
                    }
                    3 -> { //강의동
                        locationx = "37.5997893"
                        locationy = "126.8633706"
                        location_name = "강의동"
                    }
                    4 -> { //학생회관
                        locationx = "37.6001817"
                        locationy = "126.8659652"
                        location_name = "학생회관"
                    }
                }
            }
        }



        request_button.setOnClickListener { //요청하기 버튼 클릭
            name = findViewById<TextView>(R.id.profile_name).text.toString()
            id = findViewById<TextView>(R.id.profile_id).text.toString()
            total_page = findViewById<EditText>(R.id.edit_total).text.toString()
            detail_request = findViewById<EditText>(R.id.edit_request).text.toString()
            time = findViewById<TextView>(R.id.edit_time).text.toString()
            print_fb = findViewById<CheckBox>(R.id.print_fb).isChecked.toString()
            color_print = findViewById<CheckBox>(R.id.color_print).isChecked.toString()
            val pr = Prints_Request(name, id, total_page, detail_request, date, time, locationx, locationy, location_name, div_page, print_fb, color_print, picture_location)
            //Firebase 데이터 삽입
            //Firebase 변수
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("PRINTS_REQUEST")
            myRef.push().setValue(pr)
            val nextIntent = Intent(this, ComRequestActivity::class.java)
            startActivity(nextIntent)
        }
    }
}