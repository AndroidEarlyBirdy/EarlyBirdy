package com.nbcproject.earlybirdy.sign.sendemail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.databinding.ActivitySendEmailBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.google.firebase.auth.FirebaseAuth
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.sign.sendemail.viewmodel.SendEmailViewModel
import com.nbcproject.earlybirdy.sign.sendemail.viewmodel.SendEmailViewModelFactory

class SendEmailActivity : MainActivity() {

    private lateinit var binding : ActivitySendEmailBinding
    private lateinit var sendEmailViewModel : SendEmailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sendEmailViewModel = ViewModelProvider(this,SendEmailViewModelFactory())[SendEmailViewModel::class.java]

        setOnclickListeners()
    }

    private fun setOnclickListeners() = with(binding) {
        btnSend.setOnClickListener {
            val emailAddress = binding.titSendEmail.text.toString()
            if (emailAddress.isBlank()) {
                binding.tilSendEmail.error = getString(R.string.util_error_emptyEmail)
            }
            else {
                sendEmailViewModel.sendEmail(emailAddress)
                navigateToSigninActivity(this@SendEmailActivity)
            }
        }

        // 나가기 = 앱 종료
        tvFinish.setOnClickListener {
            navigateToSigninActivity(this@SendEmailActivity)
        }
    }

    override fun onBackPressed() {
        navigateToSigninActivity(this)
    }
}