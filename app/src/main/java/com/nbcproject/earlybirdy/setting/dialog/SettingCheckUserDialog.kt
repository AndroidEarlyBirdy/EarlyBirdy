package com.nbcproject.earlybirdy.setting.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.nbcproject.earlybirdy.databinding.ActivitySettingLoginDialogBinding
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.google.firebase.auth.FirebaseAuth
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.sealedclass.CheckAuth
import com.nbcproject.earlybirdy.sealedclass.CheckDelete
import com.nbcproject.earlybirdy.setting.viewmodel.SettingViewModel

class SettingCheckUserDialog(
    private val context: Context,
    private val settingViewModel: SettingViewModel
) : Dialog(context) {

    private lateinit var binding: ActivitySettingLoginDialogBinding
    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

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
        settingViewModel.checkAuth.observe(context as LifecycleOwner) {
            when (it) {
                is CheckAuth.SuccessAuth -> {
                    deleteUser()
                }
                is CheckAuth.ElseException -> {
                    binding.tilSldPassword.error = context.getString(R.string.setting_auth_fail_else)
                }
            }
        }
        settingViewModel.checkDeleteAuth.observe(context as LifecycleOwner) {
            when (it) {
                is CheckDelete.DeleteSuccess -> {
                    user?.let { it1 -> settingViewModel.checkDeleteData(it1.uid) }
                }
            }
        }
        settingViewModel.checkDeleteData.observe(context as LifecycleOwner) {
            when (it) {
                is CheckDelete.DeleteSuccess -> {
                    navigateToSigninActivity(context)
                    dismiss()
                }
            }
        }
    }

    private fun loadUserData(uid: String) {
        settingViewModel.getUserEmail(uid)
    }

    private fun checkAuth(email: String, password: String) {
        settingViewModel.checkAuth(email, password)
    }

    private fun deleteUser() {
        settingViewModel.checkDeleteAuth()
    }
}