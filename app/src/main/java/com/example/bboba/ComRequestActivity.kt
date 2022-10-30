package com.example.bboba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast

class ComRequestActivity : AppCompatActivity() {
//글이 정상적으로 등록됐다는 것을 알려주는 페이지
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_com_request)
        val context = this
        Handler().postDelayed(object: Runnable{ //0.5초 후 메인페이지로 이동한다
            override fun run() {
                val nextintent = Intent(context,MainActivity::class.java)
                startActivity(nextintent)
            }
        }, 800)
    }
    override fun onBackPressed() {  //사용자가 뒤로가기를 눌렀을 때 다시 요청글을 쓰는 창이 아닌 메인페이지로 이동함
        Toast.makeText(this,"메인 페이지로 이동합니다.",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
