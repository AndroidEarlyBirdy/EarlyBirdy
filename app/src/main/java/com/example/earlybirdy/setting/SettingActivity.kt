package com.example.earlybirdy.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.R
import com.example.earlybirdy.board.BoardFragment
import com.example.earlybirdy.databinding.ActivitySettingBinding
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast

import com.google.firebase.auth.FirebaseAuth


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnUserSupport.setOnClickListener {
            val title = "고객 지원"
            navigateToDetail("고객 지원")
        }

        binding.btnOpenLicense.setOnClickListener {
            val title = "오픈 라이선스"
            navigateToDetail("오픈 라이선스")
        }

        binding.btnGenralCondition.setOnClickListener {
            val title = "약관"
            navigateToDetail("약관")
        }

        binding.btnNotificationSettings.setOnClickListener {
            presentNotificationSetting(this)

        }
        binding.btnLogout.setOnClickListener {
            signOut()
            Log.d("SettingActivity", "Logout button clicked")
        }
    }

    private fun signOut() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
            showToast(this, "로그아웃 되었습니다. 로그인 페이지로 이동합니다.")
            finish()
            navigateToSigninActivity(this)
        }
    }

    fun presentNotificationSetting(context: Context) {
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationSettingOreo(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    //오레오 버전 이하에서 사용 가능
    fun notificationSettingOreoLess(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo?.uid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    private fun navigateToDetail(title: String) {
        val intent = Intent(this, SettingDetailActivity::class.java)
        intent.putExtra("title", title)
        startActivity(intent)
    }
}


