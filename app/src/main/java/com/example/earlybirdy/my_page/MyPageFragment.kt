package com.example.earlybirdy.my_page

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.FragmentMyPageBinding
import com.example.earlybirdy.util.navigateToEditProfileActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.earlybirdy.util.navigateToSettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MyPageFragment()
    }

    //Firebase
    private lateinit var calendarView: MaterialCalendarView
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    //ViewModel
    private lateinit var myPageViewModel: MyPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        myPageViewModel =
            ViewModelProvider(this, MyPageViewModelFactory())[MyPageViewModel::class.java]

        getData()
        observeData()
        setListener()
        loadAttendanceData()
    }

    //Repository에서 데이터 get
    private fun getData() {
        myPageViewModel.getMyPageUserData(user.uid)
        Log.d("겟",user.uid )
        myPageViewModel.getAttendanceData(user.uid)
    }

    //LiveData 감지
    @SuppressLint("SetTextI18n")
    private fun observeData() {
        myPageViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.tvNickname.text = user.nickname
            user.exp?.let { myPageViewModel.initializeUIWithLocalExp(it) }
            user.profile?.let { setProfileImage(it) }
        }

//        myPageViewModel.attendanceData.observe(viewLifecycleOwner) {list ->
//            val dateList = ArrayList<CalendarDay>()
//            for(date in list) {
//                convertStringToCalendarDay(date.date)?.let { dateList.add(it) }
//            }
//            Log.d("dateList",dateList.toString())
//            binding.calendarView.addDecorator(Decorator(dateList, requireContext()))
//        }

        myPageViewModel.expMap.observe(viewLifecycleOwner) { map ->
            val currentExp = map["exp"]
            val currentLevel = map["level"]
            val currentMapExp = map["maxExp"]

            binding.pbExp.progress = currentExp!!
            binding.tvLevel.text = "레벨 $currentLevel"
            binding.pbExp.max = currentMapExp!!
            binding.tvExperience.text = "$currentExp / $currentMapExp xp"

            updateLevelImage(currentLevel!!)
        }
    }

    //리스너 부착
    private fun setListener() {
        binding.ivSetting.setOnClickListener {
            navigateToSettingActivity(requireContext())
        }
        binding.ivEditProfile.setOnClickListener {
            navigateToEditProfileActivity(requireContext())
        }
    }

    //레벨 범위에 따른 인장 설정
    private fun updateLevelImage(level: Int) {
        when (level) {
            in 1..10 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia1)
            in 11..20 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia2)
            in 21..30 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia3)
            in 31..40 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia4)
            else -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia5)
        }
    }

    private fun setProfileImage(profileNum: Int) {
        when (profileNum) {
            1 -> binding.ivProfile.setImageResource(R.drawable.img_profile_man1)
            2 -> binding.ivProfile.setImageResource(R.drawable.img_profile_woman1)
            3 -> binding.ivProfile.setImageResource(R.drawable.img_profile_man2)
            4 -> binding.ivProfile.setImageResource(R.drawable.img_profile_woman2)
            else -> binding.ivProfile.setImageResource(R.drawable.img_profile_add)
        }
    }

//    //String 값을 CalendarDay로 전환
//    private fun convertStringToCalendarDay(dateString: String): CalendarDay? {
//        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
//        return try {
//            val date = dateFormat.parse(dateString)
//            val calendar = Calendar.getInstance()
//            if (date != null) {
//                calendar.time = date
//            }
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH) + 1
//            val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//            CalendarDay.from(year, month, day)
//        } catch (e: Exception) {
//            null
//        }
//    }


    private fun loadAttendanceData() {

        var userId = "H15cV1QjDBfiqF02izuq5JLUM553"
        firestore.collection("UserDto")
            .document(userId)
            .collection("Attendance")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // 에러 처리
                    return@addSnapshotListener
                }

                val attendanceDates = mutableListOf<CalendarDay>()
                val goalAchievedDates = mutableMapOf<CalendarDay, Int>()
                for (document in querySnapshot!!) {
                    val timestamp = document.getTimestamp("date")
                    if (timestamp != null) {
                        val calendarDay = parseDate(timestamp)
                        attendanceDates.add(calendarDay)
                        goalAchievedDates[calendarDay] = 0
                    }
                }

                firestore.collection("UserDto")
                    .document(userId)
                    .collection("MyGoal")
                    .whereEqualTo("check", true)
                    .addSnapshotListener { querySnapshot, error ->
                        if (error != null) {
                            // 에러 처리
                            return@addSnapshotListener
                        }

                        goalAchievedDates.clear()

                        for (document in querySnapshot!!) {
                            val goalDate = document.getTimestamp("date")
                            if (goalDate != null) {
                                val calendarDay = parseDate(goalDate)
                                goalAchievedDates[calendarDay] =
                                    goalAchievedDates[calendarDay]?.toInt() ?: 0 + 1
                            }

                            for (day in attendanceDates) {
                                val decoratorDrawableResId = when {
                                    goalAchievedDates[day]?.toInt() ?: 0 >= 3 -> R.drawable.bg_calendar_date4
                                    goalAchievedDates[day]?.toInt() ?: 0 == 2 -> R.drawable.bg_calendar_date3
                                    goalAchievedDates[day]?.toInt() ?: 0 == 1 -> R.drawable.bg_calendar_date2
                                    else -> R.drawable.bg_calendar_date1
                                }
                                val dayDecorator =
                                    DayDecorator(day, requireContext(), decoratorDrawableResId)
                                calendarView.addDecorator(dayDecorator)
                            }
                        }
                    }
            }
    }

    private fun parseDate(timestamp: com.google.firebase.Timestamp): CalendarDay {
        // Timestamp에서 Date 객체를 가져오고
        val date = timestamp.toDate()

        // Date 객체를 Calendar로 변환
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        // Calendar를 사용하여 CalendarDay로 변환
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return CalendarDay.from(year, month, day)
    }


    // 날짜에 따라 배경 이미지 추가
    // DayDecorator 클래스 수정
    private class DayDecorator(
        private val date: CalendarDay,
        private val context: Context,
        private val decoratorDrawableResId: Int
    ) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == date
        }

        override fun decorate(view: DayViewFacade) {

            view.setBackgroundDrawable(context.resources.getDrawable(decoratorDrawableResId))

        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

//    //날짜 Custom Decorator
//    class Decorator(dates: List<CalendarDay>, context: Context) : DayViewDecorator {
//
//        private val selectedDates = dates
//
//        @SuppressLint("UseCompatLoadingForDrawables")
//        private val drawable = context.getDrawable(R.drawable.bg_calendar_date)
//        override fun shouldDecorate(day: CalendarDay?): Boolean {
//            return selectedDates.contains(day)
//        }
//
//        override fun decorate(view: DayViewFacade?) {
//            if (drawable != null) {
//                view?.setBackgroundDrawable(drawable)
//            }
//        }
//    }
}