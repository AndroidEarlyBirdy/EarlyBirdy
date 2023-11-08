package com.nbcproject.earlybirdy.setting.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.nbcproject.earlybirdy.databinding.ActivitySettingLoginDialogBinding
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nbcproject.earlybirdy.setting.viewmodel.SettingViewModel

class SettingCheckUserDialog(private val context: Context, private val settingViewModel: SettingViewModel) : Dialog(context) {

    private lateinit var binding: ActivitySettingLoginDialogBinding
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingLoginDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        observeData()
    }

    private fun initViews() = with(binding) {
        val user = auth.currentUser
        if (user != null) {
            loadUserData(user.uid)

            btnSldSignin.setOnClickListener {
                checkAuth(user.email ?: "", binding.titSldPassword.text.toString())
            }
        }
    }

    private fun observeData() {
        settingViewModel.userEmail.observe(context as LifecycleOwner) {
            binding.titSldEmail.setText(it)
        }
    }

    private fun loadUserData(uid: String) {
        settingViewModel.getUserEmail(uid)
    }

    private fun checkAuth(email: String, password: String) {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(email, password)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUser()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        binding.tilSldPassword.error = "비밀번호가 틀립니다."
                    } else {
                        binding.tilSldPassword.error = "재인증에 실패했습니다."
                    }
                }
            }
    }


    private fun deleteUser() {
        val user = auth.currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fireStore.collection("UserDto").document(user.uid)
                        .delete()
                        .addOnSuccessListener {
                            navigateToSigninActivity(context)
                            dismiss()
                        }

                }
            }
    }
}