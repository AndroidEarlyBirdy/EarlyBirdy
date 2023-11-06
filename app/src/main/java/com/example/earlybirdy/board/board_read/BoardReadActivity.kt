package com.example.earlybirdy.board.board_read

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.board.board_main.BoardAdapter
import com.example.earlybirdy.board.board_write.BoardWriteActivity
import com.example.earlybirdy.databinding.ActivityBoardReadBinding
import com.example.earlybirdy.dto.BoardDto
import com.example.earlybirdy.dto.CommentDto
import com.example.earlybirdy.util.showToast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date
import java.util.UUID

class BoardReadActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBoardReadBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore
    private val fireStore = FirebaseFirestore.getInstance()

    private var nickname: String? = ""

    private val cdata: MutableList<CommentDto> = mutableListOf()

    private val commentAdapter by lazy {
        CommentAdapter(this)
    }

    //private lateinit var cContext: Context
    private lateinit var cmanager: LinearLayoutManager

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

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        setOnClickListener()
        readBoard()
        initCommentView() // 댓글

    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        if (auth.currentUser!!.uid == BoardData.uid) { // 삭제와 수정 둘 다 uid 검증 필요
            binding.tvUpdate.setOnClickListener {
                val boardWriteIntent = Intent(this, BoardWriteActivity::class.java)
                boardWriteIntent.putExtra("boardType", 2)
                startActivity(boardWriteIntent)
                finish()
            }

            binding.tvDelete.setOnClickListener {
                //deleteData(BoardData)
            }
        } else {
            showToast(this, "작성자만 내용 수정이 가능합니다.")
        }

        binding.tvAddComment.setOnClickListener {
            writeComment()
        }

        binding.tvLoadComment.setOnClickListener {
            initCommentView()
        }

    }

    // 게시글 읽기
    private fun readBoard() = with(binding) {
        tvNickname.text = BoardData.writer
        etContentsTitle.text = BoardData.contentsTitle
        etContents.text = BoardData.contents
        ivPicture.setImageURI(Uri.parse(BoardData.contentsPoto))
    }

    // 게시글 삭제
    private fun deleteBoardData(boardData: BoardDto) {
        val user = auth.currentUser
        if (user!!.uid == boardData.uid){
            fireStore.collection("BoardDto").document(boardData.bid).delete()
                .addOnSuccessListener {
                }
        }
    }

    // 댓글 보기
    private fun initCommentView() = with(binding) {

        loadCommentData()

        cmanager = LinearLayoutManager(this@BoardReadActivity, LinearLayoutManager.VERTICAL, false)
        rvComment.layoutManager = cmanager
        rvComment.adapter = commentAdapter

        commentAdapter.itemClick = object : CommentAdapter.ItemClick {
            override fun deleteItem(view: View, commentData: CommentDto) {
                deleteCommentData(commentData)
                loadCommentData()
            }
        }

    }

    // 댓글 삭제
    private fun deleteCommentData(commentData: CommentDto) {
        val user = auth.currentUser
        if (user!!.uid == commentData.uid){
            fireStore.collection("BoardDto").document(BoardData.bid).collection("CommentDto").document(commentData.cid).delete()
                .addOnSuccessListener {
                }
        }
    }

    // 댓글 저장
    private fun loadCommentData() {
        fireStore.collection("Comment").get()
            .addOnSuccessListener { value ->
                commentAdapter.clearList()
                cdata.clear()
                for (i in value!!.documents) {
                    var citem = i.toObject(CommentDto::class.java)
                    if (citem != null) {
                        val commentItam = CommentDto(
                            citem.cid,
                            citem.uid,
                            citem.writer,
                            citem.commentTime,
                            citem.comments
                        )
                        cdata.add(commentItam)
                        Log.d("comment", commentItam.toString())
                    }
                }
                commentAdapter.addItems(cdata)
            }
    }

    fun getUserNicknameData() {
        var user = auth.currentUser
        fireStore.collection("UserDto").document(user!!.uid).addSnapshotListener { value, _ ->
            if (value != null) {
                nickname = value.getString("nickname")
            }
            binding.tvWriter.text = nickname
        }
    }

    private fun writeComment() {
        val user = auth.currentUser

        var commentIndex = UUID.randomUUID().toString()
        val commentTime = Timestamp(Date())

        val comments = binding.etComment.text.toString()

        if (comments.isEmpty()) {
            binding.etComment.error = "댓글을 입력해주세요"
        } else {
            val commentDto =
                CommentDto(
                    commentIndex,
                    user!!.uid,
                    nickname!!,
                    commentTime,
                    comments
                )
            db.collection("BoardDto").document(BoardData.bid).collection("CommentDto").document(commentIndex)
                .set(commentDto)
                .addOnSuccessListener { documentReference ->
                    //binding.etComment.
                }
                .addOnFailureListener { e ->
                }
        }
    }
}