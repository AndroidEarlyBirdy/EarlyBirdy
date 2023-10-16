package com.example.earlybirdy.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivitySettingBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}