package com.nbcproject.earlybirdy.sign.sendemail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.databinding.ActivitySendEmailBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.nbcproject.earlybirdy.util.navigateToSignupActivity
import com.google.firebase.auth.FirebaseAuth
import com.nbcproject.earlybirdy.sign.sendemail.viewmodel.SendEmailViewModel
import com.nbcproject.earlybirdy.sign.sendemail.viewmodel.SendEmailViewModelFactory

class SendEmailActivity : MainActivity() {

    private lateinit var binding : ActivitySendEmailBinding
    private lateinit var sendEmailViewModel : SendEmailViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sendEmailViewModel = ViewModelProvider(this,SendEmailViewModelFactory())[SendEmailViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        setOnclickListeners()
    }

    private fun setOnclickListeners() = with(binding) {
        btnSend.setOnClickListener {
            onStart()
            navigateToSigninActivity(this@SendEmailActivity)
        }

        // 나가기 = 앱 종료
        tvFinish.setOnClickListener {
            navigateToSigninActivity(this@SendEmailActivity)
        }

//        // 회원가입 페이지로 이동
//        binding.tvBtnSignup.setOnClickListener {
//            navigateToSignupActivity(this)
//        }
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
            val emailAddress = binding.titSendEmail.text.toString()
            if (emailAddress.isNotBlank()) {
                sendEmailViewModel.sendEmail(emailAddress)
            }
    }

    override fun onBackPressed() {
        navigateToSigninActivity(this)
    }
}