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
    override fun onBackPressed() {
        Toast.makeText(this,"메인 페이지로 이동합니다.",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
