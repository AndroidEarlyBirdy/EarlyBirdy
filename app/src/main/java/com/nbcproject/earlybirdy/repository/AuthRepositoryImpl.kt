package com.nbcproject.earlybirdy.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.nbcproject.earlybirdy.sealedclass.CheckAuth
import com.nbcproject.earlybirdy.sealedclass.CheckDelete

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    private var _checkAuthState = MutableLiveData<CheckAuth>()
    val checkAuthState : LiveData<CheckAuth> get() = _checkAuthState

    private var _checkDeleteAuth = MutableLiveData<CheckDelete>()
    val checkDeleteAuth : LiveData<CheckDelete> get() = _checkDeleteAuth

    override fun signOut() {
        if (user != null) {
            auth.signOut()
        }
    }

    override fun checkAuth(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _checkAuthState.value = CheckAuth.SuccessAuth
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        _checkAuthState.value = CheckAuth.InvalidCredential
                    } else {
                        _checkAuthState.value = CheckAuth.ElseException
                    }
                }
            }
    }

    override fun deleteUser() {
        user?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) {
                _checkDeleteAuth.value = CheckDelete.DeleteSuccess
            }
        }
    }

    override fun sendEmail(email : String) {
        auth.sendPasswordResetEmail(email)
    }
}