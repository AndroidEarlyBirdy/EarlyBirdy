package com.nbcproject.earlybirdy.setting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl
import com.nbcproject.earlybirdy.repository.UserRepositoryImpl

class SettingViewModel(private val authRepository: AuthRepositoryImpl, private val userRepository: UserRepositoryImpl) : ViewModel() {

    val userEmail : LiveData<String> = userRepository.userEmail

    fun signOut() {
        authRepository.signOut()
    }

    fun getUserEmail(userId : String) {
        userRepository.getUserEmail(userId)
    }
}