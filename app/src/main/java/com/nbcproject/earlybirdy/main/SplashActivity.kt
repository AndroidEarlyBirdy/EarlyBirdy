package com.nbcproject.earlybirdy.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.nbcproject.earlybirdy.signin.SigninActivity
import com.nbcproject.earlybirdy.util.navigateToMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.nbcproject.earlybirdy.R

class SplashActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        signCheck()
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