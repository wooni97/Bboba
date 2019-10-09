package com.example.bboba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import kotlinx.android.synthetic.main.activity_request.*

class RequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        Done.setOnClickListener{

            val page:String?= Page_input.text.toString()
            val time: String?= Time_input.text.toString()
            val location: String?= Location_input.text.toString()

            Log.d("RequestActivity", "page is: " + page)
            Log.d("RequestActivity", "time is: " + time)
            Log.d("RequestActivity", "location is: " + location)

            //값이 하나라도 null이라면 toast를 띄우고 싶음
            //아직 안 됨
            //log에는 공백으로 찍히는데 이게 null이라는 값을 가지는건지 모르겠음
            //null이 정말 null을 의미하는가
            if(page == null || time == null || location == null){
                Toast.makeText(this,"상세 조건을 기입하세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val nextintent = Intent(this, ComRequestActivity::class.java)
                startActivity(nextintent)
            }
        }
    }
}
