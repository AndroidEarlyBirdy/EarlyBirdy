package com.example.earlybirdy.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.databinding.ActivitySignupBinding
import com.example.earlybirdy.dto.UserDto
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

//        binding.tilPassword.editText.addTextChangedListener()

        // 회원가입
        binding.btnSignup.setOnClickListener {
            onStart()
        }

        binding.btnSignupGoogle.setOnClickListener {
            navigateToMainActivity(this)
        }

        // 로그인 페이지로 이동
        binding.tvBtnSignin.setOnClickListener {
            navigateToSigninActivity(this)
        }
    }

    // 회원가입 함수
    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
        if (user != null) { // 로그인 여부 체크
            navigateToMainActivity(this)
        } else {
            val profile = binding.ivProfile.toString() // 이미지 객체 정보
            val nickname = binding.titNickname.text.toString()
            val email = binding.titEmail.text.toString()
            val password = binding.titPassword.text.toString()
            val passwordCheck = binding.titPasswordCheck.text.toString()

            // 빈칸 확인
            if (nickname.isEmpty()) {
                binding.tilNickname.error = "닉네임을 입력해주세요"
            } else if (email.isEmpty()) {
                binding.tilEmail.error = "이메일을 입력해주세요"
            } else if (password.isEmpty()) {
                binding.tilPassword.error = "비밀번호를 입력해주세요"
            } else {
                if (password == passwordCheck) { // 비밀번호 확인
                    // auth 회원가입
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                updateUI(user)
                                // firestore DB에 저장
                                val userDto =
                                    UserDto(/*user!!.uid,*/profile, nickname, email, password)
                                db.collection("UserDto").document(user!!.uid)
                                    .set(userDto)
                                    .addOnSuccessListener { documentReference ->
                                    }
                                    .addOnFailureListener { e ->
                                    }
                                showToast(this, "회원가입 성공 & 자동로그인")
                                navigateToMainActivity(this)
                                finish()
                            } else { // 이미 있는 이메일, 닉네임 정보 예외처리
                                updateUI(null)
                            }
                        }
//                            .addOnFailureListener {
//                            when
//                        }
                } else {
                    binding.tilPasswordCheck.error = "같은 비밀번호를 입력해주세요"
                }

            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
    }
}