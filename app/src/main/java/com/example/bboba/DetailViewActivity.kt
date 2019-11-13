package com.example.bboba

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_detail_view.*
import java.util.*

class DetailViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)
        val context = this
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
        detail_profile_name.text = requestData.name
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

        request_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("매칭 선택")
                .setMessage("이 요청글과 매칭하시겠습니까?")
                .setPositiveButton("매칭하기", DialogInterface.OnClickListener { dialog, id ->
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
                })
            builder.create()
            builder.show()
        }
    }
}