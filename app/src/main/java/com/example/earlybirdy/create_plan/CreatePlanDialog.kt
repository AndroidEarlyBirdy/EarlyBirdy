package com.example.earlybirdy.create_plan

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ActivityCreatePlanDialogBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CreatePlanDialog(
    context: Context,
    selectedDay: CalendarDay,
    listener: DialogListener
) : Dialog(context) {
    private lateinit var binding: ActivityCreatePlanDialogBinding
    private val createDay : CalendarDay
    private val dialogListener: DialogListener
    init {
        createDay = selectedDay
        dialogListener = listener
    }
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
            val todoContent = binding.etCreatePlanDialog.text.toString()
            if(todoContent.isNotBlank()) {
                val todo = Todo(createDay,todoContent,false)
                dialogListener.onDialogSaveClicked(todo)
                dismiss()
            } else {
                Toast.makeText(context,"오늘의 계획을 입력하세요.",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCreatePlanDialogCancel.setOnClickListener {
            dismiss()
        }
    }
    interface DialogListener {
        fun onDialogSaveClicked(todo : Todo)
    }
}