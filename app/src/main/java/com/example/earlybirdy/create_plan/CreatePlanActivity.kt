package com.example.earlybirdy.create_plan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ActivityCreatePlanBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class CreatePlanActivity : AppCompatActivity() {

    private val listAdapter = CreatePlanAdapter()
    private val testList = mutableListOf(
        Todo(CalendarDay.from(2023,10,17),"test",false),
        Todo(CalendarDay.from(2023,10,16),"test2",true),
        Todo(CalendarDay.from(2023,10,20), "test3", false))

    private lateinit var binding : ActivityCreatePlanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setCalendar()
    }
    //달력 세팅
    private fun setCalendar() = with(binding) {
        //오늘 날짜 Selected 되게 설정
        //오늘 계획 setting
        calendarView.selectedDate = CalendarDay.today()
        val today = CalendarDay.today().year.toString()+ CalendarDay.today().month.toString() + CalendarDay.today().day.toString()
        listAdapter.clearItems()
        for (todo in testList) {
            if (todo.date == CalendarDay.today()) {
                listAdapter.addItem(todo)
            }
        }

        //날짜 변경 시
        calendarView.setOnDateChangedListener { _, date, _ ->
            listAdapter.clearItems()
            val selectedDate = date.year.toString() + date.month.toString() + date.day.toString()
            for (todo in testList) {
                if (todo.date == date) {
                    listAdapter.addItem(todo)
                }
            }
        }
    }

    private fun initView() = with(binding) {
        recyclerViewTodo.adapter = listAdapter
        recyclerViewTodo.layoutManager= LinearLayoutManager(parent, LinearLayoutManager.VERTICAL, false)

        binding.ivAddTodo.setOnClickListener {
            CreatePlanDialog(this@CreatePlanActivity).show()
        }
    }
}