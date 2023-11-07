package com.example.earlybirdy.main

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.R
import com.example.earlybirdy.alarm.AlarmReceiver
import com.example.earlybirdy.signin.SigninActivity
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        signCheck()

//        val receiver = ComponentName(this, AlarmReceiver::class.java)
//
//        this.packageManager.setComponentEnabledSetting(
//            receiver,
//            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//            PackageManager.DONT_KILL_APP
//        )
    }

    private fun signCheck() {
        val user = auth.currentUser

        if (user != null) {
            navigateToMainActivity(this)
            finish()
        } else {
            Handler().postDelayed({
                val intent = Intent(this, SigninActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }, DURATION)
        }
    }

    companion object {
        private const val DURATION: Long = 3000
    }
}