package com.example.earlybirdy.create_plan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.R
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ActivityCreatePlanBinding

class CreatePlanActivity : AppCompatActivity() {

    private val listAdapter = CreatePlanAdapter()

    private lateinit var binding : ActivityCreatePlanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        initView()
    }

    private fun setData() {
        val testList = mutableListOf(Todo("1","test",false), Todo("2","test2",true))
        listAdapter.addItems(testList)
    }

    private fun initView() = with(binding) {
        recyclerViewTodo.adapter = listAdapter
        recyclerViewTodo.layoutManager= LinearLayoutManager(parent, LinearLayoutManager.VERTICAL, false)

        binding.ivAddTodo.setOnClickListener{
            val builder = AlertDialog.Builder(this@CreatePlanActivity)

            val dialogView = layoutInflater.inflate(R.layout.activity_create_plan_dialog, null)
            builder.setView(dialogView)

        }
    }


}