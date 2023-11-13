package com.nbcproject.earlybirdy.board.board_read

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BoardReadActivity : MainActivity() {
    private val binding by lazy { ActivityBoardReadBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val db = Firebase.firestore
    private val fireStore = FirebaseFirestore.getInstance()
    private var nickname: String? = ""
    private val cdata: MutableList<CommentDto> = mutableListOf()
    private val commentAdapter by lazy {
        CommentAdapter()
    }
    private lateinit var cmanager: LinearLayoutManager

    companion object {
        //나중에 위치 받아서 값 설정하기
        // 위치 사용될 변수
        lateinit var BoardData: BoardDto
        fun boardReadIntent(context: Context?, boardData: BoardDto) =
            Intent(context, BoardReadActivity::class.java).apply {
                BoardData = boardData
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        getUserNicknameData()

        setOnClickListener()
        readBoard()
        initCommentView() // 댓글

    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvUpdate.setOnClickListener {
            if (auth.currentUser?.uid == BoardData.uid) {
                val boardWriteIntent = Intent(this, BoardWriteActivity::class.java)
                boardWriteIntent.putExtra("boardType", 2)
                startActivity(boardWriteIntent)
                finish()
            } else {
                showToast(this, getString(R.string.edit_board_error))
            }
        }

        binding.tvDelete.setOnClickListener {
            deleteBoardData(BoardData)
        }

        binding.tvClaim.setOnClickListener {
            intentToGeneralConditionLink()
        }

        binding.tvAddComment.setOnClickListener {
            writeComment()
            binding.etComment.text = null
        }

        binding.tvLoadComment.setOnClickListener {
            initCommentView()
        }

    }

    // 신고하기 구글폼으로 이동
    private fun intentToGeneralConditionLink() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.claimUrl))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(this, getString(R.string.setting_connect_error_toast))
        }
    }

    // 게시글 읽기
    private fun readBoard() = with(binding) {

        val dateFormat = SimpleDateFormat("yy.MM.dd.HH.mm.ss", Locale.getDefault())

        tvNickname.text = BoardData.writer
        etContentsTitle.text = BoardData.contentsTitle
        tvCreatedDatetime.text = BoardData.createdTime?.toDate()?.let { dateFormat.format(it) }
        etContents.text = BoardData.contents
        if (BoardData.contentsPhoto != null) {
            Glide.with(this@BoardReadActivity).load(BoardData.contentsPhoto).error(R.drawable.ic_logo)
                .into(binding.ivPicture)
        }else{
            binding.ivPicture.visibility = View.GONE
        }


    }

    // 게시글 삭제
    private fun deleteBoardData(boardData: BoardDto) {
        val user = auth.currentUser
        if (user!!.uid == boardData.uid) {
            fireStore.collection("BoardDto").document(boardData.bid).delete()
                .addOnSuccessListener {
                    fireStore.collection("BoardDto").document(BoardData.bid)
                        .collection("CommentDto")
                        .document().delete()
                        .addOnSuccessListener {
                            finish()
                        }
                }.addOnFailureListener {
                    showToast(this, getString(R.string.delete_board_error))
                }
        } else {
            showToast(this, getString(R.string.delete_board_user_error))
        }
    }

    // 댓글 보기
    private fun initCommentView() = with(binding) {

        loadCommentData()

        cmanager = LinearLayoutManager(this@BoardReadActivity, LinearLayoutManager.VERTICAL, false)
        rvComment.layoutManager = cmanager
        rvComment.adapter = commentAdapter

        rvComment.isNestedScrollingEnabled = false

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
            showToast(this, getString(R.string.delete_reply_user_error))
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
                    }
                }
                cdata.sortBy { it.commentTime } // 날짜순 정렬
                commentAdapter.addItems(cdata)
            }
    }

    // 댓글에 들어갈 작성자 닉네임 불러오기
    private fun getUserNicknameData() {
        val user = auth.currentUser
        fireStore.collection("UserDto").document(user!!.uid).addSnapshotListener { value, _ ->
            if (value != null) {
                nickname = value.getString("nickname")
            }
        }
    }

    // 댓글 저장
    private fun writeComment() {
        val user = auth.currentUser

        var commentIndex = UUID.randomUUID().toString()
        val commentTime = Timestamp(Date())

        val comments = binding.etComment.text.toString()

        if (comments.isEmpty()) {
            binding.etComment.error = getString(R.string.reply_blank_error)
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
        }
    }
}