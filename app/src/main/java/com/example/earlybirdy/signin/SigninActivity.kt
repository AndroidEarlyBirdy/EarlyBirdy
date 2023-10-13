package com.example.earlybirdy.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.databinding.ActivitySigninBinding
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSignupActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //로그인
        binding.btnSignin.setOnClickListener {
            signIn()
        }

        binding.btnSigninGoogle.setOnClickListener {
            navigateToMainActivity(this)
        }

        // 나가기 = 앱 종료
        binding.tvFinish.setOnClickListener {
            //val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${}"))
            moveTaskToBack(true)
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        // 회원가입 페이지로 이동
        binding.tvBtnSignup.setOnClickListener {
            //val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${}"))
            navigateToSignupActivity(this)
        }
    }

    //로그인 함수
    private fun signIn() {
        val email = binding.titEmail.text.toString()
        val password = binding.titPassword.text.toString()

        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        showToast(this, "로그인 성공")
                        navigateToMainActivity(this)
                        finish()
                    } else {
                        showToast(this, "로그인 실패!")
                    }
                }
        } else {
            showToast(this, "이메일 및 비밀번호를 입력해주세요")
        }
    }
}

//finish() : 이 코드가 속해있는 액티비티를 종료 onDestroy()를 호출
//system.exit(0) : 현재 액티비티를 종료
//android.os.Process.killProcess(android.os.Process.myPid()) : 현재의 프로세스 및 서비스를 종료
//moveTaskToBack(boolean): 현재 어플을 백그라운드로 넘김 /  현재 실행되고있는 어플이 하나라면 홈화면으로 바귀지만 종료는 아님