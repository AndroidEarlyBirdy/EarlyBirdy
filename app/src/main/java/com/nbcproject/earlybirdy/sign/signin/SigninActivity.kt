package com.nbcproject.earlybirdy.sign.signin

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.nbcproject.earlybirdy.databinding.ActivitySigninBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.sealedclass.SigninNavigation
import com.nbcproject.earlybirdy.sign.signin.viewmodel.SigninViewModel
import com.nbcproject.earlybirdy.util.navigateToMainActivity
import com.nbcproject.earlybirdy.util.navigateToSendEmailActivity
import com.nbcproject.earlybirdy.util.navigateToSignupActivity
import com.nbcproject.earlybirdy.util.showToast

class SigninActivity : MainActivity() {

    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }
    private val viewModel: SigninViewModel by viewModels()

    private val MY_PERMISSION_ACCESS_ALL = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeData()
        setOnClickListener()
        askPermissions()

    }

    private fun observeData(){
        viewModel.signinState.observe(this) {
            when (it) {
//                is SigninNavigation.AlreadySignin -> {
//                    navigateToMainActivity(this)
//                }

                is SigninNavigation.EmptyEmail -> {
                    binding.tilEmail.error = "이메일을 입력해주세요"
                }

                is SigninNavigation.EmptyPassword -> {
                    binding.tilPassword.error = "비밀번호를 입력해주세요"
                }

                is SigninNavigation.SigninSuccess -> {
                    showToast(this, "로그인에 성공하였습니다")
                    navigateToMainActivity(this)
                    finish()
                }

                is SigninNavigation.SigninFailed -> {
                    showToast(this, "로그인에 실패하였습니다")
                }
            }
        }
    }

    private fun setOnClickListener() {
        // 비밀번호를 잊으셨나요?
        binding.tvBtnResetPassword.setOnClickListener {
            navigateToSendEmailActivity(this)
            finish()
        }

        //로그인
        binding.btnSignin.setOnClickListener {
            onStart()
        }

        binding.btnSigninGoogle.setOnClickListener {
            navigateToMainActivity(this)
        }

        // 나가기 = 앱 종료
        binding.tvFinish.setOnClickListener {
            moveTaskToBack(true)
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        // 회원가입 페이지로 이동
        binding.tvBtnSignup.setOnClickListener {
            //val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${}"))
            navigateToSignupActivity(this)
            finish()
        }
    }

    private fun askPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("종료 확인")
            .setMessage("앱을 종료하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                moveTaskToBack(true)
                finish()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            .setNegativeButton("아니오") { _, _ ->
            }
            .create()
            .show()
    }

    //로그인 함수
    public override fun onStart() {
        super.onStart()
        val email = binding.titEmail.text.toString()
        val password = binding.titPassword.text.toString()

        viewModel.signin(email, password)
    }
}