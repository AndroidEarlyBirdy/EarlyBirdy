package com.example.earlybirdy.signin.sendemail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SendEmailViewModel : ViewModel() {

    fun sendEmail(emailAddress : String) {
        Firebase.auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("send", emailAddress)
                }
            }.addOnFailureListener {
            }
    }
}