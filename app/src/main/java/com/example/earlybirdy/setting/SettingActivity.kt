package com.example.earlybirdy.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivitySettingBinding
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast

import com.example.earlybirdy.util.Constants.Companion.supportUrl
import com.example.earlybirdy.util.navigateToDetail

class SettingActivity : MainActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var settingDeleteDialog: SettingDeleteDialog
    private val settingViewModel: SettingViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingDeleteDialog = SettingDeleteDialog(this@SettingActivity)

        setOnclickListener()

        //화면전환 애니메이션
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_left_exit)
    }

    private fun setOnclickListener() {

        //고객지원 구글폼으로 연결
        binding.btnUserSupport.setOnClickListener {
            intentToSupportLink()
        }

        //오픈 라이선스 버튼
        binding.btnOpenLicense.setOnClickListener {
            navigateToDetail("오픈 라이선스", this)
        }

        //약관 버튼
        binding.btnGenralCondition.setOnClickListener {
            navigateToDetail("약관", this)
        }

        //알림 설정 버튼
        binding.btnNotificationSettings.setOnClickListener {
            presentNotificationSetting(this)

        }

        //로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            signOut()
        }

        //뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        //회원 탈퇴 버튼
        binding.btnDeleteAccount.setOnClickListener {
            settingDeleteDialog.show()
        }
    }

    private fun intentToSupportLink() {
        val supportLink = supportUrl

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(supportLink))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(this, "연결 오류 발생")
        }
    }

    //로그아웃
    private fun signOut() {
        settingViewModel.signOut()
        showToast(this, "로그아웃 되었습니다. 로그인 페이지로 이동합니다.")
        finish()
        navigateToSigninActivity(this)
    }

    //Notification Setting Page 이동 함수
    private fun presentNotificationSetting(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationSettingOreo(context)
        } else {
            notificationSettingOreoLess(context)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    //오레오 버전 이상부터 사용 가능
    //권한 설정 페이지 이동
    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationSettingOreo(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    //오레오 버전 이하에서 사용 가능
    //권한 설정 페이지 이동
    private fun notificationSettingOreoLess(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo?.uid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

}
