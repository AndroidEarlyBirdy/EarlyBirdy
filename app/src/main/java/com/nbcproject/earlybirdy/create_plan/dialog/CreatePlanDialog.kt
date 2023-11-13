package com.nbcproject.earlybirdy.create_plan.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.data.Todo
import com.nbcproject.earlybirdy.databinding.ActivityCreatePlanDialogBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CreatePlanDialog(
    context: Context,
    selectedDay: CalendarDay?,
    listener: DialogCreateListener,
    type: String,
    position: Int?,
    todo: Todo?
) : Dialog(context) {
    private lateinit var binding: ActivityCreatePlanDialogBinding
    private val createDay: CalendarDay?
    private val dialogListener: DialogCreateListener
    private val dialogType: String
    private val todoPosition : Int?
    private val dialogTodo : Todo?

    init {
        createDay = selectedDay
        dialogListener = listener
        dialogType = type
        todoPosition = position
        dialogTodo = todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContent()
        initViews()
    }

    private fun setContent() {
        if(dialogType == "edit") {
            binding.etCreatePlanDialog.setText(dialogTodo?.title)
        }
    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnCreatePlanDialogSave.setOnClickListener {
            val todoContent = binding.etCreatePlanDialog.text.toString()
            when (dialogType) {
                "create" -> {
                    if (todoContent.isNotBlank()) {
                        val todo = createDay?.let { day -> Todo(null,day, todoContent, false) }
                        if (todo != null) {
                            dialogListener.onDialogSaveClicked(todo)
                        }
                        dismiss()
                    } else {
                        Toast.makeText(context, R.string.toast_blank_plan, Toast.LENGTH_SHORT).show()
                    }
                }

                "edit" -> {
                    if(todoContent.isNotBlank()) {
                        val todo = createDay?.let { day -> dialogTodo?.let { it1 ->
                            Todo(
                                dialogTodo.tid,day,todoContent,
                                it1.isChecked)
                        } }
                        if (todo != null) {
                            if (todoPosition != null) {
                                dialogListener.onDialogEditClicked(todo,todoPosition)
                                dismiss()
                            }
                            else {
                                Toast.makeText(context, R.string.toast_blank_plan, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        binding.btnCreatePlanDialogCancel.setOnClickListener {
            dismiss()
        }
    }

    interface DialogCreateListener {
        fun onDialogSaveClicked(todo: Todo)
        fun onDialogEditClicked(todo : Todo, position: Int)
    }

}