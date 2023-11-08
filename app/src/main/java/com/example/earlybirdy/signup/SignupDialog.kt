package com.example.earlybirdy.signup

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivitySignupDialogBinding


class SignupDialog (
    context: Context,
) : Dialog(context) {
    private lateinit var binding: ActivitySignupDialogBinding
    private var selectedImageId: Int = 0
    private var lastSelectedImageId: Int = 0
    private var onSaveClickListener: ((Int) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    // 이미지 ID를 반환하는 함수
    fun getSelectedImageId(): Int {
        if (selectedImageId != 0) {
            lastSelectedImageId = selectedImageId
        }
        return lastSelectedImageId
    }

    // "저장" 버튼 클릭 리스너 설정
    fun setOnSaveClickListener(listener: (Int) -> Unit) {
        onSaveClickListener = listener
    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.ivSignupDialog1.setOnClickListener{
            selectedImageId = 1
            binding.ivSignupDialogSelect.setImageResource(R.drawable.ic_person1)
        }

        binding.ivSignupDialog2.setOnClickListener{
            selectedImageId = 2
            binding.ivSignupDialogSelect.setImageResource(R.drawable.ic_person2)
        }

        binding.ivSignupDialog3.setOnClickListener{
            selectedImageId = 3
            binding.ivSignupDialogSelect.setImageResource(R.drawable.ic_person3)
        }

        binding.ivSignupDialog4.setOnClickListener{
            selectedImageId = 4
            binding.ivSignupDialogSelect.setImageResource(R.drawable.ic_person4)
        }

        binding.btnSignupDialogSave.setOnClickListener {
            if(selectedImageId != 0) {
                onSaveClickListener?.invoke(selectedImageId)

            } else {
                dismiss()
            }
            dismiss()
        }
        binding.btnSignupDialogCancel.setOnClickListener {
            dismiss()
        }
    }
}
