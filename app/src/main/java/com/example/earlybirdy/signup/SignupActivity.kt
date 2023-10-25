package com.example.earlybirdy.signup

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.earlybirdy.databinding.ActivitySignupBinding
import com.example.earlybirdy.dto.UserDto
import com.example.earlybirdy.util.navigateToMainActivity
import com.example.earlybirdy.util.navigateToSigninActivity
import com.example.earlybirdy.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val db = Firebase.firestore
    //val storage = Firebase.storage

    private lateinit var selectImgUri: Uri
    private val IMAGE_PICKER_REQUEST_CODE = 123
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
            signupDialog.setOnSaveClickListener { selectedImageId ->
                binding.ivProfile.setImageResource(selectedImageId)
            }
        }

        // 비밀번호 확인 함수
        binding.titPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.titPassword.getText().toString()
                        .equals(binding.titPasswordCheck.getText().toString())
                ) {
                    binding.tilPasswordCheck.error = "비밀번호가 일치합니다"
                } else {
                    binding.tilPasswordCheck.error = "일치하지 않습니다"
                }
            }
        })

        // 회원가입
        binding.btnSignup.setOnClickListener {
            onStart()
        }

        binding.btnSignupGoogle.setOnClickListener {
            navigateToMainActivity(this)
        }

        // 로그인 페이지로 이동
        binding.tvBtnSignin.setOnClickListener {
            navigateToSigninActivity(this)
            finish()
        }
    }

    // 회원가입 함수
    public override fun onStart() { // 이미 로그인 페이지에서 로그인 여부 체크가 들어가기 때문에 굳이?
        super.onStart()
        val user = auth.currentUser
        user?.reload() // 최신 유저 정보 갱신
        if (user != null) { // 로그인 여부 체크
            navigateToMainActivity(this)
            showToast(this, "이미 로그인 중입니다")
        } else {
            val profile = signupDialog.getSelectedImageId()  // 이미지 객체 정보
            val nickname = binding.titNickname.text.toString()
            val email = binding.titEmail.text.toString()
            val password = binding.titPassword.text.toString()
            val passwordCheck = binding.titPasswordCheck.text.toString()

            // 빈칸 확인
            if (nickname.isBlank()) {
                binding.tilNickname.error = "닉네임을 입력해주세요"
            } else if (email.isBlank()) {
                binding.tilEmail.error = "이메일을 입력해주세요"
            } else if (password.isBlank()) {
                binding.tilPassword.error = "비밀번호를 입력해주세요"
            } else if (!password.equals(passwordCheck)) {
                binding.tilPasswordCheck.error = "일치하지 않습니다"
            } else {
                // 닉네임 중복 처리 확인
                db.collection("UserDto").whereEqualTo("nickname", nickname)
                    .get()
                    .addOnSuccessListener { documentReference ->
                        Log.d("docuTest", "${documentReference.documents}")
                        if (documentReference.documents.isEmpty()) {
                            // auth 회원가입
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        // firestore DB에 저장
                                        val userDto =
                                            UserDto(user!!.uid, profile, nickname, email, 0)

                                        db.collection("UserDto").document(user.uid)
                                            .set(userDto)
                                            .addOnSuccessListener { documentReference ->
                                            }
                                            .addOnFailureListener { e ->
                                            }

                                        showToast(this, "회원가입 성공 & 자동로그인")
                                        navigateToMainActivity(this)
                                        finish()
                                    }
                                }.addOnFailureListener { // 이메일 중복 체크
                                    if (it is FirebaseAuthUserCollisionException) {
                                        binding.tilEmail.error = "이미 가입된 이메일입니다"
                                    }
                                    if (it is FirebaseAuthInvalidCredentialsException) {
                                        binding.tilEmail.error = "유효하지 않은 이메일 형식입니다"
                                    }
                                }
                        } else {
                            binding.tilNickname.error = "이미 가입된 닉네임 입니다."
                        }
                    }
                    .addOnFailureListener { e ->
                    }
            }
        }
    }

//    private fun profileUpload(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            checkPermission(Manifest.permission.READ_MEDIA_IMAGES)
//        }else{
//            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//    }

//    private fun openAlbum() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            selectImgUri = data.data ?: return
//            binding.ivProfile.setImageURI(selectImgUri)
//        }
//    }
}