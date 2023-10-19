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
        val content = getContentByTitle(title)

        binding.tvItemTitle.text = title
        binding.tvDetial.text = content

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getContentByTitle(title: String?): String {
        return when (title) {
            "고객 지원" -> "고객센터입니다. 문의는 123@asd.com으로 주세요."
            "오픈 라이선스" -> "오픈 라이선스에 대한 내용입니다."
            else -> "약관에 대한 내용입니다"
        }
    }
}


