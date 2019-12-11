package com.example.bboba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_com_request.*

class ComRequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_com_request)

        To_main.setOnClickListener{
            val nextintent = Intent(this,MainActivity::class.java)
            startActivity(nextintent)
        }
    }
    override fun onBackPressed() {  //사용자가 뒤로가기를 눌렀을 때 다시 요청글을 쓰는 창이 아닌 메인페이지로 이동함
        Toast.makeText(this,"메인 페이지로 이동합니다.",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
