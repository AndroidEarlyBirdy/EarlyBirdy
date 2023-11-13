package com.nbcproject.earlybirdy.edit_profile

import android.os.Bundle
import com.nbcproject.earlybirdy.databinding.ActivityEditProfileBinding
import android.util.Log
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nbcproject.earlybirdy.edit_profile.dialog.EditProfileActivityDialog
import com.nbcproject.earlybirdy.edit_profile.dialog.EditProfileDialog

class EditProfileActivity : MainActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileDialog: EditProfileDialog
    private lateinit var editProfileActivityDialog: EditProfileActivityDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    val db = Firebase.firestore
    private val imageMap = mapOf(
        1 to R.drawable.ic_person1,
        2 to R.drawable.ic_person2,
        3 to R.drawable.ic_person3,
        4 to R.drawable.ic_person4,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editProfileActivityDialog = EditProfileActivityDialog(this@EditProfileActivity)
        editProfileDialog = EditProfileDialog(this@EditProfileActivity)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        user = auth.currentUser!!
        fireStore = FirebaseFirestore.getInstance()


        // 프로필 사진 등록
        binding.icEditprofileProfile.setOnClickListener {
            editProfileDialog.show()
            editProfileDialog.setOnSaveClickListener {
                val value = editProfileDialog.getSelectedEditProfileImageId()
                setImageByFixedValue(value)
            }
        }

        binding.imgProflileProfile.setOnClickListener {
            editProfileDialog.show()
            editProfileDialog.setOnSaveClickListener {
                val value = editProfileDialog.getSelectedEditProfileImageId()
                setImageByFixedValue(value)
            }
        }

        // 저장 하기 버튼
        binding.btnProfileSave.setOnClickListener {
            onSaveButtonClick()
        }

        binding.btnPasswordChange.setOnClickListener {
            editProfileActivityDialog.show()
        }

        binding.imgProfileBack.setOnClickListener {
            finish()
        }

        loadUserData()
    }


    private fun onSaveButtonClick() {
        val profile = editProfileDialog.getSelectedEditProfileImageId()  // 이미지 객체 정보
        val nickname = binding.etProfileNickname.text.toString()
//        val email = binding.etProfileEmail.text.toString()

        // 빈칸 확인
        if (nickname.isBlank()) {
            binding.tilProfileNickname.error = "닉네임을 입력해주세요"

        } else {
                updateUserData(user.uid, profile, nickname)
                showToast(this@EditProfileActivity, "회원 정보 수정 완료!")
                finish()
        }

    }

    private fun updateUserData(uid: String, profile: Int, nickname: String?) {

       if(profile > 0)
        db.collection("UserDto").document(uid).update(
            mapOf(
                "profile" to profile,
                "nickname" to nickname,
            )
        ).addOnSuccessListener {
            showToast(this@EditProfileActivity,"회원정보 업데이트 완료!")
        }.addOnFailureListener{e ->
            Log.e("error","Error updating document", e)
        } else {
           db.collection("UserDto").document(uid).update(
               mapOf(
                   "nickname" to nickname
               )
           ).addOnSuccessListener {
               showToast(this@EditProfileActivity,"회원정보 업데이트 완료!")
           }.addOnFailureListener{e ->
               Log.e("error","Error updating document", e)
           }
       }
    }
    private fun loadUserData() {
        val uid = user.uid


        db.collection("UserDto").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("nickname") ?: ""
                    val profile = document.getLong("profile")?.toInt() ?: 0
                    setImageByFixedValue(profile)
                    binding.etProfileNickname.setText(nickname)
                }
            }
            .addOnFailureListener { e ->
                showToast(this, "사용자 정보 로딩에 실패했습니다.")
            }
    }

    private fun setImageByFixedValue(fixedValue: Int) {
        val uid = user.uid
        val profileImageRef = fireStore.collection("UserDto").document(uid)
        val imageResourceId = imageMap[fixedValue]
        if (imageResourceId != null) {
            if (imageResourceId > 0) {
                binding.imgProflileProfile.setImageResource(imageResourceId)
            } else {
                profileImageRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val imageResId = document.getLong("profile")?.toInt()
                            if (imageResId != null) {
                                binding.imgProflileProfile.setImageResource(imageResId)
                            }
                        } else {
                            binding.imgProflileProfile.setImageResource(R.drawable.img_profile_add111)
                        }
                    }
                    .addOnFailureListener { exception ->

                        binding.imgProflileProfile.setImageResource(R.drawable.ic_person4)
                    }
            }
        }
    }



}
