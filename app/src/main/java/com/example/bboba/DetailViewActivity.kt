package com.example.bboba

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail_req.*
import java.lang.Exception
import java.util.*

class DetailViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_req)

        //각 뷰들에 대한 데이터 채워넣기
        val request_data = intent.getParcelableExtra<Prints_Request>("request_data")
        val calendar = Calendar.getInstance()
        val date = request_data.date
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
        profile_name.text = request_data.name
        profile_id.text = request_data.id
        edit_total.text = request_data.total_page
        edit_request.text = request_data.detail_request
        edit_date.text = "${request_data.date} ($day_name)"
        edit_time.text = request_data.time
        spinner_location.text = request_data.location_name
        if(request_data.print_fb == "true") {
            print_fb.isChecked = true
        }
        if(request_data.print_color == "true") {
            color_print.isChecked = true
        }
    }
}