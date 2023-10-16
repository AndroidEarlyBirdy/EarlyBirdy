package com.example.earlybirdy.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.signin.SigninActivity
import com.example.earlybirdy.signup.SignupActivity

// 메인 액티비티로 이동 함수
fun navigateToMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
}

// 로그인 액티비티로 이동 함수
fun navigateToSigninActivity(context: Context) {
    val intent = Intent(context, SigninActivity::class.java)
    context.startActivity(intent)
}

// 회원가입 액티비티로 이동 함수
fun navigateToSignupActivity(context: Context) {
    val intent = Intent(context, SignupActivity::class.java)
    context.startActivity(intent)
}

// 토스트 메시지 함수
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}