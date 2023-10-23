package com.example.earlybirdy.my_page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.FragmentMyPageBinding
import com.example.earlybirdy.dto.AttendanceDto
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.earlybirdy.home.HomeViewModel
import com.example.earlybirdy.setting.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private var currentExp = 0
    private var currentLevel = 1

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var myPageViewModel : MyPageViewModel

    // 나머지 코드 생략
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        fireStore = FirebaseFirestore.getInstance()
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPageViewModel = ViewModelProvider(this, MyPageViewModelFactory())[MyPageViewModel::class.java]

        myPageViewModel.getMyPageUserData("vlKOuWtxe1b6flDCwHoPRwOYsWt2")
        myPageViewModel.getAttendanceData("vlKOuWtxe1b6flDCwHoPRwOYsWt2")

        myPageViewModel.userData.observe(viewLifecycleOwner) { user->
            binding.tvNickname.text = user.nickname
            user.exp?.let { initializeUIWithLocalExp(it) }
        }

        myPageViewModel.attendanceData.observe(viewLifecycleOwner) {list ->
            val dateList = ArrayList<CalendarDay>()
            for(date in list) {
                convertStringToCalendarDay(date.date)?.let { dateList.add(it) }
            }
            Log.d("dateList",dateList.toString())
            binding.calendarView.addDecorator(Decorator(dateList, requireContext()))
        }

        binding.ivSetting.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
    }


    // 마이페이지를 눌렀을 때 보이는 레벨, 경험치, 프로그레스 바
    private fun updateExpUI() {
        binding.tvLevel.text = "레벨 $currentLevel"
        binding.pbExp.max = calculateMaxExpForLevel(currentLevel)
        binding.pbExp.progress = currentExp
        binding.tvExperience.text = "$currentExp / ${calculateMaxExpForLevel(currentLevel)} xp"
    }

    //레벨 범위에 따른 인장 설정
    private fun updateLevelImage() {
        when (currentLevel) {
            in 1..10 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia1)
            in 11..20 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia2)
            in 21..30 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia3)
            in 31..40 -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia4)
            else -> binding.ivProfileBorder1.setImageResource(R.drawable.ic_insignia5)
        }
    }

    // 레벨 당 경험치 량
    private fun calculateMaxExpForLevel(level: Int): Int {
        return when (level) {
            in 1..10 -> 100
            in 11..20 -> 150
            in 21..30 -> 200
            in 31..40 -> 250
            else -> 300
        }
    }

    // localExp의 값에 따라 경험치와 레벨 변경
    private fun initializeUIWithLocalExp(localExp: Int) {
        var tempExp = localExp
        var tempLevel = 1
        while (tempExp >= calculateMaxExpForLevel(tempLevel)) {
            tempExp -= calculateMaxExpForLevel(tempLevel)
            tempLevel++
        }
        currentExp = tempExp
        currentLevel = tempLevel
        updateExpUI()
        updateLevelImage()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = MyPageFragment()
    }

    //기상 성공한 날짜 Custom
    class Decorator(dates: List<CalendarDay>, context: Context) : DayViewDecorator {

        private val selectedDates = dates

        @SuppressLint("UseCompatLoadingForDrawables")
        private val drawable = context.getDrawable(R.drawable.bg_calendar_date)
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return selectedDates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            if (drawable != null) {
                view?.setBackgroundDrawable(drawable)
            }
        }
    }

    private fun convertStringToCalendarDay(dateString: String): CalendarDay? {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return try {
            val date = dateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            CalendarDay.from(year, month, day)
        } catch (e: Exception) {
            null
        }
    }
}