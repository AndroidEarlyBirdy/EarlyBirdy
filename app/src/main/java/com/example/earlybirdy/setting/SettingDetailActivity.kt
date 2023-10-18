package com.example.earlybirdy.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivityDetailBinding

class SettingDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

       initView()
    }

    private fun initView() {
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")

        binding.tvItemTitle.text = title
        binding.tvDetial.text = content

        binding.btnBack.setOnClickListener {
            finish()

        }
    }


}

