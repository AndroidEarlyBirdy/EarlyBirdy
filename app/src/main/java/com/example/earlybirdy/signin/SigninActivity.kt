package com.example.earlybirdy.signin

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivitySigninBinding
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSendEmailActivity
import com.example.earlybirdy.util.navigateToSignupActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth

    val MY_PERMISSION_ACCESS_ALL = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user != null){
            showToast(this, "이미 로그인 중입니다.")
        }else{
            binding.tvBtnResetPassword.setOnClickListener {
                navigateToSendEmailActivity(this)
                finish()
            }
        }
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
            finish()
        }
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_right_exit)

        // 알림 권한 설정 함수
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            var permissions = arrayOf(
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }
    }

    //로그인 함수
    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) { // 로그인 여부 체크
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