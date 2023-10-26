package com.example.earlybirdy.communitywrite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityCommunityWriteBinding

class CommunityWriteActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCommunityWriteBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setOnClickListener()
    }
    // 클릭 리스너 함수
    private fun setOnClickListener() {
        binding.tvCancel.setOnClickListener {
            finish()
        }


    }
}