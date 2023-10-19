package com.example.earlybirdy.create_plan

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.R
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ActivityCreatePlanBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CreatePlanActivity : AppCompatActivity(), CreatePlanDialog.DialogCreateListener {

    private val listAdapter by lazy {
        CreatePlanAdapter{position, todo ->
            run {
                CreatePlanDialog(this@CreatePlanActivity, selectedDay, this@CreatePlanActivity, "edit", position, todo).show()
            }
        }
    }
    private val testList = mutableListOf(
        Todo(CalendarDay.from(2023, 10, 17), "test", false),
        Todo(CalendarDay.from(2023, 10, 16), "test2", true),
        Todo(CalendarDay.from(2023, 10, 20), "test3", false)
    )
    private var selectedDay: CalendarDay = CalendarDay.today()

    private lateinit var binding: ActivityCreatePlanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setCalendar()
    }

    //달력 세팅
    private fun setCalendar() = with(binding) {

        calendarView.addDecorator(Decorator(this@CreatePlanActivity))

        //오늘 날짜 Selected 되게 설정
        //오늘 계획 setting
        calendarView.selectedDate = CalendarDay.today()
        listAdapter.filterByDate(CalendarDay.today())
        //날짜 변경 시
        calendarView.setOnDateChangedListener { _, date, _ ->
            listAdapter.clearItems()
            for (todo in testList) {
                if (todo.date == date) {
                    listAdapter.filterByDate(date)
                }
            }
            //Dialog에 전달할 날짜 데이터
            selectedDay = date
        }
    }

    private fun initView() = with(binding) {
        binding.recyclerViewTodo.adapter = listAdapter
        binding.recyclerViewTodo.layoutManager =
            LinearLayoutManager(parent, LinearLayoutManager.VERTICAL, false)

        listAdapter.addItems(testList)

        //버튼 클릭 시 Dialog 생성
        binding.ivAddTodo.setOnClickListener {
            CreatePlanDialog(
                this@CreatePlanActivity,
                selectedDay,
                this@CreatePlanActivity,
                "create",
                null,
                null
            ).show()
        }
    }

    //오늘 날짜를 Custom
    class Decorator(context: Context) : DayViewDecorator {

        @SuppressLint("UseCompatLoadingForDrawables")
        private val drawable = context.getDrawable(R.drawable.bg_calendar_today)
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == CalendarDay.today()
        }

        override fun decorate(view: DayViewFacade?) {
            if (drawable != null) {
                view?.setBackgroundDrawable(drawable)
            }
        }

    }

    //Add Todo
    override fun onDialogSaveClicked(todo: Todo) {
        listAdapter.addItem(todo)
        listAdapter.addItemInList(todo)
    }

    override fun onDialogEditClicked(todo: Todo, position: Int) {
        listAdapter.editItem(todo,position)
        listAdapter.editItemInList(todo,position)

    }

}