package com.nbcproject.earlybirdy.resetpassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.edit_profile.dialog.EditProfileDialog
import com.nbcproject.earlybirdy.util.navigateToEditProfileActivity
import com.nbcproject.earlybirdy.util.navigateToMainActivity
import com.nbcproject.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivityResetPasswordBinding
import com.nbcproject.earlybirdy.resetpassword.viewmodel.ResetPasswordViewModel
import com.nbcproject.earlybirdy.resetpassword.viewmodel.ResetPasswordViewModelFactory
import com.nbcproject.earlybirdy.sealedclass.CheckChangePassword

class ResetPasswordActivity : MainActivity() {

    private val binding by lazy { ActivityResetPasswordBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private lateinit var editProfileDialog: EditProfileDialog
    private lateinit var user: FirebaseUser
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        resetPasswordViewModel = ViewModelProvider(this, ResetPasswordViewModelFactory())[ResetPasswordViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        editProfileDialog = EditProfileDialog(this@ResetPasswordActivity)

        observeData()
        initView()
    }

    private fun initView() = with(binding) {
        btnReset.setOnClickListener {
            onSaveButtonClick()
        }

        icRpBack.setOnClickListener {
            finish()
        }

        titPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.titPassword.text.toString() == binding.titPasswordCheck.text.toString()
                ) {
                    binding.tilPasswordCheck.error =
                        getString(R.string.edittext_match_password)
                } else {
                    binding.tilPasswordCheck.error =
                        getString(R.string.edittext_unmatch_password)
                }
            }
        })
    }

    private fun observeData() {
        resetPasswordViewModel.changePasswordState.observe(this@ResetPasswordActivity) {
            when(it) {
                is CheckChangePassword.ChangeFailed -> {
                    showToast(this, getString(R.string.reset_password_fail))
                }
            }
        }
    }

    private fun onSaveButtonClick() {
        val password = binding.titPassword.text.toString()
        val passwordCheck = binding.titPasswordCheck.text.toString()

        // 빈칸 확인
        if (password.isBlank()) {
            binding.tilPassword.error = getString(R.string.reset_password_enter_password)
        } else if (password != passwordCheck) {
            binding.tilPasswordCheck.error = getString(R.string.edittext_unmatch_password)
        }
        else {
            changePassword()
            navigateToMainActivity(this)
        }
    }

    private fun changePassword() {
        val newPassword = binding.titPassword.text.toString()
        resetPasswordViewModel.changePassword(newPassword)
    }

    override fun onBackPressed() {
        finish()
    }
}