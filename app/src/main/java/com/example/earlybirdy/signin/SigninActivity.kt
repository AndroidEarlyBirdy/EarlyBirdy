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
            onStart()
        }

        binding.btnSigninGoogle.setOnClickListener {
            navigateToMainActivity(this)
        }

        // 나가기 = 앱 종료
        binding.tvFinish.setOnClickListener {
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
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) { // 로그인 여부 체크
            navigateToMainActivity(this)
        } else {
            val email = binding.titEmail.text.toString()
            val password = binding.titPassword.text.toString()

            if (email.isEmpty()) {
                binding.tilEmail.error = "이메일을 입력해주세요"
            } else if (password.isEmpty()) {
                binding.tilPassword.error = "비밀번호를 입력해주세요"
            } else {
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
            }
        }
    }
}