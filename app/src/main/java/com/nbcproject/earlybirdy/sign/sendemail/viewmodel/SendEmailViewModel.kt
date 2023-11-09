package com.nbcproject.earlybirdy.sign.sendemail.viewmodel

import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.repository.AuthRepository

class SendEmailViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun sendEmail(email : String) {
        authRepository.sendEmail(email)
    }
}