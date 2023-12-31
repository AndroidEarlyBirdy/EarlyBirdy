package com.nbcproject.earlybirdy.my_page

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nbcproject.earlybirdy.data.MyPageData
import com.nbcproject.earlybirdy.util.navigateToEditProfileActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.nbcproject.earlybirdy.util.navigateToSettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nbcproject.earlybirdy.R
import com.nbcproject.earlybirdy.databinding.FragmentMyPageBinding
import com.nbcproject.earlybirdy.my_page.viewmodel.MyPageViewModel
import com.nbcproject.earlybirdy.my_page.viewmodel.MyPageViewModelFactory
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.Calendar
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
    private lateinit var userId: String
    private lateinit var myUser: MyPageData

    //ViewModel
    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var myPageContext : Context

    override fun onAttach(context: Context) {
        myPageContext = context
        super.onAttach(context)
    }

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
        userId = user.uid
        myPageViewModel =
            ViewModelProvider(this, MyPageViewModelFactory())[MyPageViewModel::class.java]

        getData()
        setListener()
        loadAttendanceData()
    }

    //Repository에서 데이터 get
    private fun getData() {
        firestore.collection("UserDto").document(userId).addSnapshotListener { value, _ ->
            myUser = MyPageData()
            if (value != null) {
                myUser.nickname = value.getString("nickname")
                myUser.exp = value.getLong("exp")?.toInt()
                myUser.profile = value.getLong("profile")?.toInt()

                // set user info to view
                binding.tvNickname.text = myUser.nickname
                val map = initializeUIWithLocalExp(myUser.exp ?: 0)
                val currentExp = map["exp"]
                val currentLevel = map["level"]
                val currentMapExp = map["maxExp"]

                binding.pbExp.progress = currentExp!!
                binding.tvLevel.text = "레벨 $currentLevel"
                binding.pbExp.max = currentMapExp!!
                binding.tvExperience.text = "$currentExp / $currentMapExp xp"

                myUser.profile?.let { setProfileImage(it) }
                updateLevelImage(currentLevel!!)
            }
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
        Log.d("레벨", level.toString())
        when (level) {

            in 1..10 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia1)
            in 11..20 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia2)
            in 21..30 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia3)
            in 31..40 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia4)
            else -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia5)
        }

    }

    private fun setProfileImage(profileNum: Int) {
        val uid = user.uid
        val profileImageRef = firestore.collection("UserDto").document(uid)
        when (profileNum) {
            1 -> binding.ivProfile.setImageResource(R.drawable.ic_person1)
            2 -> binding.ivProfile.setImageResource(R.drawable.ic_person2)
            3 -> binding.ivProfile.setImageResource(R.drawable.ic_person3)
            4 -> binding.ivProfile.setImageResource(R.drawable.ic_person4)
            else -> binding.ivProfile.setImageResource(R.drawable.ic_person1)
        }
    }

    private fun loadAttendanceData() {
        firestore.collection("UserDto")
            .document(user.uid)
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
                        if (calendarDay != null) {
                            attendanceDates.add(calendarDay)
                            goalAchievedDates[calendarDay] = 0
                            Log.d("출석", document.getTimestamp("date").toString())
                        }
                    }
                }

                firestore.collection("UserDto")
                    .document(user.uid)
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
                                if (calendarDay != null) {
                                    goalAchievedDates[calendarDay] =
                                        (goalAchievedDates[calendarDay] ?: 0) + 1 //연산자 우선순위
                                    Log.d("확인색상", goalAchievedDates[calendarDay].toString())
                                    Log.d("확인색상", calendarDay.toString())
                                }
                            }
                        }

                        // DayDecorator 클래스 사용
                        val decorators = mutableListOf<DayDecorator>()
                        for (day in attendanceDates) {
                            Log.d("색상", goalAchievedDates[day].toString())
                            val decoratorDrawableResId = when {
                                goalAchievedDates[day]?.toInt() ?: 0 >= 3 -> R.drawable.bg_calendar_date4
                                goalAchievedDates[day]?.toInt() ?: 0 == 2 -> R.drawable.bg_calendar_date3
                                goalAchievedDates[day]?.toInt() ?: 0 == 1 -> R.drawable.bg_calendar_date2
                                else -> R.drawable.bg_calendar_date1
                            }
                            decorators.add(DayDecorator(day, myPageContext, decoratorDrawableResId))
                        }
                        calendarView.addDecorators(decorators)
                    }
            }
    }

    private fun parseDate(timestamp: com.google.firebase.Timestamp): CalendarDay? {
        try {
            val date = timestamp.toDate()
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return CalendarDay.from(year, month, day)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

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
            view.setSelectionDrawable(context.resources.getDrawable(decoratorDrawableResId))
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

// 경험치 관련 작업을 아래로 옮김
fun initializeUIWithLocalExp(localExp: Int): Map<String, Int> {
    var tempExp = localExp
    var tempLevel = 1
    while (tempExp >= calculateMaxExpForLevel(tempLevel)) {
        tempExp -= calculateMaxExpForLevel(tempLevel)
        tempLevel++
    }

    val expMap = mapOf("exp" to tempExp, "level" to tempLevel, "maxExp" to calculateMaxExpForLevel(tempLevel))
    return expMap
}

private fun calculateMaxExpForLevel(level: Int) : Int{
    return when (level) {
        in 1..10 -> 100
        in 11..20 -> 150
        in 21..30 -> 200
        in 31..40 -> 250
        else -> 300
    }
}