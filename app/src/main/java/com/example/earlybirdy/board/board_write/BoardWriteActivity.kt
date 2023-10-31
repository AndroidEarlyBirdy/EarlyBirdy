package com.example.earlybirdy.board.board_write

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.earlybirdy.board.board_read.BoardReadActivity
import com.example.earlybirdy.databinding.ActivityBoardWriteBinding
import com.example.earlybirdy.dto.BoardDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class BoardWriteActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBoardWriteBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore

    private val fireStore = FirebaseFirestore.getInstance()

    private var nickname: String? = ""

    companion object {
        //나중에 위치 받아서 값 설정하기
        // 위치 사용될 변수
        lateinit var BoardData: BoardDto
        fun BoardReadIntent(context: Context?, boardData: BoardDto) =
            Intent(context, BoardWriteActivity::class.java).apply {
                BoardData = boardData
            }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        readBoard()

        setOnClickListener()
        getUserNicknameData()
    }

    // 클릭 리스너 함수
    private fun setOnClickListener() {
        binding.tvCancel.setOnClickListener {
            finish()
        }

        binding.tvSave.setOnClickListener {
            createdContents()
        }
    }

    fun getUserNicknameData() {
        var user = auth.currentUser
        fireStore.collection("UserDto").document(user!!.uid).addSnapshotListener { value, _ ->
            if (value != null) {
                nickname = value.getString("nickname")
            }
            binding.tvNickname.text = nickname
        }
    }


    private fun createdContents() {
        val user = auth.currentUser

        var boardIndex = UUID.randomUUID().toString()

        val contentsTitle = binding.etContentsTitle.text.toString()
        val contents = binding.etContents.text.toString()

        if (contentsTitle.isEmpty()) {
            binding.etContentsTitle.error = "제목을 입력해주세요"
        } else if (contents.isEmpty()) {
            binding.etContents.error = "내용을 입력해주세요"
        } else {
            val boardDto =
                BoardDto(boardIndex, user!!.uid, nickname!!, contentsTitle, contents)
            db.collection("BoardDto").document(boardIndex)
                .set(boardDto)
                .addOnSuccessListener { documentReference ->
                    Log.d("boardDto", "${boardDto}")
                    finish()
                }
                .addOnFailureListener { e ->
                }
        }

    }

    private fun readBoard() {
        binding.tvNickname.text = BoardReadActivity.BoardData.writer
        binding.etContentsTitle.setText(BoardReadActivity.BoardData.contentsTitle)
        binding.etContents.setText(BoardReadActivity.BoardData.contents)
    }

//    private fun modifyContents() {
//        val user = auth.currentUser
//
//        db.collection("BoardDto").document().get()
//            .addOnSuccessListener {
//
//            }
//        val contentsTitle = binding.etContentsTitle.text.toString()
//        val contents = binding.etContents.text.toString()
//
//        if (contentsTitle.isEmpty()) {
//            binding.etContentsTitle.error = "제목을 입력해주세요"
//        } else if (contents.isEmpty()) {
//            binding.etContents.error = "내용을 입력해주세요"
//        } else {
//            val boardDto =
//                BoardDto(boardIndex, user!!.uid, nickname!!, contentsTitle, contents)
//            db.collection("BoardDto").document(boardIndex)
//                .set(boardDto)
//                .addOnSuccessListener { documentReference ->
//                    Log.d("boardDto", "${boardDto}")
//                    finish()
//                }
//                .addOnFailureListener { e ->
//                }
//        }
//    }
}