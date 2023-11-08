package com.nbcproject.earlybirdy.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.nbcproject.earlybirdy.alarm.AlarmActivity
import com.nbcproject.earlybirdy.board.board_read.BoardReadActivity
import com.nbcproject.earlybirdy.board.board_write.BoardWriteActivity
import com.nbcproject.earlybirdy.create_plan.CreatePlanActivity
import com.nbcproject.earlybirdy.edit_profile.EditProfileActivity
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.signin.sendemail.SendEmailActivity
import com.nbcproject.earlybirdy.resetpassword.ResetPasswordActivity
import com.nbcproject.earlybirdy.setting.SettingActivity
import com.nbcproject.earlybirdy.signin.SigninActivity
import com.nbcproject.earlybirdy.signup.SignupActivity

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

fun navigateToResetPassword(context: Context) {
    val intent = Intent(context, ResetPasswordActivity::class.java)
    context.startActivity(intent)
}

// 게시글 작성 페이지로 이동 함수
fun navigateToBoardWriteActivity(context: Context) {
    val intent = Intent(context, BoardWriteActivity::class.java)
    intent.putExtra("boardType", 1)
    context.startActivity(intent)
}

fun navigateToBoardReadActivity(context: Context) {
    val intent = Intent(context, BoardReadActivity::class.java)
    context.startActivity(intent)
}

// 토스트 메시지 함수
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}