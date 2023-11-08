package com.nbcproject.earlybirdy.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbcproject.earlybirdy.sealedclass.SigninNavigation
import com.google.firebase.auth.FirebaseAuth

class SigninViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    val signinState = MutableLiveData<SigninNavigation>()

    fun signin(email: String, password: String) {
//        val user = auth.currentUser
//        if (user != null) { // 로그인 여부 체크
//            signinState.value = SigninNavigation.AlreadySignin
//        } else {
//
//        }
        if (email.isEmpty()){ // 이메일 입력 여부
            signinState.value = SigninNavigation.EmptyEmail("이메일을 입력해주세요")
        }else if (password.isEmpty()){
            signinState.value = SigninNavigation.EmptyPassword("비밀번호를 입력해주세요")
        }else{
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        signinState.value = SigninNavigation.SigninSuccess
                    } else {
                        signinState.value = SigninNavigation.SigninFailed("로그인에 실패하였습니다. 이메일과 비밀번호를 확인해주세요.")
                    }
                }
        }
    }

}