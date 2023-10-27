package com.example.earlybirdy.resetpassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.earlybirdy.databinding.ActivityResetPasswordBinding
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.signup.EditProfileDialog
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSignupActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : MainActivity() {
    private val binding by lazy { ActivityResetPasswordBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var editProfileDialog: EditProfileDialog
    private lateinit var database: DatabaseReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        user = auth.currentUser!!
        fireStore = FirebaseFirestore.getInstance()
        editProfileDialog = EditProfileDialog(this@ResetPasswordActivity)

        // 비밀번호 확인 함수
        binding.titPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.titPassword.text.toString()
                        .equals(binding.titPasswordCheck.text.toString())
                ) {
                    binding.tilPasswordCheck.error = "비밀번호가 일치합니다."
                } else {
                    binding.tilPasswordCheck.error = "비밀번호가 일치하지 않습니다."
                }
            }
        })

        binding.btnReset.setOnClickListener {
            onSaveButtonClick()
        }

        // 나가기 = 앱 종료
        binding.tvFinish.setOnClickListener {
            finish()
        }

        // 회원가입 페이지로 이동
        binding.tvBtnSignup.setOnClickListener {
            //val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${}"))
            navigateToSignupActivity(this)
        }
    }

    private fun onSaveButtonClick() {
        val password = binding.titPassword.text.toString()
        val passwordCheck = binding.titPasswordCheck.text.toString()



        // 빈칸 확인
        if (password.isBlank()) {
            binding.tilPassword.error = "비밀번호를 입력해주세요"
        } else if (!password.equals(passwordCheck)) {
            binding.tilPasswordCheck.error = "일치하지 않습니다"
        }

        changePassword()
        navigateToMainActivity(this)
        showToast(this@ResetPasswordActivity,"비밀번호 수정 완료!")

        finish()

    }


    private fun changePassword() {
        val user = Firebase.auth.currentUser!!
        val newPassword = binding.titPassword.text.toString()
        val credential = EmailAuthProvider
            .getCredential("user@example.com", "password123")
        user.reauthenticate(credential)
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("password", "User password updated.")
                }
            }
            .addOnFailureListener {
                showToast(this, "비밀번호변경에 실패하였습니다.")
                Log.e("fail","${it}")
            }
    }
}