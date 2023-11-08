package com.nbcproject.earlybirdy.repository

interface AuthRepository {
    fun signOut()
    fun checkAuth(email : String, password : String)
    fun deleteUser()
    fun sendEmail(email : String)
}