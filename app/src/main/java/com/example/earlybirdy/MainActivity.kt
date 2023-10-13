package com.example.earlybirdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    //val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val btnSignout: Button = findViewById<Button>(R.id.btn_signout_test)
        btnSignout.setOnClickListener {
            logOut()
        }
    }

    private fun login() {
        val tvTest = findViewById<TextView>(R.id.tv_test)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            showToast(this, "로그인 중 = ${currentUser.email}")
            tvTest.setText("${currentUser.uid}")
        } else {
            navigateToSigninActivity(this)
        }
    }

    private fun logOut() {
        val tvTest = findViewById<TextView>(R.id.tv_test)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()
            showToast(this, "로그아웃 성공!")
            navigateToSigninActivity(this)
        } else {
            showToast(this, "로그인을 먼저 해주세요")
            tvTest.setText("${currentUser?.uid}")
            navigateToSigninActivity(this)
        }
    }
}

