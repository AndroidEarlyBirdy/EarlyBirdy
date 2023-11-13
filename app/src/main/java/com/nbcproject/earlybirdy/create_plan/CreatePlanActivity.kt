package com.nbcproject.earlybirdy.create_plan

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.data.MyGoal
import com.nbcproject.earlybirdy.data.Todo
import com.nbcproject.earlybirdy.databinding.ActivityCreatePlanBinding
import com.nbcproject.earlybirdy.main.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.nbcproject.earlybirdy.create_plan.dialog.CreateFailDialog
import com.nbcproject.earlybirdy.create_plan.dialog.CreatePlanDialog
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.Calendar
import java.util.UUID

class CreatePlanActivity : MainActivity(), CreatePlanDialog.DialogCreateListener,
    CreatePlanAdapter.TodoDeleteListener {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private val demoList = ArrayList<Todo>()

    private val expDecrease = -10

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
    private val testList = ArrayList<Todo>()
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

        loadData()
        initView()
    }

    //달력 세팅
    private fun setCalendar() = with(binding) {

        calendarView.addDecorator(Decorator(this@CreatePlanActivity))

        //오늘 날짜 Selected 되게 설정
        //오늘 계획 setting
        filterDate(CalendarDay.today())
        calendarView.selectedDate = CalendarDay.today()
        //날짜 변경 시
        calendarView.setOnDateChangedListener { _, date, _ ->
            listAdapter.clearItems()
            filterDate(date)
            //Dialog에 전달할 날짜 데이터
            selectedDay = date
        }
    }

    private fun initView() = with(binding) {
        recyclerViewTodo.adapter = listAdapter
        recyclerViewTodo.layoutManager =
            LinearLayoutManager(parent, LinearLayoutManager.VERTICAL, false)

        //버튼 클릭 시 Dialog 생성
        ivAddTodo.setOnClickListener {
            if (CalendarDay.today().isBefore(selectedDay) || CalendarDay.today() == selectedDay) {
                if(demoList.size < 3) {
                    CreatePlanDialog(
                        this@CreatePlanActivity,
                        selectedDay,
                        this@CreatePlanActivity,
                        "create",
                        null,
                        null
                    ).show()
                }
                else {
                    CreateFailDialog(this@CreatePlanActivity, getString(R.string.dialog_plan_limit)).show()
                }
            } else {
                CreateFailDialog(this@CreatePlanActivity, getString(R.string.dialog_passed_date)).show()
            }

        }
        binding.btnBack.setOnClickListener {
            finish()
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
        val attendIndex = UUID.randomUUID().toString()
        todo.tid = attendIndex

        testList.add(todo)
        listAdapter.clearItems()
        filterDate(selectedDay)

        fireStore.collection("UserDto").document(user.uid).collection("MyGoal")
            .document(attendIndex).set(
                hashMapOf(
                    "goalId" to attendIndex,
                    "date" to todo.date.toTimestamp(),
                    "title" to todo.title,
                    "check" to false
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
                    .document(it).update("title", todo.title)
            }
        }

    }

    override fun onDeleteButtonClicked(todo: Todo) {
        val matchingTodo = testList.find { it.tid == todo.tid }
        matchingTodo?.let {
            testList.remove(it)
        }
        val matchingDemo = demoList.find { it.tid == todo.tid }
        demoList.remove(matchingDemo)

        todo.tid?.let {
            fireStore.collection("UserDto").document(user.uid).collection("MyGoal").
            document(it).addSnapshotListener { value, _ ->
                if(value?.getBoolean("check") == true) {
                    fireStore.collection("UserDto").document(user.uid).update("exp",FieldValue.increment(expDecrease.toLong()))
                }
            }
        }

        todo.tid?.let {
            fireStore.collection("UserDto").document(user.uid).collection("MyGoal")
                .document(it).delete().addOnCompleteListener {
                }
        }

    }

    private fun CalendarDay.toTimestamp(): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.set(this.year, this.month - 1, this.day)
        return Timestamp(calendar.time)
    }

    private fun filterDate(day: CalendarDay) {
        demoList.clear()
        for (date in testList) {
            if (date.date == day) demoList.add(date)
        }
        listAdapter.addItems(demoList)
    }

    private fun loadData() {
        fireStore.collection("UserDto").document(user.uid)
            .collection("MyGoal").get().addOnSuccessListener { value ->
                for (snapshot in value!!.documents) {
                    val item = snapshot.toObject(MyGoal::class.java)
                    if (item != null) {
                        val todo = Todo(
                            item.goalId,
                            timestampToCalendarDay(item.date),
                            item.title,
                            item.check
                        )
                        testList.add(todo)
                    }
                }
                setCalendar()
            }

    }

    private fun timestampToCalendarDay(timestamp: Timestamp?): CalendarDay {
        val date = timestamp?.toDate()

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return CalendarDay.from(year, month + 1, day)
    }

}