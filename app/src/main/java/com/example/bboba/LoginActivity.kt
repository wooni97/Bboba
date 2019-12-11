package com.example.bboba

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.Utility.getPackageInfo
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

//카카오 Developer 사이트의 공식 자바코드를 코틀린 형식에 맞게 재작성함
class LoginActivity : AppCompatActivity() {
    val activity: Activity = this
    private var callback: SessionCallback = SessionCallback(this, activity)
    private val multiplePermissionCode = 100
    private val requiredPermissionList = arrayOf(Manifest.permission.SEND_SMS)
    private fun checkPermissions() {
        val rejectedPermissionList = ArrayList<String>()
         //필요한 퍼미션들을 체크한다
        for(permission in requiredPermissionList) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //필요한 권한중 승인 받지 못한 것을 배열에 담는다
                rejectedPermissionList.add(permission)
            }
        }
        if(rejectedPermissionList.isNotEmpty()) { //승인 받지 못한 권한이 존재하면
            //권한 요청하기
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            multiplePermissionCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                        }
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //권한 설정 체크 함수 실행
        checkPermissions()
        //세션 검사
        Session.getCurrentSession().addCallback(callback)
        //Log.d("example", "hash::"+getHashKey(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("session","session get current session")
            getHashKey(this)
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private class SessionCallback(val context: Context, val activity: Activity) : ISessionCallback { //세션 콜백 함수->로그인 성공시 메인액티비티로 이동한다
        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e("session","Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {

                override fun onFailure(errorResult: ErrorResult?) {
                    Log.d("exception","Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.e("exception","Session Call back :: onSessionClosed ${errorResult?.errorMessage}")
                }
                override fun onSuccess(result: MeV2Response?) {
                    checkNotNull(result) { "session response null" }
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("Phone")
                    val myEmail = result.kakaoAccount.email
                    val userRealId = myEmail.substring(0,myEmail.indexOf('@'))//아이디 추출
                    val idRef = myRef.child(userRealId)
                    idRef.addListenerForSingleValueEvent(object:ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.child("phone_number").value == null){
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("핸드폰 번호 입력")
                                builder.setMessage("010ABCDEFGH 형식으로 입력해주세요")

                                val et: EditText = EditText(context)
                                builder.setView(et)
                                builder.setPositiveButton("완료", object: DialogInterface.OnClickListener{
                                    override fun onClick(dialog: DialogInterface, which: Int) {
                                        val phoneNumber = et.text.toString()
                                        p0.child("phone_number").ref.setValue(phoneNumber)
                                        dialog.dismiss()

                                        //성공적으로 로그인 했으므로 메인액티비티로 이동한다
                                        val nextIntent = Intent(context, MainActivity::class.java)
                                        startActivity(context, nextIntent, null)
                                    }
                                })

                                builder.show()
                            }
                            else {
                                // 성공적으로 세션 확인이 되었으므로 메인액티비티로 이동한다
                                val nextIntent = Intent(context, MainActivity::class.java)
                                startActivity(context, nextIntent, null)
                            }
                        }
                    })
                }
            })
        }
    }
    fun getHashKey(context: Context): String? { //카카오 api에 해쉬키를 등록할 때 사용하는 함수
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                val packageInfo = getPackageInfo(context, PackageManager.GET_SIGNING_CERTIFICATES)
                val signatures = packageInfo.signingInfo.apkContentsSigners
                val md = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    return String(Base64.encode(md.digest(), NO_WRAP))
                }
            } else {
                val packageInfo =
                    getPackageInfo(context, PackageManager.GET_SIGNATURES) ?: return null

                for (signature in packageInfo!!.signatures) {
                    try {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                    } catch (e: NoSuchAlgorithmException) {
                        Log.w("exception",
                            "Unable to get MessageDigest. signature=$signature"
                        )
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }
    override fun onBackPressed() { //뒤로가기 버튼을 누르면 어플을 종료한다
        Toast.makeText(this,"어플을 종료합니다.", Toast.LENGTH_SHORT).show()
        finishAffinity()
    }
}
