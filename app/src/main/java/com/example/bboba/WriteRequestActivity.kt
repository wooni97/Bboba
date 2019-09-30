package com.example.bboba

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.write_req.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase




class WriteRequestActivity:AppCompatActivity() {
    lateinit var request: MutableMap<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_req)
        //툴바 생성
        setSupportActionBar(write_request_toolbar)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        //추가버튼
        add_req.setOnClickListener(){
            val builder = AlertDialog.Builder(this)
            val username = findViewById<EditText>(R.id.edit_name).text.toString()
            val totalpage = findViewById<EditText>(R.id.edit_page).text.toString()
            val usertel = findViewById<EditText>(R.id.edit_tel).text.toString()
            val detailreq = findViewById<EditText>(R.id.edit_detail).text.toString()
            builder.setTitle("프린트 요청 글 작성")
            builder.setMessage("이름 : $username \n" +
                    "페이지 수 : $totalpage \n" +
                    "전화번호 : $usertel \n" +
                    "요청사항 : $detailreq")

            builder.setPositiveButton("요청하기") { _, _ ->
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("prints_request").child("2")
                request = mutableMapOf()
                request.put("user_name","$username")
                request.put("total_page","$totalpage")
                request.put("uset_tel","$usertel")
                request.put("detail_request", "$detailreq")

                myRef.setValue(request)

                Toast.makeText(applicationContext, "프린트 요청글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("취소") { _, _ ->
                Toast.makeText(applicationContext, "취소", Toast.LENGTH_SHORT).show()
            }
//            builder.setNeutralButton("Cancel") {_,_ ->
//                Toast.makeText(applicationContext, "you cancelled the dialog", Toast.LENGTH_SHORT).show()
//            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.write_request_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.setting -> {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}