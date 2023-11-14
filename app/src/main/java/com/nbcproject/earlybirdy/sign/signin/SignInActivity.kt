package com.nbcproject.earlybirdy.sign.signin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivitySigninBinding
import com.nbcproject.earlybirdy.home.HomeFragment
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.sealedclass.SignInNavigation
import com.nbcproject.earlybirdy.sign.signin.viewmodel.SignInViewModel
import com.nbcproject.earlybirdy.sign.signin.viewmodel.SignInViewModelFactory
import com.nbcproject.earlybirdy.util.navigateToMainActivity
import com.nbcproject.earlybirdy.util.navigateToSendEmailActivity
import com.nbcproject.earlybirdy.util.navigateToSignupActivity
import com.nbcproject.earlybirdy.util.showToast

class SignInActivity : MainActivity() {

    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }
    private lateinit var signInViewModel: SignInViewModel
    private val MY_PERMISSION_ACCESS_ALL = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        signInViewModel =
            ViewModelProvider(this, SignInViewModelFactory())[SignInViewModel::class.java]

        observeData()
        setOnClickListener()
        askPermissions()
    }

    private fun observeData() {
        signInViewModel.signInState.observe(this) {
            when (it) {
                is SignInNavigation.SignInSuccess -> {
                    navigateToMainActivity(this)
                }

                is SignInNavigation.SignInFailed -> {
                    showToast(this, getString(R.string.signIn_error_failedSign))
                }
            }
        }
    }

    private fun setOnClickListener() {
        // 비밀번호를 잊으셨나요?
        binding.tvBtnResetPassword.setOnClickListener {
            navigateToSendEmailActivity(this)
        }

        //로그인
        binding.btnSignin.setOnClickListener {
            onStart()
        }

        // 나가기 = 앱 종료
        binding.tvFinish.setOnClickListener {
            moveTaskToBack(true)
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        // 회원가입 페이지로 이동
        binding.tvBtnSignup.setOnClickListener {
            navigateToSignupActivity(this)
        }
    }

    private fun askPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.signIn_tv_exitTitle))
            .setMessage(getString(R.string.signIn_tv_exitContent))
            .setPositiveButton(getString(R.string.signIn_tv_exitDone)) { _, _ ->
                moveTaskToBack(true)
                finish()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            .setNegativeButton(R.string.signIn_tv_exitCancel) { _, _ ->
            }
            .create()
            .show()
    }

    //로그인 함수
    public override fun onStart() {
        super.onStart()
        val email = binding.titEmail.text.toString()
        val password = binding.titPassword.text.toString()

        if (email.isEmpty()) { // 이메일 입력 여부
            binding.tilEmail.error = getString(R.string.util_error_emptyEmail)
        } else if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.util_error_emptyPassword)
        } else {
            signInViewModel.signIn(email, password)
        }
    }
}