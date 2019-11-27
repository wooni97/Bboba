package com.example.bboba

import com.kakao.auth.*
import com.kakao.network.ErrorResult
import com.kakao.auth.ApiResponseCallback
import android.os.Bundle
import java.util.UUID.randomUUID
import java.util.UUID.nameUUIDFromBytes
import android.content.Context.TELEPHONY_SERVICE
import android.media.audiofx.BassBoost
import androidx.core.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager

import com.kakao.util.helper.SharedPreferencesCache
import com.kakao.auth.IPushConfig
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.UUID.randomUUID
import java.util.UUID.nameUUIDFromBytes
import android.content.Context.TELEPHONY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.provider.Settings.Secure




class KakaoSDKAdapter: KakaoAdapter() {
    override fun getSessionConfig(): ISessionConfig {
        return object : ISessionConfig {
            override fun getAuthTypes(): Array<AuthType> {
                return arrayOf(AuthType.KAKAO_ACCOUNT)
            }

            override fun isUsingWebviewTimer(): Boolean {
                return false
            }

            override fun getApprovalType(): ApprovalType? {
                return ApprovalType.INDIVIDUAL
            }

            override fun isSaveFormData(): Boolean {
                return true
            }

            override fun isSecureMode(): Boolean {
                return true
            }
        }
    }

    override fun getApplicationConfig(): IApplicationConfig {
        return IApplicationConfig {
            GlobalApplication.instance?.getGlobalApplicationContext()
        }
    }


}