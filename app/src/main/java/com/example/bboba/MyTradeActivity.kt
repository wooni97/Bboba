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
import kotlinx.android.synthetic.main.activity_my_trade.*
import kotlinx.android.synthetic.main.activity_request.*


open class MyTradeActivity : AppCompatActivity(){
    val myRequestFragment = MyRequestFragment()
    val myGivingFragment = MyGivingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_trade)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(myRequestFragment, "나의 요청")
        adapter.addFragment(myGivingFragment, "제공")
        viewPager_in_mytrade.adapter = adapter
        tabs_in_mytrade.setupWithViewPager(viewPager_in_mytrade)

        UserManagement.getInstance().me(object: MeV2ResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=실패")
            }
            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.d("example", "aaabb=세션 닫힘")
            }
            override fun onSuccess(result: MeV2Response?) {
                if(result!=null) {
                    val picture_location = result.kakaoAccount.profile.profileImageUrl?:"" //프로필 이미지가 없으면 null이 들어감
                    val name = result.kakaoAccount.profile.nickname
                    val userEmail = result.kakaoAccount.email?:""
                    if(picture_location!="") Glide.with(this@MyTradeActivity).load(picture_location).transform(RoundedCorners(20)).into(nav_header_profile_in_mytrade)
                    else Glide.with(this@MyTradeActivity).load(R.drawable.blank_profile).transform(RoundedCorners(20)).into(nav_header_profile_in_mytrade)
                    nav_profile_name_in_mytrade.text = name
                    nav_profile_email_in_mytrade.text = userEmail
                }
            }
        })
    }
}