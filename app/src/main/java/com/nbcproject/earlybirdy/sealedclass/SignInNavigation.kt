package com.nbcproject.earlybirdy.sealedclass

sealed class SignInNavigation{
    // 로그인 성공
    object SignInSuccess: SignInNavigation()
    // 로그인 실패
    object SignInFailed: SignInNavigation()
}
