package com.example.earlybirdy.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.databinding.ActivitySignupBinding
import com.example.earlybirdy.dto.UserDto
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
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

        // 회원가입
        binding.btnSignup.setOnClickListener {
            createUser()
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
    private fun createUser() {
        val profile = binding.ivProfile.toString()
        val nickname = binding.titNickname.text.toString()
        val email = binding.titEmail.text.toString()
        val password = binding.titPassword.text.toString()
        //val passwordCheck = binding.titPasswordCheck.text.toString()

        // 빈칸 확인
        if (nickname.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            // auth 회원가입
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        // firestore DB에 저장
                        val userDto = UserDto(/*user!!.uid,*/profile, nickname, email, password)
                        db.collection("UserDto").document(currentUser!!.uid)
                            .set(userDto)
                            .addOnSuccessListener { documentReference ->
                            }
                            .addOnFailureListener { e ->
                            }
                        showToast(this, "회원가입 성공 & 자동로그인")
                        navigateToMainActivity(this)
                        finish()
                    }
                }
        } else {
            // If sign in fails, display a message to the user.
            showToast(this, "빈칸을 다 채우세요")
        }
    }
}