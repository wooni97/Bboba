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


open class GiveMainActivity : AppCompatActivity(),MapFragment.OnFragmentInteractionListener {
    val listFragment = ListFragment()
    val mapFragment = MapFragment()

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_givemain)
        setSupportActionBar(give_toolbar)

        //supportActionBar!!.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기
        supportActionBar!!.title = "프린트 요청 목록 확인"

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(listFragment, "시간순 보기")
        adapter.addFragment(mapFragment, "위치로 보기")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}