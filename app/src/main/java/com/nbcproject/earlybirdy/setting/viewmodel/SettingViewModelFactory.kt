package com.nbcproject.earlybirdy.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl
import com.nbcproject.earlybirdy.repository.UserRepositoryImpl

class SettingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl()
            val userRepository = UserRepositoryImpl()
            return SettingViewModel(authRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}