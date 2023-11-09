package com.nbcproject.earlybirdy.sign.signin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.sealedclass.SignInNavigation
import com.nbcproject.earlybirdy.repository.AuthRepositoryImpl

class SignInViewModel(private val authRepository: AuthRepositoryImpl): ViewModel() {
    val signInState : LiveData<SignInNavigation> = authRepository.signInState

    fun signIn(email: String, password: String) {
            authRepository.signIn(email,password)
        }

}
