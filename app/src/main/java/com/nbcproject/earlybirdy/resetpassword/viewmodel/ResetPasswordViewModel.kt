package com.nbcproject.earlybirdy.resetpassword.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl
import com.nbcproject.earlybirdy.sealedclass.CheckChangePassword

class ResetPasswordViewModel(private val authRepository : AuthRepositoryImpl) : ViewModel() {

    val changePasswordState : LiveData<CheckChangePassword> = authRepository.changePasswordState

    fun changePassword(password : String) {
        authRepository.changePassword(password)
    }

}