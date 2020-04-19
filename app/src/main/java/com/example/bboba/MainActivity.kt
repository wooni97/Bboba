package com.example.bboba

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.auth.ApiResponseCallback
import com.kakao.auth.network.response.AccessTokenInfoResponse
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.helper.log.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_nav_header.*


class MainActivity : AppCompatActivity(),MapFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {
    val listFragment = ListFragment() // 하나의 프레그먼트를 계속 이용하기 위해 생성자로 생성후 변수에 저장
    val mapFragment = MapFragment()
    val context: Context = this // MainActivity context

    //카카오 계정에 로그인 되어있는지를 검사하여 로그인 정보가 없으면 바로 로그인 페이지로 이동하는 함수
    //토큰이 있으면 메인액티비티가 나온다
    private fun requestAccessTokenInfo(context: Context) { //context를 매개변수에 넣어줘야한다
        com.kakao.auth.AuthService.getInstance().requestAccessTokenInfo(object:
            ApiResponseCallback<AccessTokenInfoResponse>() {
            override fun onSessionClosed(errorResult: ErrorResult?) {
                val nextIntent = Intent(context, LoginActivity::class.java)
                startActivity(nextIntent)
            }
            override fun onNotSignedUp() {
                val nextIntent = Intent(context, LoginActivity::class.java)
                startActivity(nextIntent)
            }
            override fun onFailure(errorResult: ErrorResult?) {
                super.onFailure(errorResult)
                Logger.e("failed to get access token info. msg="+errorResult)
            }
            override fun onSuccess(result: AccessTokenInfoResponse?) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAccessTokenInfo(this)//카카오 로그인 여부 검사 후 인텐트로 화면 전환


        val adapter = ViewPagerAdapter(supportFragmentManager) //프레그먼트 이동을 위한 어댑터
        adapter.addFragment(listFragment, "시간순 보기")
        adapter.addFragment(mapFragment, "지도로 보기")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        //툴바 사용 설정
        setSupportActionBar(my_toolbar)
        // 2. 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // 네비게이션드로어 메뉴 사용
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp) // 네비게이션드로어 메뉴 아이콘 설정
        supportActionBar!!.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기

        //네비게이션뷰 리스너 등록
        navigationView.setNavigationItemSelectedListener(this)

        //요청글 작성 버튼 리스너 등록
        add_request_button.setOnClickListener {
            val nextIntent = Intent(this, RequestActivity::class.java)
            startActivity(nextIntent)
        }
    }

    // 3.툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // menu/main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    //툴바 메뉴 버튼이 클릭 됐을 때 콜백
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item!!.itemId){
            android.R.id.home -> {
                val profileview = nav_header_profile //네비게이션뷰의 프로필 사진
                drawerLayout.openDrawer(GravityCompat.START) //메뉴버튼을 누르면 네비게이션 드로어를 연다

                //카카오api에서 유저정보 받아오기
                UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                    override fun onFailure(errorResult: ErrorResult?) {
                    }
                    override fun onSessionClosed(errorResult: ErrorResult?) {
                    }
                    override fun onSuccess(result: MeV2Response?) {//세션정보를 잘 받아왔으면, 정보들을 채워넣는다
                        if(result!=null) {
                            val profileImage = result.kakaoAccount.profile.profileImageUrl
                            val userName = result.kakaoAccount.profile.nickname
                            val userEmail = result.kakaoAccount.email?:""
                            Glide.with(context).load(profileImage).transform(RoundedCorners(20)).into(profileview)
                            nav_profile_name.text = userName
                            nav_profile_email.text = userEmail
                        }
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //네비게이션 드로어 메뉴 선택시
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.cart -> { //나의 거래목록
                val intent = Intent(context,MyTradeActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> { //로그아웃
                UserManagement.getInstance().requestLogout(object: LogoutResponseCallback(){
                    override fun onCompleteLogout() {
                        //로그아웃 메세지 띄우기
                        val snackbar: Snackbar = Snackbar.make(my_toolbar, "로그아웃되었습니다", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                        //메세지 가운데 정렬
                        val view: View = snackbar.view
                        val txtv: TextView = view.findViewById(com.google.android.material.R.id.snackbar_text)
                        txtv.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        val nextIntent = Intent(context, LoginActivity::class.java)
                        startActivity(nextIntent)
                    }

                })
            }
            R.id.setting -> { //회원탈퇴
                val appendMessage: String = "회원탈퇴 하시겠습니까?"
                AlertDialog.Builder(this).setMessage(appendMessage)
                    .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                UserManagement.getInstance().requestUnlink(object: UnLinkResponseCallback() {
                                    override fun onSessionClosed(errorResult: ErrorResult) {
                                        val nextIntent = Intent(context, RequestActivity::class.java)
                                        startActivity(nextIntent)
                                    }
                                    override fun onNotSignedUp() {
                                        val nextIntent = Intent(context, RequestActivity::class.java)
                                        startActivity(nextIntent)
                                    }
                                    override fun onSuccess(result: Long?) {
                                        val snackbar: Snackbar = Snackbar.make(my_toolbar, "회원탈퇴가 처리완료 되었습니다.", Snackbar.LENGTH_SHORT)
                                        snackbar.show()
                                        val nextIntent = Intent(context, LoginActivity::class.java)
                                        startActivity(nextIntent)
                                    }
                                })
                                dialog!!.dismiss()
                            }
                        })
                    .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.dismiss()
                            }
                        }).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    /*
 * 뒤로가기 버튼으로 네비게이션 닫기
 * 네비게이션 드로어가 열려 있을 때 뒤로가기 버튼을 누르면 네비게이션을 닫고,
 * 닫혀 있다면 기존 뒤로가기 버튼으로 작동한다.
 */
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }else{
            Toast.makeText(this,"어플을 종료합니다.", Toast.LENGTH_SHORT).show()
            finishAffinity()        //메인페이지에서 뒤로가기를 누르면 어플이 종료된다
        }
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

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}