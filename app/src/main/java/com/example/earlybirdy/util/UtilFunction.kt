package com.example.earlybirdy.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.earlybirdy.alarm.AlarmActivity
import com.example.earlybirdy.create_plan.CreatePlanActivity
import com.example.earlybirdy.edit_profile.EditProfileActivity
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.resetpassword.ResetPasswordActivity
import com.example.earlybirdy.signin.sendemail.SendEmailActivity
import com.example.earlybirdy.setting.SettingActivity
import com.example.earlybirdy.setting.SettingDetailActivity
import com.example.earlybirdy.signin.SignInActivity
import com.example.earlybirdy.signup.SignupActivity

// 메인 액티비티로 이동 함수
fun navigateToMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
}

// 로그인 액티비티로 이동 함수
fun navigateToSigninActivity(context: Context) {
    val intent = Intent(context, SignInActivity::class.java)
    context.startActivity(intent)
}

// 회원가입 액티비티로 이동 함수
fun navigateToSignupActivity(context: Context) {
    val intent = Intent(context, SignupActivity::class.java)
    context.startActivity(intent)
}
// 개인정보 수정 액티비티로 이동 함수
fun navigateToEditProfileActivity(context: Context) {
    val intent = Intent(context, EditProfileActivity::class.java)
    context.startActivity(intent)
}
// 오늘의목표 액티비티로 이동 함수
fun navigateToCreatePlanActivity(context: Context) {
    val intent = Intent(context, CreatePlanActivity::class.java)
    context.startActivity(intent)
}

fun navigateToSettingActivity(context: Context) {
    val intent = Intent(context, SettingActivity::class.java)
    context.startActivity(intent)
}

// 비밀번호 재설정 액티비티로 이동 함수
fun navigateToSendEmailActivity(context: Context) {
    val intent = Intent(context, SendEmailActivity::class.java)
    context.startActivity(intent)
}

// 알람 액티비티로 이동 함수
fun navigateToAlarmActivity(context: Context) {
    val intent = Intent(context, AlarmActivity::class.java)
    context.startActivity(intent)
}

fun navigateToDetail(title: String, context:Context) {
    val intent = Intent(context, SettingDetailActivity::class.java)
    intent.putExtra(Constants.SETTING_TITLE_KEY, title)
    context.startActivity(intent)
}

fun navigateToResetPassword(context: Context) {
    val intent = Intent(context, ResetPasswordActivity::class.java)
    context.startActivity(intent)
}

// 토스트 메시지 함수
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}