package com.nbcproject.earlybirdy.setting

import android.os.Bundle
import com.nbcproject.earlybirdy.databinding.ActivityDetailBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.Constants

class SettingDetailActivity : MainActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        val title = intent.getStringExtra(Constants.SETTING_TITLE_KEY)
        val content = getContentByTitle(title)

        binding.tvItemTitle.text = title
        binding.btnBoard.text = content

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getContentByTitle(title: String?): String {
        return when (title) {
            "오픈 라이선스" -> "오픈 라이선스에 대한 내용입니다."
            "약관" -> "약관에 대한 내용입니다"
            else -> "Title 전달 오류 발생"
        }
    }
}



