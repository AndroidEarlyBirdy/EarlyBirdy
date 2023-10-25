package com.example.earlybirdy.setting

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    fun signOut() {
        if (user != null) {
            auth.signOut()
        }
    }
}