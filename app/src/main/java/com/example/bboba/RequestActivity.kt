package com.example.bboba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_request.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/*
fun main(args: Array<String>){
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
    val formatted = current.format(formatter)

    println("Current: $formatted")
}
*/

class RequestActivity : AppCompatActivity(),SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int,fromUser: Boolean) {
        progressView!!.text = (progress+1).toString()
    }
    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }
    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    var progressView: TextView? = null
    var seekbarView: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)


        progressView = this.progress
        seekbarView = this.seekBar
        seekbarView!!.setOnSeekBarChangeListener(this)

        Done.setOnClickListener {

            val page: String? = Page_input.text.toString()
            val time: String? = Time_input.text.toString()
            val location: String? = Location_input.text.toString()

            Log.d("RequestActivity", "page is: " + page)
            Log.d("RequestActivity", "time is: " + time)
            Log.d("RequestActivity", "location is: " + location)

            //값이 하나라도 null이라면 toast를 띄우고 싶음
            //아직 안 null
            //log에는 공백으로 찍히는데 이게 null이라는 값을 가지는건지 모르겠음
            //null이 정말 null을 의미하는가
            if (page.equals(null) || time.equals(null) || location.equals(null)) {
                Toast.makeText(this, "상세 조건을 기입하세요", Toast.LENGTH_SHORT).show()
            } else {
                val nextintent = Intent(this, ComRequestActivity::class.java)
                //val time: LocalDateTime = LocalDateTime.now()
                //time을 불러와도 시간을 표시해주지만 now에서 빨간줄, error는 아니지만 warning이뜸

                val tz = TimeZone.getTimeZone("Asia/Seoul")
                val gc = GregorianCalendar(tz)
                var year = gc.get(GregorianCalendar.YEAR).toString()
                var month = (gc.get(GregorianCalendar.MONTH) + 1).toString()
                var day = gc.get(GregorianCalendar.DATE).toString()
                var hour = gc.get(GregorianCalendar.HOUR).toString()
                var min = gc.get(GregorianCalendar.MINUTE).toString()
                var sec = gc.get(GregorianCalendar.SECOND).toString()

                startActivity(nextintent)
                Toast.makeText(
                    this, year + " 년 " + month + " 월 " + day + " 일 " + hour + " 시 " + min + " 분 " + sec + " 초 " + "에 요청이 완료 되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d("Time","현재 시간은 " + year + " 년 " + month + " 월 " + day + " 일 " + hour + " 시 " + min + " 분 " + sec + " 초 "
                )
            }
        }
    }
}
