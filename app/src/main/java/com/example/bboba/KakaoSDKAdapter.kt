package com.example.bboba

import android.content.Context
import android.provider.Settings
import com.kakao.auth.*
import com.kakao.network.ErrorResult
import com.kakao.util.helper.SharedPreferencesCache
import java.util.*

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

    override fun getPushConfig(): IPushConfig {
        return object:IPushConfig {
            override fun getTokenRegisterCallback(): ApiResponseCallback<Int> {
                return object:ApiResponseCallback<Int>() {
                    override fun onSessionClosed(errorResult: ErrorResult?) {
                    }
                    override fun onSuccess(result: Int?) {
                    }
                }
            }
            override fun getDeviceUUID(): String {
                var deviceUUID: String = ""
                val cache: SharedPreferencesCache = Session.getCurrentSession().appCache
                val id: String? = cache.getString("PROPERTY_DEVICE_ID")

                if(id!=null){
                    deviceUUID = id;
                    return deviceUUID;
                }
                else {//다시
                    var uuid: UUID? = null;
                    val context: Context = applicationConfig.applicationContext
                    val androidId: String = Settings.Secure.ANDROID_ID
                    return androidId
                }
            }

        }
    }
}