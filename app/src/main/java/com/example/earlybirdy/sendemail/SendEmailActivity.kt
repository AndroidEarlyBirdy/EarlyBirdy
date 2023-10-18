package com.example.earlybirdy.sendemail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.earlybirdy.databinding.ActivitySendEmailBinding
import com.example.earlybirdy.databinding.ActivitySigninBinding
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.navigateToSignupActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SendEmailActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySendEmailBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.btnSend.setOnClickListener {
            onStart()
            finish()
            navigateToSigninActivity(this)
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

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
        if (user != null) {
            //navigateToMainActivity(this)
            showToast(this, "이미 로그인 중")
        } else {
            val emailAddress = binding.titSendEmail.text.toString()
            if (emailAddress.isNotBlank()) {
                Firebase.auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("send", "${emailAddress}")
                        }
                    }.addOnFailureListener {
                        // 유저 정보에 없는 이메일이면 보내지 않음
                    }
            }
        }
    }
}