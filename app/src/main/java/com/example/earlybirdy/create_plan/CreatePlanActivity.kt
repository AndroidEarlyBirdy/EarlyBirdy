package com.example.earlybirdy.create_plan

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earlybirdy.R
import com.example.earlybirdy.data.Todo
import com.example.earlybirdy.databinding.ActivityCreatePlanBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.Calendar
import java.util.UUID

class CreatePlanActivity : AppCompatActivity(), CreatePlanDialog.DialogCreateListener,
    CreatePlanAdapter.TodoDeleteListener {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private val demoList = ArrayList<Todo>()

    private val listAdapter by lazy {
        CreatePlanAdapter({ position, todo ->
            run {
                CreatePlanDialog(
                    this@CreatePlanActivity,
                    selectedDay,
                    this@CreatePlanActivity,
                    "edit",
                    position,
                    todo
                ).show()
            }
        }, this)
    }
    private val testList = mutableListOf(
        Todo("1", CalendarDay.from(2023, 10, 17), "test", false),
        Todo("2", CalendarDay.from(2023, 10, 16), "test2", true),
        Todo("3", CalendarDay.from(2023, 10, 20), "test3", false),
        Todo("4", CalendarDay.from(2023, 10, 19), "test4", false)
    )
    private var selectedDay: CalendarDay = CalendarDay.today()

    private lateinit var binding: ActivityCreatePlanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStore = FirebaseFirestore.getInstance()
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        initView()
        setCalendar()
    }

    //달력 세팅
    private fun setCalendar() = with(binding) {

        calendarView.addDecorator(Decorator(this@CreatePlanActivity))

        //오늘 날짜 Selected 되게 설정
        //오늘 계획 setting
        calendarView.selectedDate = CalendarDay.today()
        filterDate(CalendarDay.today())
        //날짜 변경 시
        calendarView.setOnDateChangedListener { _, date, _ ->
            listAdapter.clearItems()
            filterDate(date)
            //Dialog에 전달할 날짜 데이터
            selectedDay = date
        }
    }

    private fun initView() = with(binding) {
        binding.recyclerViewTodo.adapter = listAdapter
        binding.recyclerViewTodo.layoutManager =
            LinearLayoutManager(parent, LinearLayoutManager.VERTICAL, false)

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

    override fun onDialogSaveClicked(todo: Todo) {
        var attendIndex = UUID.randomUUID().toString()
        todo.tid = attendIndex

        testList.add(todo)
        listAdapter.clearItems()
        filterDate(selectedDay)

        fireStore.collection("UserDto").document(user.uid).collection("MyGoal")
            .document(attendIndex).set(
                hashMapOf(
                    "TodoId" to attendIndex,
                    "Date" to todo.date.toTimestamp(),
                    "Title" to todo.title,
                    "IsChecked" to todo.isChecked
                )
            )
    }

    override fun onDialogEditClicked(todo: Todo, position: Int) {
        val index = testList.indexOfFirst { it.tid == todo.tid }
        listAdapter.editItemInList(todo, position)

        if (index != -1) {
            testList[index].title = todo.title
            listAdapter.editItemInList(testList[index], position)
            todo.tid?.let {
                fireStore.collection("UserDto").document(user.uid).collection("MyGoal")
                    .document(it).update("Title", todo.title)
            }
        }

    }

    override fun onDeleteButtonClicked(todo: Todo) {
        val matchingTodo = testList.find { it.tid == todo.tid }
        matchingTodo?.let {
            testList.remove(it)
        }

        todo.tid?.let {
            fireStore.collection("UserDto").document(user.uid).collection("MyGoal")
                .document(it).delete().addOnCompleteListener {

                }
        }
    }

    private fun CalendarDay.toTimestamp(): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(this.year, this.month, this.day)
        return Timestamp(calendar.time)
    }

    private fun filterDate(day: CalendarDay) {
        demoList.clear()
        for (date in testList) {
            if (date.date == day) demoList.add(date)
        }
        listAdapter.addItems(demoList)
    }

}