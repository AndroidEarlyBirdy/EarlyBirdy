package com.nbcproject.earlybirdy.setting.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl

class SettingViewModel(private val authRepository: AuthRepositoryImpl) : ViewModel() {
    fun signOut() {
        authRepository.signOut()
    }
}