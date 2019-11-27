package com.example.bboba

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
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

class LoginActivity : AppCompatActivity() {
    private var callback: SessionCallback = SessionCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

    private class SessionCallback(val context: Context) : ISessionCallback { //세션 콜백 함수->로그인 성공시 메인액티비티로 이동한다
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
                    // register or login
                    val nextIntent = Intent(context, MainActivity::class.java)
                    startActivity(context, nextIntent, null)
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
