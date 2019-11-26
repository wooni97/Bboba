package com.example.bboba

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.tabs.TabLayout
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_givemain.*
import kotlinx.android.synthetic.main.activity_main.*
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

    override fun onResume() {
        super.onResume()
        //교수님이 말씀하신 매칭 선택 후 뒤로가기를 통해 map화면으로 오는 것이 아닌 자동으로 map 화면 띄우기 구현

        var fragmentnumber = this.intent.getIntExtra("fragmentNumber", 1)
        if(fragmentnumber==2) {
            val tabhost: TabLayout = this.findViewById(R.id.tabs)
            tabhost.getTabAt(1)!!.select()
        }
    }
}