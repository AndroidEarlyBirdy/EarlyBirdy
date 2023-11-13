package com.nbcproject.earlybirdy.edit_profile.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.nbcproject.earlybirdy.databinding.ActivityEditProfileDialogBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.navigateToResetPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nbcproject.earlybirdy.R

class EditProfileActivityDialog (
    context: Context,
) : Dialog(context, R.style.Theme_TransparentBackground) {
    private lateinit var binding: ActivityEditProfileDialogBinding
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
        }
        btnEpdSignin.setOnClickListener {
            if (titEpdPassword.text.isNullOrEmpty()) {
                tilEpdPassword.error = context.getString(R.string.error_password)
            }
            else {
                checkAuth(user?.email ?: "", titEpdPassword.text.toString())
            }
        }
    }

    private fun loadUserData(uid: String) {

        db.collection("UserDto").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val loadEmail = document.getString("email") ?: ""
                    binding.titEpdEmail.setText(loadEmail)
                }
            }
    }
    private fun checkAuth(email:String,password:String) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 인증 성공: 입력한 이메일과 비밀번호가 일치함
                            Toast.makeText(context, "비밀번호가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                            navigateToResetPassword(context)
                            dismiss()
                        } else {
                            // 인증 실패: 입력한 이메일과 비밀번호가 일치하지 않음
                                // 잘못된 자격 증명 오류 처리
                                Toast.makeText(
                                    context,
                                    "이메일 또는 비밀번호가 올바르지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dismiss()
                        }
                    }

            }
    }
