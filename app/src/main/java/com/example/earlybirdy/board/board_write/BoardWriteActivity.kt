package com.example.earlybirdy.board.board_write

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.example.earlybirdy.R
import com.example.earlybirdy.board.board_read.BoardReadActivity
import com.example.earlybirdy.databinding.ActivityBoardWriteBinding
import com.example.earlybirdy.dto.BoardDto
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Date
import java.util.UUID

class BoardWriteActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBoardWriteBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var nickname: String? = ""
    private var contentsPoto: String? = null

    private lateinit var selectImgUri: Uri
    private val IMAGE_PICKER_REQUEST_CODE = 1

    private val boardType: Int by lazy {
        intent.getIntExtra("boardType", 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        binding.ivPicture


        if (boardType == 2) {
            readBoard()
        }

        setOnClickListener()
        getUserNicknameData()
    }

    // 클릭 리스너 함수
    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnCreatContents.setOnClickListener {

            if (boardType == 1) {
                createdBoard()
            } else {
                updateBoard()
            }
        }
        binding.btnAddPicture.setOnClickListener {
            openGallery()
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

    // url 변환
    private fun convertImageUriToString(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val imageBytes = inputStream?.readBytes()
            inputStream?.close()
            imageBytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun createdBoard() {
        val user = auth.currentUser

        var boardIndex = UUID.randomUUID().toString()
        val createdTime = Timestamp(Date())

        val imagesRef = storage.reference.child("boardImages/${UUID.randomUUID()}") // storage로 넣는 부분

        val contentsTitle = binding.etContentsTitle.text.toString()
        val contents = binding.etContents.text.toString()

        if (contentsTitle.isEmpty()) {
            binding.etContentsTitle.error = "제목을 입력해주세요"
        } else if (contents.isEmpty()) {
            binding.etContents.error = "내용을 입력해주세요"
        } else {
            val boardDto =
                BoardDto(
                    boardIndex,
                    user!!.uid,
                    nickname!!,
                    createdTime,
                    contentsTitle,
                    contents,
                    contentsPoto
                )
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectImgUri = data.data ?: return
            binding.ivPicture.setImageURI(selectImgUri)

            contentsPoto = convertImageUriToString(selectImgUri)
        }
    }


    // BoardReadActivity에서 페이지를 이동했을 경우 화면에 해당 게시물의 내용을 띄움
    private fun readBoard() {
        binding.tvWriter.text = BoardReadActivity.BoardData.writer
        binding.etContentsTitle.setText(BoardReadActivity.BoardData.contentsTitle)
        binding.etContents.setText(BoardReadActivity.BoardData.contents)
    }

    // 게시글 수정 시 업데이트
    private fun updateBoard() {
        val user = auth.currentUser

        val boardData = BoardReadActivity.BoardData
        val contentsTitle = binding.etContentsTitle.text.toString()
        val contents = binding.etContents.text.toString()

        if (contentsTitle.isEmpty()) {
            binding.etContentsTitle.error = "제목을 입력해주세요"
        } else if (contents.isEmpty()) {
            binding.etContents.error = "내용을 입력해주세요"
        } else {
            val boardDto =
                BoardDto(
                    boardData.bid,
                    boardData.uid,
                    nickname!!,
                    boardData.createdTime,
                    contentsTitle,
                    contents,
                    contentsPoto
                )
            db.collection("BoardDto").document(boardData.bid)
                .set(boardDto)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                }
        }
    }
}