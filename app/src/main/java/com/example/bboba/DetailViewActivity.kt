package com.example.bboba

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.database.FirebaseDatabase
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_detail_req.*
import kotlinx.android.synthetic.main.activity_detail_req.color_print
import kotlinx.android.synthetic.main.activity_detail_req.edit_date
import kotlinx.android.synthetic.main.activity_detail_req.edit_request
import kotlinx.android.synthetic.main.activity_detail_req.edit_time
import kotlinx.android.synthetic.main.activity_detail_req.edit_total
import kotlinx.android.synthetic.main.activity_detail_req.print_fb
import kotlinx.android.synthetic.main.activity_detail_req.spinner_location
import kotlinx.android.synthetic.main.activity_request.*
import java.util.*

class DetailViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_req)
        val context = this
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
        detail_user_email.text = request_data.email
        edit_total.text = request_data.total_page
        edit_request.text = request_data.detail_request
        edit_date.text = "${request_data.date} ($day_name)"
        edit_time.text = request_data.time
        spinner_location.text = request_data.location_name
        if(request_data.picture_location!="") Glide.with(context).load(request_data.picture_location).transform(RoundedCorners(20)).into(detail_req_profile)
        else Glide.with(context).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(detail_req_profile)
        if(request_data.print_fb == "true") {
            print_fb.isChecked = true
        }
        if(request_data.print_color == "true") {
            color_print.isChecked = true
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
    }
}