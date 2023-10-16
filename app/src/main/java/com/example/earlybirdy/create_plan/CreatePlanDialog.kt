package com.example.earlybirdy.create_plan

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ActivityCreatePlanDialogBinding

class CreatePlanDialog (
    context: Context,
) : Dialog(context) {
    private lateinit var binding: ActivityCreatePlanDialogBinding
    private val listAdapter = CreatePlanAdapter()
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
            if(binding.etCreatePlanDialog.text.isNullOrBlank()) {
                Toast.makeText(context,"오늘의 계획을 입력하세요.",Toast.LENGTH_SHORT).show()
            } else {
                addTodayPlan()
                dismiss()
            }
        }
        binding.btnCreatePlanDialogCancel.setOnClickListener {
            dismiss()
        }
    }
    private fun addTodayPlan() {
        val todayPlan: MutableList<Todo> = mutableListOf(Todo("",binding.etCreatePlanDialog.text.toString(),false))
        listAdapter.addItems(todayPlan)
    }
}