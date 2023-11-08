package com.nbcproject.earlybirdy.sign.sendemail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl

class SendEmailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SendEmailViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            return SendEmailViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}