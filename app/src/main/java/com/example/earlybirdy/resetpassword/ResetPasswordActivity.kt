package com.example.earlybirdy.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityResetPasswordBinding
import com.example.earlybirdy.databinding.ActivitySendEmailBinding
import com.example.earlybirdy.databinding.ActivitySettingBinding
import com.example.earlybirdy.dto.UserDto
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSignupActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    private val binding by lazy { ActivityResetPasswordBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        // 비밀번호 확인 함수
        binding.titPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.titPassword.getText().toString()
                        .equals(binding.titPasswordCheck.getText().toString())
                ) {
                    binding.tilPasswordCheck.error = "비밀번호가 일치합니다"
                } else {
                    binding.tilPasswordCheck.error = "일치하지 않습니다"
                }
            }
        })

        binding.btnReset.setOnClickListener {
            onStart()
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
}