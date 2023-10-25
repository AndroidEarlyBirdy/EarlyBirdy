package com.example.earlybirdy.setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.earlybirdy.databinding.ActivitySettingDeleteDialogBinding

class SettingDeleteDialog(
    context: Context
) : Dialog(context) {
    private lateinit var binding: ActivitySettingDeleteDialogBinding
    private lateinit var settingLoginDialog: SettingCheckUserDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingLoginDialog = SettingCheckUserDialog(context)
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
