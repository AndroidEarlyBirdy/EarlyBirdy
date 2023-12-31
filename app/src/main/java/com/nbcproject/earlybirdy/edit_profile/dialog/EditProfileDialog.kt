package com.nbcproject.earlybirdy.edit_profile.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.ActivityProfileDialogBinding


class EditProfileDialog (
    context: Context,
) : Dialog(context, R.style.Theme_TransparentBackground) {
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
            selectedEditProfileImageId = 1
            binding.ivProfileDialogSelect.setImageResource(R.drawable.ic_person1)
        }

        binding.ivProfileDialog2.setOnClickListener{
            selectedEditProfileImageId = 2
            binding.ivProfileDialogSelect.setImageResource(R.drawable.ic_person2)
        }

        binding.ivProfileDialog3.setOnClickListener{
            selectedEditProfileImageId = 3
            binding.ivProfileDialogSelect.setImageResource(R.drawable.ic_person3)
        }

        binding.ivProfileDialog4.setOnClickListener{
            selectedEditProfileImageId = 4
            binding.ivProfileDialogSelect.setImageResource(R.drawable.ic_person4)
        }

        binding.btnProfileDialogSave.setOnClickListener {
            if(selectedEditProfileImageId != 0) {
                onSaveClickListener?.invoke(selectedEditProfileImageId)
            }
            dismiss()
        }
        binding.btnProfileDialogCancel.setOnClickListener {
            dismiss()
        }
    }
}
