package com.example.bboba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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
