package com.nbcproject.earlybirdy.setting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl
import com.nbcproject.earlybirdy.repository.UserRepositoryImpl
import com.nbcproject.earlybirdy.sealedclass.CheckAuth
import com.nbcproject.earlybirdy.sealedclass.CheckDelete

class SettingViewModel(private val authRepository: AuthRepositoryImpl, private val userRepository: UserRepositoryImpl) : ViewModel() {

    val userEmail : LiveData<String> = userRepository.userEmail
    val checkAuth : LiveData<CheckAuth> = authRepository.checkAuthState
    val checkDeleteAuth : LiveData<CheckDelete> = authRepository.checkDeleteAuth
    val checkDeleteData : LiveData<CheckDelete> = userRepository.checkDeleteData

    fun signOut() {
        authRepository.signOut()
    }

    fun getUserEmail(userId : String) {
        userRepository.getUserEmail(userId)
    }

    fun checkAuth(email : String, password : String) {
        authRepository.checkAuth(email,password)
    }

    fun checkDeleteAuth() {
        authRepository.deleteUser()
    }

    fun checkDeleteData(userId : String) {
        userRepository.deleteUserData(userId)
    }
}