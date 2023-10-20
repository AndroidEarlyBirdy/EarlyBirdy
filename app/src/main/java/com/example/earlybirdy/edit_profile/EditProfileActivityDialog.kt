package com.example.earlybirdy.edit_profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.earlybirdy.databinding.ActivityEditProfileDialogBinding
import com.example.earlybirdy.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class EditProfileActivityDialog (
    context: Context,
) : Dialog(context) {
    private lateinit var binding: ActivityEditProfileDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        binding.btnEpdSignin.setOnClickListener{
            checkAuth()
        }

    }
    private fun checkAuth() {
        val email = binding.titEpdEmail.text.toString()
        val password = binding.titEpdPassword.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 인증 성공: 입력한 이메일과 비밀번호가 일치함
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(context, "비밀번호가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("login","success")
                    dismiss()
                } else {
                    // 인증 실패: 입력한 이메일과 비밀번호가 일치하지 않음
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        // 잘못된 자격 증명 오류 처리
                        Toast.makeText(context, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("log","잘못된 자격 증명 오류 처리")
                        context.startActivity(Intent(context, MainActivity::class.java))



                    } else {
                        Toast.makeText(context, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("log","그 외")
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            }

    }

}