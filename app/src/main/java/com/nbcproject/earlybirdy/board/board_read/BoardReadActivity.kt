package com.nbcproject.earlybirdy.board.board_read

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nbcproject.earlybirdy.board.board_write.BoardWriteActivity
import com.nbcproject.earlybirdy.databinding.ActivityBoardReadBinding
import com.nbcproject.earlybirdy.dto.BoardDto
import com.nbcproject.earlybirdy.dto.CommentDto
import com.nbcproject.earlybirdy.util.showToast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    private val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

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

        binding.tvUpdate.setOnClickListener {
            if (auth.currentUser?.uid == BoardData.uid){
                val boardWriteIntent = Intent(this, BoardWriteActivity::class.java)
                boardWriteIntent.putExtra("boardType", 2)
                startActivity(boardWriteIntent)
                finish()
            }else{
                showToast(this, "작성자만 게시글을 수정할 수 있습니다")
            }
        }

        binding.tvDelete.setOnClickListener {
                deleteBoardData(BoardData)
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

        val dateFormat = SimpleDateFormat("yy.MM.dd.HH.mm.ss", Locale.getDefault())

        tvNickname.text = BoardData.writer
        etContentsTitle.text = BoardData.contentsTitle
        tvCreatedDatetime.text = BoardData.createdTime?.toDate()?.let { dateFormat.format(it) }
        etContents.text = BoardData.contents

        val imageRef = storageRef.child(BoardData.bid).child(BoardData.bid)

        imageRef.downloadUrl.addOnSuccessListener {
            Glide.with(this@BoardReadActivity)
                .load(it)
                .into(binding.ivPicture)
        }.addOnFailureListener {
            binding.ivPicture.visibility = View.GONE
        }
    }

    // 게시글 삭제
    private fun deleteBoardData(boardData: BoardDto) {
        val user = auth.currentUser
        if (user!!.uid == boardData.uid) {
            fireStore.collection("BoardDto").document(boardData.bid).delete()
                .addOnSuccessListener {
                    fireStore.collection("BoardDto").document(BoardData.bid).collection("CommentDto")
                        .document().delete()
                        .addOnSuccessListener {
                            showToast(this, "게시글이 삭제되었습니다.")
                            finish()
                        }
                }.addOnFailureListener {
                    showToast(this, "게시글 삭제에 실패했습니다.")

                }
        } else {
            showToast(this, "작성자만 게시글을 삭제할 수 있습니다")
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
        if (user!!.uid == commentData.uid) {
            fireStore.collection("BoardDto").document(BoardData.bid).collection("CommentDto")
                .document(commentData.cid).delete()
                .addOnSuccessListener {
                    cdata.remove(commentData)
                }
        } else {
            showToast(this, "댓글 작성자만 댓글을 삭제할 수 있습니다")
        }
    }

    // 댓글 불러오기
    private fun loadCommentData() {
        fireStore.collection("BoardDto").document(BoardData.bid).collection("CommentDto").get()
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
                cdata.sortBy { it.commentTime } // 날짜순 정렬
                commentAdapter.addItems(cdata)
            }
    }

    // 댓글에 들어갈 작성자 닉네임 불러오기
    fun getUserNicknameData() {
        var user = auth.currentUser
        fireStore.collection("UserDto").document(user!!.uid).addSnapshotListener { value, _ ->
            if (value != null) {
                nickname = value.getString("nickname")
            }
            binding.tvWriter.text = nickname
        }
    }

    // 댓글 저장
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
            db.collection("BoardDto").document(BoardData.bid).collection("CommentDto")
                .document(commentIndex)
                .set(commentDto)
                .addOnSuccessListener { documentReference ->
                }
                .addOnFailureListener { e ->
                }
        }
    }
}