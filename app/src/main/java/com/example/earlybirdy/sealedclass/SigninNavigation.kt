package com.example.earlybirdy.sealedclass

import java.lang.Exception

sealed class SigninNavigation{
    // 이미 로그인 되어 있음
    object AlreadySignin: SigninNavigation()
    // 이메일 입력 상태
    data class EmptyEmail(val eMsg: String): SigninNavigation()
    // 패스워드 입력 상태
    data class EmptyPassword(val pMsg: String): SigninNavigation()
    // 로그인 성공
    object SigninSuccess: SigninNavigation()
    // 로그인 실패
    data class SigninFailed(val eMsg: String): SigninNavigation()
}
