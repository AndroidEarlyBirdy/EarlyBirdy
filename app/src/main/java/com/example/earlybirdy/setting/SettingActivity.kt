package com.example.earlybirdy.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUserSupport.setOnClickListener {
            navigateToDetail("고객 지원", "고객 지원 내용")
        }

        binding.btnOpenLicense.setOnClickListener {
            navigateToDetail("오픈 라이선스", "오픈 라이선스")
        }

        binding.btnGenralCondition.setOnClickListener {

        }


    }

    private fun navigateToDetail(title: String, content: String) {
        val intent = Intent(this, SettingDetailActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        startActivity(intent)

    }

    }



