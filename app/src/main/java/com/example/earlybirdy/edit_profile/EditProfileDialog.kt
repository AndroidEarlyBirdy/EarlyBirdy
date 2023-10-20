package com.example.earlybirdy.signup

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityProfileDialogBinding


class EditProfileDialog (
    context: Context,
) : Dialog(context) {
    private lateinit var binding: ActivityProfileDialogBinding
    private var selectedEditProfileImageId: Int = 0
    private var lastSelectedEditProfileImageId: Int = 0
    private var onSaveClickListener: ((Int) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    fun getSelectedEditProfileImageId(): Int {
        if (selectedEditProfileImageId != 0) {
            lastSelectedEditProfileImageId = selectedEditProfileImageId
        }
        return lastSelectedEditProfileImageId
    }

    // "저장" 버튼 클릭 리스너 설정
    fun setOnSaveClickListener(listener: (Int) -> Unit) {
        onSaveClickListener = listener
    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.ivProfileDialogSelect.setImageResource(selectedEditProfileImageId)

        binding.ivProfileDialog1.setOnClickListener{
            selectedEditProfileImageId = R.drawable.img_profile_sample
            binding.ivProfileDialogSelect.setImageResource(selectedEditProfileImageId)
        }

        binding.ivProfileDialog2.setOnClickListener{
            selectedEditProfileImageId = R.drawable.ic_insignia2
            binding.ivProfileDialogSelect.setImageResource(selectedEditProfileImageId)
        }

        binding.ivProfileDialog3.setOnClickListener{
            selectedEditProfileImageId = R.drawable.ic_insignia3
            binding.ivProfileDialogSelect.setImageResource(selectedEditProfileImageId)
        }

        binding.ivProfileDialog4.setOnClickListener{
            selectedEditProfileImageId = R.drawable.ic_insignia4
            binding.ivProfileDialogSelect.setImageResource(selectedEditProfileImageId)
        }

        binding.btnProfileDialogSave.setOnClickListener {
            if(selectedEditProfileImageId != 0) {
                onSaveClickListener?.invoke(selectedEditProfileImageId)

            } else {
                dismiss()
            }
            dismiss()
        }
        binding.btnProfileDialogCancel.setOnClickListener {
            dismiss()
        }
    }
}
