package com.example.earlybirdy.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.R
import com.example.earlybirdy.board.BoardFragment
import com.example.earlybirdy.databinding.ActivitySettingBinding
import com.example.earlybirdy.edit_profile.EditProfileActivityDialog
import com.example.earlybirdy.my_page.MyPageFragment
import com.example.earlybirdy.signin.SigninActivity
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var settingDeleteDialog: SettingDeleteDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        settingDeleteDialog = SettingDeleteDialog(this@SettingActivity)
        database = Firebase.database.reference
        user = auth.currentUser!!
        fireStore = FirebaseFirestore.getInstance()

        //고객지원 구글폼으로 연결
        binding.btnUserSupport.setOnClickListener {
            val supportUrl = "https://forms.gle/hgZMcfx2uWNDUkTe6"

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(supportUrl))
            try {
                startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {

            }
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

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnDeleteAccount.setOnClickListener {
            settingDeleteDialog.show()
        }
        //화면전환 애니메이션
        overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_left_exit)

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


