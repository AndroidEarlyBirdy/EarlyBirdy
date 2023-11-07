package com.nbcproject.earlybirdy.edit_profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.nbcproject.earlybirdy.databinding.ActivityEditProfileDialogBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.navigateToResetPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivityDialog (
    context: Context,
) : Dialog(context) {
    private lateinit var binding: ActivityEditProfileDialogBinding
    private lateinit var user: FirebaseUser
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnCancelListener {
            val intent = Intent(context, MainActivity::class.java).apply{
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }

        initViews()
    }

    private fun initViews() = with(binding) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            loadUserData(user.uid)
            binding.btnEpdSignin.setOnClickListener {
                checkAuth(user.email ?: "", binding.titEpdPassword.text.toString())
            }
        } else {
            dismiss()
            Toast.makeText(context,"로그인을 해주세요!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData(uid: String) {

        db.collection("UserDto").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val loadEmail = document.getString("email") ?: ""
                    binding.titEpdEmail.setText(loadEmail)
                }
            }.addOnFailureListener{
                Log.e("loadUserData", "Failed to load user data", it)
            }
    }
    private fun checkAuth(email:String,password:String) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 인증 성공: 입력한 이메일과 비밀번호가 일치함
                            val user = FirebaseAuth.getInstance().currentUser
                            Toast.makeText(context, "비밀번호가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("login", "success")
                            navigateToResetPassword(context)
                            dismiss()
                        } else {
                            // 인증 실패: 입력한 이메일과 비밀번호가 일치하지 않음
                            val exception = task.exception
                            if (exception is FirebaseAuthInvalidCredentialsException) {
                                // 잘못된 자격 증명 오류 처리
                                Toast.makeText(
                                    context,
                                    "이메일 또는 비밀번호가 올바르지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("log", "잘못된 자격 증명 오류 처리")
                                context.startActivity(Intent(context, MainActivity::class.java))


                            } else {
                                Toast.makeText(context, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                Log.d("log", "그 외")
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }
                        }
                    }

            }
    }
