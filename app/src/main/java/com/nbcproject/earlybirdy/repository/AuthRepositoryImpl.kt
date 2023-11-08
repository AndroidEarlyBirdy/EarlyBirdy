package com.nbcproject.earlybirdy.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    override fun signOut() {
        if (user != null) {
            auth.signOut()
        }
    }
}