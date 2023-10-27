package com.example.earlybirdy.my_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.earlybirdy.R
import com.example.earlybirdy.home.HomeFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MyPageFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_page, container, false)
        calendarView = view.findViewById(R.id.calendarView)


        loadAttendanceData()

        return view
    }

    private fun loadAttendanceData() {
        val userId = "alUKQs4TIDM7F6GFC01TfXdUWwB2"


        firestore.collection("UserDto")
            .document(userId)
            .collection("Attendance")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                for (document in querySnapshot!!) {
                    val date = document.getString("date")
                    if (date != null) {
                        val calendarDay = parseDate(date)
                        val dayDecorator = DayDecorator(calendarDay, requireContext())
                        calendarView.addDecorator(dayDecorator)
                    }
                }

            }
    }

    // 날짜를 CalendarDay로 변환
    private fun parseDate(date: String): CalendarDay {
        val year = date.substring(0, 4).toInt()
        val month = date.substring(5, 7).toInt()
        val day = date.substring(8, 10).toInt()
        return CalendarDay.from(year, month, day)
    }

//    // 날짜에 따라 배경 이미지 추가
//    private fun checkGoalsForDate(date: CalendarDay) {
//        val userId = "사용자 UID" // 사용자의 UID로 변경
//
//        // Firestore에서 사용자의 목표 데이터 가져오기
//        firestore.collection("UserDto")
//            .document(userId)
//            .collection("MyGoal")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot) {
//                    val goalDate = document.getString("date")
//                    val checked = document.getBoolean("check") ?: false
//
//                    if (goalDate != null && parseDate(goalDate) == date && checked) {
//                        // 해당 날짜에 목표가 달성되고 체크된 경우, 배경 이미지를 추가
//                        calendarView.addDecorator(DayDecorator(date, R.drawable.bg_calendar_date))
//                        break // 이미 배경 이미지를 추가했으므로 루프 종료
//                    }
//                }
//            }
//    }

    // 날짜에 따라 배경 이미지 추가
    private class DayDecorator(private val date: CalendarDay, private val context: Context) : DayViewDecorator {

        private val drawable = context.resources.getDrawable(R.drawable.bg_calendar_date1)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == date
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(drawable)
        }
    }



    companion object {
        fun newInstance() = MyPageFragment()
    }
}
