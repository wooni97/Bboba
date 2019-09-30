package com.example.bboba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //액티비티 이동
        write_request.setOnClickListener {
            val nextIntent = Intent(this, WriteRequestActivity::class.java)
            startActivity(nextIntent)
        }
        //액션바 생성
        setSupportActionBar(main_toolbar)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        //데이터베이스 읽기
        val reqMap = mutableMapOf<String, String>()
        val database = FirebaseDatabase.getInstance()
        val reqRef = database.getReference("prints_request").child("2")
        reqMap.clear()
        reqRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for(h in p0.children) {
                    val req = ""+h.value
                    reqMap[h.key!!] = req //갱신할 때마다 최신 값을 유지함
                    Log.d("example", "value=진짜값 :$req")
                }
                Log.d("example", "value=0: $reqMap")
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        val pr = arrayListOf<Prints_Request>(
            Prints_Request("user1","1","tel1","det1"),
            Prints_Request("user2","2","tel2","det2"),
            Prints_Request("user3","3","tel3","det3"),
            Prints_Request("user4","4","tel4","det4"),
            Prints_Request("user5","5","tel5","det5"),
            Prints_Request("user6","6","tel6","det6"),
            Prints_Request("user7","7","tel7","det7"),
            Prints_Request("user8","8","tel8","det8"),
            Prints_Request("user9","9","tel9","det9"),
            Prints_Request("user10","10","tel10","det10"),
            Prints_Request("user11","11","tel11","det11"),
            Prints_Request("user12","12","tel12","det12")
        )

        //레이아웃매니저 설정
        recyclerview_item.layoutManager = LinearLayoutManager(this)
        recyclerview_item.setHasFixedSize(true)

        //어답터 설정
        recyclerview_item.adapter = RecyclerViewAdapter(pr)

    }
    data class Prints_Request(val user_name: String="", val total_page: String="", val user_tel: String="", val detail_req: String=""){
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.v_map -> {
                true
            }
            R.id.setting -> {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}

