package com.example.bboba

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.kakao.auth.ApiResponseCallback
import com.kakao.auth.network.response.AccessTokenInfoResponse
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.helper.log.Logger
import kotlinx.android.synthetic.main.main_nav_header.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val context: Context = this



    //카카오 계정에 로그인 되어있는지를 검사하여 로그인 정보가 없으면 바로 로그인 페이지로 이동
    //토큰이 있으면 메인액티비티가 나온다
    fun requestAccessTokenInfo(context: Context) { //context를 매개변수에 넣어줘야한다
        com.kakao.auth.AuthService.getInstance().requestAccessTokenInfo(object:ApiResponseCallback<AccessTokenInfoResponse>() {
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


        //액티비티 이동
        bt_req.setOnClickListener {
            //요청하기
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
        menuInflater.inflate(R.menu.main_menu, menu) // menu/main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }
    //툴바 메뉴 버튼이 클릭 됐을 때 콜백
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item!!.itemId){
            // 네비게이션 드로어 열기
            android.R.id.home -> {
                val profileview = nav_header_profile //네비게이션뷰의 프로필 사진
                drawerLayout.openDrawer(GravityCompat.START)
                //카카오api에서 유저정보 받아오기
                UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                    override fun onFailure(errorResult: ErrorResult?) {
                        Log.d("example", "aaabb=실패")
                    }
                    override fun onSessionClosed(errorResult: ErrorResult?) {
                        Log.d("example", "aaabb=세션 닫힘")
                    }

                    override fun onSuccess(result: MeV2Response?) {
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
            R.id.cart -> {
                val intent = Intent(context,MyTradeActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                UserManagement.getInstance().requestLogout(object:LogoutResponseCallback(){
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
//            R.id.bug_report -> { //임시로 로그인으로 사용
//                val nextIntent = Intent(this, LoginActivity::class.java)
//                startActivity(nextIntent)
//            }
            R.id.setting -> { //회원탈퇴
                //chat test
                val nextintent = Intent(context,ChatActivity::class.java)
                startActivity(nextintent)

                /*
                val appendMessage: String = "회원탈퇴 하시겠습니까?"
                AlertDialog.Builder(this).setMessage(appendMessage)
                    .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                UserManagement.getInstance().requestUnlink(object: UnLinkResponseCallback() {
                                    override fun onFailure(errorResult: ErrorResult) {
                                        super.onFailure(errorResult)
                                    }
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
                        }).show()*/
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
            finishAffinity()
        }
    }


}