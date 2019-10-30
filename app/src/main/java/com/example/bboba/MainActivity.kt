package com.example.bboba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

//import kotlinx.android.synthetic.main.activity_request.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //액티비티 이동
        bt_req.setOnClickListener {

            val nextIntent = Intent(this, RequestActivity::class.java)
            startActivity(nextIntent)
        }
        bt_give.setOnClickListener {
            //제공하기
            val nextIntent = Intent(this, GiveMainActivity::class.java)
            startActivity(nextIntent)
        }

        //툴바 사용 설정
        setSupportActionBar(my_toolbar)
        // 2. 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp) // 왼쪽 버튼 아이콘 설정
        supportActionBar!!.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기
        //네비게이션뷰 리스너 등록
        navigationView.setNavigationItemSelectedListener(this)
    }

    // 3.툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }
    //툴바 메뉴 버튼이 클릭 됐을 때 콜백
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item!!.itemId){
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
        }
        return super.onOptionsItemSelected(item)
    }

    //네비게이션 드로어 메뉴 선택시
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.account -> {
                Snackbar.make(my_toolbar, "clicked", Snackbar.LENGTH_SHORT).show()
            }
            R.id.cart -> {

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    /*
 * 뒤로가기 버튼으로 네비게이션 닫기
 *
 * 네비게이션 드로어가 열려 있을 때 뒤로가기 버튼을 누르면 네비게이션을 닫고,
 * 닫혀 있다면 기존 뒤로가기 버튼으로 작동한다.
 */
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }else{
            Toast.makeText(this,"어플을 종료합니다.", Toast.LENGTH_SHORT).show()
            finishAffinity()
        }
    }
}

