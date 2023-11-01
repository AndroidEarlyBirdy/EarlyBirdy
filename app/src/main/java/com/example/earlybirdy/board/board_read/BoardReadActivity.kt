package com.example.earlybirdy.board.board_read

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.earlybirdy.board.board_write.BoardWriteActivity
import com.example.earlybirdy.databinding.ActivityBoardReadBinding
import com.example.earlybirdy.dto.BoardDto

class BoardReadActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBoardReadBinding.inflate(layoutInflater) }

    companion object {
        //나중에 위치 받아서 값 설정하기
        // 위치 사용될 변수
        lateinit var BoardData: BoardDto
        fun BoardReadIntent(context: Context?, boardData: BoardDto) =
            Intent(context, BoardReadActivity::class.java).apply {
                BoardData = boardData
            }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setOnClickListener()
        readBoard()
    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvUpdate.setOnClickListener { // 삭제와 수정 둘 다 uid 검증 필요
            val boardWriteIntent = Intent(this, BoardWriteActivity::class.java)
            boardWriteIntent.putExtra("boardType", 2)
            startActivity(boardWriteIntent)
            finish()
        }
    }

    private fun readBoard() {
        binding.tvNickname.text = BoardData.writer
        binding.etContentsTitle.text = BoardData.contentsTitle
        binding.etContents.text = BoardData.contents
    }
}