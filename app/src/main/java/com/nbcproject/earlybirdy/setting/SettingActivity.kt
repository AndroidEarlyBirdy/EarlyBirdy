package com.nbcproject.earlybirdy.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivitySettingBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.setting.dialog.SettingDeleteDialog
import com.nbcproject.earlybirdy.setting.viewmodel.SettingViewModel
import com.nbcproject.earlybirdy.setting.viewmodel.SettingViewModelFactory
import com.nbcproject.earlybirdy.util.Constants.Companion.genralconditionUrl
import com.nbcproject.earlybirdy.util.Constants.Companion.openlicenseUrl
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.nbcproject.earlybirdy.util.showToast

import com.nbcproject.earlybirdy.util.Constants.Companion.supportUrl

class SettingActivity : MainActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var settingDeleteDialog: SettingDeleteDialog
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingViewModel =
            ViewModelProvider(this, SettingViewModelFactory())[SettingViewModel::class.java]
        settingDeleteDialog = SettingDeleteDialog(this@SettingActivity, settingViewModel)

        setOnclickListeners()
    }

    private fun setOnclickListeners() = with(binding) {
        //알림 설정 버튼
        btnNotificationSettings.setOnClickListener {
            presentNotificationSetting(this@SettingActivity)
        }

        //고객지원 구글폼으로 연결
        btnUserSupport.setOnClickListener {
            intentToSupportLink()
        }

        //오픈 라이선스 구글폼으로 연결
        btnOpenLicense.setOnClickListener {
            intentToOpenLicenseUrlLink()
        }

        //약관 구글폼으로 연결
        btnGenralCondition.setOnClickListener {
            intentToGeneralConditionLink()
        }

        //로그아웃 버튼
        btnLogout.setOnClickListener {
            signOut()
        }

        //회원 탈퇴 버튼
        btnDeleteAccount.setOnClickListener {
            settingDeleteDialog.show()
        }

        //뒤로가기 버튼
        ivBack.setOnClickListener {
            finish()
        }

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

    private fun intentToSupportLink() {
        val supportLink = supportUrl

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(supportLink))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(this, getString(R.string.setting_connect_error_toast))
        }
    }

    private fun intentToOpenLicenseUrlLink() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(openlicenseUrl))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(this, getString(R.string.setting_connect_error_toast))
        }
    }

    private fun intentToGeneralConditionLink() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(genralconditionUrl))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(this, getString(R.string.setting_connect_error_toast))
        }
    }

    //로그아웃
    private fun signOut() {
        settingViewModel.signOut()
        navigateToSigninActivity(this)
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
