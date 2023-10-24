package com.example.earlybirdy.setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.earlybirdy.databinding.ActivitySettingDeleteDialogBinding
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingDeleteDialog(
    context: Context
) : Dialog(context) {
    private lateinit var binding: ActivitySettingDeleteDialogBinding
    private lateinit var settingLoginDialog: SettingLoginDialog
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingLoginDialog = SettingLoginDialog(context)
        setOnCancelListener {
            val intent = Intent(context, SettingActivity::class.java).apply{
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }

        initViews()
    }

    private fun initViews() {
        binding.btnSddOk.setOnClickListener {
            settingLoginDialog.show()
            deleteUser()
            dismiss()
        }

        binding.btnSddCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun deleteUser() {
        val user = Firebase.auth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("UserDto").document(user.uid)
                        .delete().addOnSuccessListener {
                            showToast(context, "회원탈퇴 되었습니다. 로그인 페이지로 이동합니다.")
                            navigateToSigninActivity(context)
                        }
                        .addOnFailureListener { e ->
                            Log.e("fail","Error", e)
                        }

                }
            }

    }

}
