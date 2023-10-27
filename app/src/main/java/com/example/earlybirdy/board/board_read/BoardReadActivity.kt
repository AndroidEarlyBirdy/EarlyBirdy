package com.example.earlybirdy.board.board_read

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityBoardReadBinding
import com.example.earlybirdy.databinding.ActivityBoardWriteBinding

class BoardReadActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBoardReadBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}