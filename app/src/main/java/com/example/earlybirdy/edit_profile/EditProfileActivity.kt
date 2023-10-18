package com.example.earlybirdy.edit_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.earlybirdy.databinding.ActivityEditProfileBinding
import com.example.earlybirdy.signup.EditProfileDialog
import android.util.Log
import com.example.earlybirdy.dto.UserDto
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileDialog : EditProfileDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editProfileDialog = EditProfileDialog(this@EditProfileActivity)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        // 프로필 사진 등록
        binding.icEditprofileProfile.setOnClickListener {
            editProfileDialog.show()
            editProfileDialog.setOnSaveClickListener { selectedImageId ->
                binding.imgProflileProfile.setImageResource(selectedImageId)
            }
        }

        // 저장 하기 버튼
        binding.clProfileSave.setOnClickListener{
            onSaveButtonClick()
        }

        binding.etProfilePassword.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (binding.etProfilePassword.getText().toString().equals(binding.etProfilePasswordCheck.getText().toString())){
                    binding.tilProfilePasswordCheck.error = "비밀번호가 일치합니다."
                    binding.clProfileSave.isEnabled= true
                } else {
                    binding.tilProfilePasswordCheck.error = "비밀번호가 일치하지 않습니다."
                    binding.clProfileSave.isEnabled=false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    fun onSaveButtonClick() {
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
        if (user != null) { // 로그인 여부 체크
            navigateToMainActivity(this)
        } else {
            val profile = editProfileDialog.getSelectedEditProfileImageId()  // 이미지 객체 정보
            val nickname = binding.etProfileNickname.text.toString()
            val email = binding.etProfileEmail.text.toString()
            val password = binding.etProfilePassword.text.toString()
            val passwordCheck = binding.etProfilePasswordCheck.text.toString()

            // 빈칸 확인
            if (nickname.isBlank()) {
                binding.tilProfileNickname.error = "닉네임을 입력해주세요"
            } else if (email.isBlank()) {
                binding.tilProfileEmail.error = "이메일을 입력해주세요"
            } else if (password.isBlank()) {
                binding.tilProfilePassword.error = "비밀번호를 입력해주세요"
            } else if(!password.equals(passwordCheck)){
                binding.tilProfilePasswordCheck.error = "일치하지 않습니다"
            } else {
                // auth 회원가입
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUI(user)
                            // firestore DB에 저장
                            val userDto =
                                UserDto(user!!.uid,profile, nickname, email, password)
                            db.collection("UserDto").document(user!!.uid)
                                .set(userDto)
                                .addOnSuccessListener { documentReference ->
                                }
                                .addOnFailureListener { e ->
                                }
                            showToast(this, "회원가입 성공 & 자동로그인")
                            navigateToMainActivity(this)
                            finish()
                        }
                    }.addOnFailureListener {
                        Log.e("email", "이메일 중복 테스트", it)
                        if (it is FirebaseAuthUserCollisionException){

                        }
                    }
            }
        }

    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(user: FirebaseUser?) {

    }

}
