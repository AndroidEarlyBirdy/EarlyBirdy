package com.example.earlybirdy.signin.sendemail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.earlybirdy.databinding.ActivitySendEmailBinding
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.navigateToSignupActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SendEmailActivity : MainActivity() {
    private val binding by lazy { ActivitySendEmailBinding.inflate(layoutInflater) }
    private val sendEmailViewModel: SendEmailViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.btnSend.setOnClickListener {
            onStart()
            finish()
            navigateToSigninActivity(this)
        }

        // 나가기 = 앱 종료
        binding.tvFinish.setOnClickListener {
            finish()
        }

        // 회원가입 페이지로 이동
        binding.tvBtnSignup.setOnClickListener {
            navigateToSignupActivity(this)
        }
    }

    override fun onBackPressed() {
        navigateToSigninActivity(this)
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
        if (user != null) {
            showToast(this, "이미 로그인 중 입니다.")
        }
        else {
            val emailAddress = binding.titSendEmail.text.toString()
            if (emailAddress.isNotBlank()) {
                sendEmailViewModel.sendEmail(emailAddress)
            }
        }
    }
}