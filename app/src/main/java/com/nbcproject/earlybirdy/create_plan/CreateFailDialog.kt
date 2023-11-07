package com.nbcproject.earlybirdy.create_plan

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.nbcproject.earlybirdy.databinding.DialogFailToAddBinding

class CreateFailDialog(context : Context, warningText : String) : Dialog(context) {

    private lateinit var binding : DialogFailToAddBinding
    private val warning : String = warningText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogFailToAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDialog()
    }

    private fun initDialog() {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = warning

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
}