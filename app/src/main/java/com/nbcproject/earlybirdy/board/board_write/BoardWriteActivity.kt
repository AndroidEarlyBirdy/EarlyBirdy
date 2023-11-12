package com.nbcproject.earlybirdy.board.board_write

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.nbcproject.earlybirdy.board.board_read.BoardReadActivity
import com.nbcproject.earlybirdy.databinding.ActivityBoardWriteBinding
import com.nbcproject.earlybirdy.dto.BoardDto
import com.nbcproject.earlybirdy.util.showToast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.nbcproject.earlybirdy.main.MainActivity
import java.util.Date
import java.util.UUID

class BoardWriteActivity : MainActivity() {
    private val binding by lazy { ActivityBoardWriteBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val db = Firebase.firestore
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var nickname: String? = ""

    private var imgUri: Uri = "".toUri()

    private val storageRef = storage.reference

    private var photoCheck: Boolean = false

    private val boardType: Int by lazy {
        intent.getIntExtra("boardType", 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        photoCheck = false

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
            when {
                //갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigateGallery()
                }

                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigateGallery()
                }
            }
        }
    }

    private fun getUserNicknameData() {
        var user = auth.currentUser
        fireStore.collection("UserDto").document(user!!.uid).addSnapshotListener { value, _ ->
            if (value != null) {
                nickname = value.getString("nickname")
            }
            binding.tvWriter.text = nickname
        }
    }

    //사진을 선택한 후
    private val pickImageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedImageUri: Uri? = data.data
                    if (selectedImageUri != null) {
                        binding.ivPicture.setImageURI(selectedImageUri)
                        imgUri = selectedImageUri
                        photoCheck = true
                    } else {
                        photoCheck = false
                        showToast(this@BoardWriteActivity, "사진을 가져오지 못했습니다.")
                    }
                }
            }
        }

    //갤러리에서 사진 선택
    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageActivityResult.launch(intent)
    }

    private fun createdBoard() {
        val user = auth.currentUser

        val boardIndex = UUID.randomUUID().toString()
        val createdTime = Timestamp(Date())

        val contentsTitle = binding.etContentsTitle.text.toString()
        val contents = binding.etContents.text.toString()

        var contentsPhoto: String?

        val storage: FirebaseStorage? = FirebaseStorage.getInstance()
        val fileName = boardIndex

        if (photoCheck){
            val imagesRef = storage!!.reference.child(boardIndex).child(fileName)

            imagesRef.putFile(imgUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->

                    contentsPhoto = it.toString()

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
                                contentsPhoto
                            )
                        db.collection("BoardDto").document(boardIndex)
                            .set(boardDto)
                            .addOnSuccessListener { documentReference ->
                                finish()
                            }
                    }
                }
            }
        }else{
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
                    )
                db.collection("BoardDto").document(boardIndex)
                    .set(boardDto)
                    .addOnSuccessListener {
                        finish()
                    }
            }
        }
    }

    // BoardReadActivity에서 페이지를 이동했을 경우 화면에 해당 게시물의 내용을 띄움
    private fun readBoard() {
        binding.tvWriter.text = BoardReadActivity.BoardData.writer
        binding.etContentsTitle.setText(BoardReadActivity.BoardData.contentsTitle)
        binding.etContents.setText(BoardReadActivity.BoardData.contents)

        val imageRef =
            storageRef.child(BoardReadActivity.BoardData.bid).child(BoardReadActivity.BoardData.bid)

        imageRef.downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(binding.ivPicture)
        }
    }

    // 게시글 수정 시 업데이트
    private fun updateBoard() {
        val boardData = BoardReadActivity.BoardData
        val contentsTitle = binding.etContentsTitle.text.toString()
        val contents = binding.etContents.text.toString()

        var contentsPhoto: String? = boardData.contentsPhoto

        if (photoCheck) {
            val imagesRef = storage.reference.child(boardData.bid).child(boardData.bid)

            imagesRef.putFile(imgUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->

                    contentsPhoto = it.toString()

                    if (contentsTitle.isEmpty()) {
                        binding.etContentsTitle.error = "제목을 입력해주세요"
                    } else if (contents.isEmpty()) {
                        binding.etContents.error = "내용을 입력해주세요"
                    } else {
                        updateBoardDatabase(boardData, contentsTitle, contents, contentsPhoto)
                    }
                }
            }
        } else {
            if (contentsTitle.isEmpty()) {
                binding.etContentsTitle.error = "제목을 입력해주세요"
            } else if (contents.isEmpty()) {
                binding.etContents.error = "내용을 입력해주세요"
            } else {
                updateBoardDatabase(boardData, contentsTitle, contents, contentsPhoto)
            }
        }
    }

    private fun updateBoardDatabase(boardData : BoardDto, contentsTitle : String,contents : String, contentsPhoto : String?) {
        val boardDto =
            BoardDto(
                boardData.bid,
                boardData.uid,
                nickname!!,
                boardData.createdTime,
                contentsTitle,
                contents,
                contentsPhoto
            )
        db.collection("BoardDto").document(boardData.bid)
            .set(boardDto)
            .addOnSuccessListener {
                finish()
            }
    }
}