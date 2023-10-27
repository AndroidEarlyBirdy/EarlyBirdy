package com.example.earlybirdy.communitywrite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.earlybirdy.R
import com.example.earlybirdy.data.CommunityData
import com.example.earlybirdy.data.MyPageData
import com.example.earlybirdy.databinding.ActivityCommunityWriteBinding
import com.example.earlybirdy.dto.CommunityDto
import com.example.earlybirdy.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class CommunityWriteActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCommunityWriteBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore

    private val fireStore = FirebaseFirestore.getInstance()

    private var nickname: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

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

        val contentsTitle = binding.etContentsTitle.text.toString()
        val contents = binding.etContents.text.toString()

        if (contentsTitle.isEmpty()) {
            binding.etContentsTitle.error = "제목을 입력해주세요"
        } else if (contents.isEmpty()) {
            binding.etContents.error = "내용을 입력해주세요"
        } else {
            val communityDto =
                CommunityDto(user!!.uid, nickname!!, contentsTitle, contents)
            db.collection("CommunityDto").document()
                .set(communityDto)
                .addOnSuccessListener { documentReference ->
                    Log.d("왜 안되냐", "${communityDto}")
                    finish()
                }
                .addOnFailureListener { e ->
                }
        }

    }

}
