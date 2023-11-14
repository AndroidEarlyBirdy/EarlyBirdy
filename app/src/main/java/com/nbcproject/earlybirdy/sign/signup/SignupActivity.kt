package com.nbcproject.earlybirdy.sign.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivitySignupBinding
import com.nbcproject.earlybirdy.dto.UserDto
import com.nbcproject.earlybirdy.main.MainActivity
import com.nbcproject.earlybirdy.util.navigateToMainActivity
import com.nbcproject.earlybirdy.util.navigateToSigninActivity
import com.nbcproject.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : MainActivity() {

    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val fireStore = Firebase.firestore

    private val imageMap = mapOf(
        1 to R.drawable.ic_person1,
        2 to R.drawable.ic_person2,
        3 to R.drawable.ic_person3,
        4 to R.drawable.ic_person4,
        0 to R.drawable.ic_person1
    )
    private lateinit var signupDialog: SignupDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        signupDialog = SignupDialog(this@SignupActivity)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        // 프로필 사진 등록
        binding.ivProfile.setOnClickListener {
            signupDialog.show()
            signupDialog.setOnSaveClickListener {
                val value = signupDialog.getSelectedImageId()
                setImageByFixedValue(value)
            }
        }

        // 비밀번호 확인 함수
        binding.titPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.titPassword.text.toString() == binding.titPasswordCheck.text.toString()
                ) {
                    binding.tilPasswordCheck.error = getString(R.string.util_error_matchPassword)
                } else {
                    binding.tilPasswordCheck.error = getString(R.string.util_error_mismatchPassword)
                }
            }
        })

        // 회원가입
        binding.btnSignup.setOnClickListener {
            onStart()
        }

        // 로그인 페이지로 이동
        binding.tvBtnSignin.setOnClickListener {
            navigateToSigninActivity(this)
        }
    }

    override fun onBackPressed() {
        navigateToSigninActivity(this)
    }

    // 회원가입 함수
    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
        if (user != null) { // 로그인 여부 체크
            navigateToMainActivity(this)
        } else {
            val profile = signupDialog.getSelectedImageId()  // 이미지 객체 정보
            val nickname = binding.titNickname.text.toString()
            val email = binding.titEmail.text.toString()
            val password = binding.titPassword.text.toString()
            val passwordCheck = binding.titPasswordCheck.text.toString()

            // 빈칸 확인
            if (nickname.isBlank()) {
                binding.tilNickname.error = getString(R.string.signUp_error_emptyNickname)
            } else if (email.isBlank()) {
                binding.tilEmail.error = getString(R.string.util_error_emptyEmail)
            } else if (password.isBlank()) {
                binding.tilPassword.error = getString(R.string.util_error_emptyPassword)
            } else if (password != passwordCheck) {
                binding.tilPasswordCheck.error = getString(R.string.util_error_mismatchPassword)
            } else {
                // 닉네임 중복 처리 확인
                fireStore.collection("UserDto").whereEqualTo("nickname", nickname)
                    .get()
                    .addOnSuccessListener { documentReference ->
                        if (documentReference.documents.isEmpty()) {
                            // auth 회원가입
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        saveDataOnDB(profile,nickname,email)
                                    }
                                }.addOnFailureListener { // 이메일 중복 체크
                                    if (it is FirebaseAuthUserCollisionException) {
                                        binding.tilEmail.error = getString(R.string.signUp_error_alreadyEmail)
                                    }
                                    if (it is FirebaseAuthInvalidCredentialsException) {
                                        binding.tilEmail.error = getString(R.string.signUp_error_invalidEmail)
                                    }
                                    if (it is FirebaseAuthWeakPasswordException){
                                        binding.tilPassword.error = getString(R.string.signUp_error_6underPassword)
                                    }
                                }
                        } else {
                            binding.tilNickname.error = getString(R.string.signUp_error_alreadyNickname)
                        }
                    }
            }
        }
    }

    // firestore DB에 저장
    private fun saveDataOnDB(profile : Int, nickname : String, email : String){
        val user = auth.currentUser
        val userDto =
            UserDto(user!!.uid, profile, nickname, email, 0)

        fireStore.collection("UserDto").document(user.uid).set(userDto)

        showToast(this, getString(R.string.signUp_toast_signUpSuccess))
        navigateToMainActivity(this)
        finish()
    }

    private fun setImageByFixedValue(fixedValue: Int) {
        val imageResourceId = imageMap[fixedValue]
        if (imageResourceId != null) {
            binding.ivProfile.setImageResource(imageResourceId)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person1)
        }
    }

}