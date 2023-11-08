package com.nbcproject.earlybirdy.setting.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivitySettingDeleteDialogBinding
import com.nbcproject.earlybirdy.setting.viewmodel.SettingViewModel

class SettingDeleteDialog(private val context: Context, private val settingViewModel: SettingViewModel) : Dialog(context, R.style.Theme_TransparentBackground) {

    private lateinit var binding: ActivitySettingDeleteDialogBinding
    private lateinit var settingLoginDialog: SettingCheckUserDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingDeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingLoginDialog = SettingCheckUserDialog(context, settingViewModel)
        setOnclickListeners()
    }

    private fun setOnclickListeners() = with(binding) {
        btnSddOk.setOnClickListener {
            settingLoginDialog.show()
            dismiss()
        }

        btnSddCancel.setOnClickListener {
            dismiss()
        }
    }
}
