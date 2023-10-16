package com.example.earlybirdy.create_plan

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.earlybirdy.databinding.ActivityCreatePlanDialogBinding

class CreatePlanDialog (
    context: Context,

) : Dialog(context) {
    private lateinit var binding: ActivityCreatePlanDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnCreatePlanDialogSave.setOnClickListener {

        }

        binding.btnCreatePlanDialogCancel.setOnClickListener {

        }
    }
}