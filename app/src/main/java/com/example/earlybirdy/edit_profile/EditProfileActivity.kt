package com.example.earlybirdy.edit_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.earlybirdy.databinding.ActivityEditProfileBinding
import com.example.earlybirdy.signup.EditProfileDialog
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.example.earlybirdy.R
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileDialog: EditProfileDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var editProfileActivityDialog: EditProfileActivityDialog
    val db = Firebase.firestore
    private val imageMap = mapOf(
        1 to R.drawable.img_profile_man1,
        2 to R.drawable.img_profile_woman1,
        3 to R.drawable.img_profile_man2,
        4 to R.drawable.img_profile_woman2,
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

        // 저장 하기 버튼
        binding.btnProfileSave.setOnClickListener {
            val password = binding.etProfilePassword.text.toString()
            val passwordCheck = binding.etProfilePasswordCheck.text.toString()
            if (password == passwordCheck) {
                onSaveButtonClick()
            } else {
                showToast(this,"비밀번호가 일치하지 않습니다.")
            }
        }

        binding.etProfilePassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etProfilePassword.text.toString()
                        .equals(binding.etProfilePasswordCheck.text.toString())
                ) {
                    binding.tilProfilePasswordCheck.error = "비밀번호가 일치합니다."
                } else {
                    binding.tilProfilePasswordCheck.error = "비밀번호가 일치하지 않습니다."
                }
            }
        })
        loadUserData()
        editProfileActivityDialog.show()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_right_exit)
    }


    private fun onSaveButtonClick() {
        val profile = editProfileDialog.getSelectedEditProfileImageId()  // 이미지 객체 정보
        val nickname = binding.etProfileNickname.text.toString()
        val email = binding.etProfileEmail.text.toString()
        val password = binding.etProfilePassword.text.toString()
        val passwordCheck = binding.etProfilePasswordCheck.text.toString()



        // 빈칸 확인
        if (nickname.isBlank()) {
            binding.tilProfileNickname.error = "닉네임을 입력해주세요"
        } else if (password.isBlank()) {
            binding.tilProfilePassword.error = "비밀번호를 입력해주세요"
        } else if (!password.equals(passwordCheck)) {
            binding.tilProfilePasswordCheck.error = "일치하지 않습니다"
        }

        changePassword()
        updateUserData(user.uid, profile, nickname, email)
        navigateToMainActivity(this)
        showToast(this@EditProfileActivity,"회원 정보 수정 완료!")

        finish()

    }

    private fun updateUserData(uid: String, profile: Int?, nickname: String?, email: String?) {

        db.collection("UserDto").document(uid).update(
            mapOf(
                "profile" to profile,
                "nickname" to nickname,
                "email" to email,
            )
        ).addOnSuccessListener {
            showToast(this@EditProfileActivity,"회원정보 업데이트 완료!")
        }.addOnFailureListener{e ->
            Log.e("error","Error updating document", e)
        }
    }
    private fun loadUserData() {
        val uid = user.uid

        db.collection("UserDto").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("nickname") ?: ""
                    val email = document.getString("email") ?: ""
                    val profile = document.getLong("profile")?.toInt() ?: 0
                    setImageByFixedValue(profile)
                    binding.etProfileNickname.setText(nickname)
                    binding.etProfileEmail.setText(email)
                }
            }
            .addOnFailureListener { e ->
                Log.e("loadUserData", "사용자 정보 로딩 실패", e)
                showToast(this, "사용자 정보 로딩에 실패했습니다.")
            }
    }

    private fun changePassword() {
        val user = Firebase.auth.currentUser!!
        val newPassword = binding.etProfilePassword.text.toString()
        val credential = EmailAuthProvider
            .getCredential("user@example.com", "password123")
        user.reauthenticate(credential)
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("password", "User password updated.")
                }
            }
            .addOnFailureListener {
                showToast(this, "비밀번호변경에 실패하였습니다.")
                Log.e("fail","${it}")
            }
    }
    private fun setImageByFixedValue(fixedValue: Int) {
        val imageResourceId = imageMap[fixedValue]
        if (imageResourceId != null) {
            binding.imgProflileProfile.setImageResource(imageResourceId)
        } else {
            binding.imgProflileProfile.setImageResource(R.drawable.img_profile_add)
        }
    }



}
