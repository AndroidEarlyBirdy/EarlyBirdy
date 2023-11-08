package com.nbcproject.earlybirdy.setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.nbcproject.earlybirdy.databinding.ActivitySettingLoginDialogBinding
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingCheckUserDialog(
    context: Context,
) : Dialog(context) {
    private lateinit var binding: ActivitySettingLoginDialogBinding
    private val auth = FirebaseAuth.getInstance()

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingLoginDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnCancelListener {
            val intent = Intent(context, SettingActivity::class.java).apply {
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
            binding.btnSldSignin.setOnClickListener {
                checkAuth(user.email ?: "", binding.titSldPassword.text.toString())
            }
        } else {
            dismiss()
            Toast.makeText(context, "로그인을 해주세요!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData(uid: String) {

        db.collection("UserDto").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val loadEmail = document.getString("email") ?: ""
                    binding.titSldEmail.setText(loadEmail)
                }
            }.addOnFailureListener {
                Log.e("loadUserData", "Failed to load user data", it)
            }
    }

    //    private fun checkAuth(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // 인증 성공: 입력한 이메일과 비밀번호가 일치함
//                    val user = FirebaseAuth.getInstance().currentUser
//                    Toast.makeText(context, "비밀번호가 확인되었습니다.", Toast.LENGTH_SHORT).show()
//                    deleteUser()
//
//                } else {
//                    // 인증 실패: 입력한 이메일과 비밀번호가 일치하지 않음
//                    val exception = task.exception
//                    if (exception is FirebaseAuthInvalidCredentialsException) { // 비밀번호 틀릭때의 예외가 아님
//                        // 잘못된 자격 증명 오류 처리
//                        binding.tilSldPassword.error = "비밀번호가 틀립니다."
//                        Log.d("log", "잘못된 자격 증명 오류 처리")
//                    } else {
//                        binding.tilSldPassword.error = "인증에 실패하였습니다"
//                        Log.d("log", "그 외")
//                    }
//                }
//            }
//    }
    private fun checkAuth(email: String, password: String) {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(email, password)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "재인증에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    deleteUser()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        // Handle invalid credentials error
                        binding.tilSldPassword.error = "비밀번호가 틀립니다."
                    } else {
                        binding.tilSldPassword.error = "재인증에 실패했습니다."
                    }
                }
            }
    }


    private fun deleteUser() {
        val user = auth.currentUser
        Log.d("delete3", "${user}")
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("delete4", "${user.uid}")
                    db.collection("UserDto").document(user.uid)
                        .delete()
                        .addOnSuccessListener {
                            navigateToSigninActivity(context)
                            dismiss()
                            Log.d("delete1", "${user}")
                        }
                        .addOnFailureListener { e ->
                            Log.d("delete2", "${user}")
                        }

                }
            }?.addOnFailureListener {

            }
    }
}