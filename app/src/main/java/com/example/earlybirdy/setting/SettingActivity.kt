package com.example.earlybirdy.setting

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            SettingItem("고객 지원"),
            SettingItem("오픈 라이선스"),
            SettingItem("약관"),
            SettingItem("로그아웃"),
            SettingItem("회원 탈퇴")
        )

        val adapter = SettingAdapter(items) { item ->

            Toast.makeText(this, "${item.title}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }
}
