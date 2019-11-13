package com.example.bboba

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_givemain.*
import kotlinx.android.synthetic.main.activity_request.*


open class MyTrade : AppCompatActivity(){
    val myRequestFragment = MyRequestFragment()
    val myGivingFragment = MyGivingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_trade)
        setSupportActionBar(give_toolbar)

        //supportActionBar!!.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기
        supportActionBar!!.title = "나의 거래"

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(myRequestFragment, "나의 요청")
        adapter.addFragment(myGivingFragment, "제공")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}