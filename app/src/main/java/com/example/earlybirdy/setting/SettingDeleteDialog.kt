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
            dismiss()
        }

        binding.btnSddCancel.setOnClickListener {
            dismiss()
        }
    }



}
