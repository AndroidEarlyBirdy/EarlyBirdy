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
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.earlybirdy.util.navigateToSettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MyPageFragment()
    }

    //Exp, Level Setting
    private var currentExp = 0
    private var currentLevel = 1

    //Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    //ViewModel
    private lateinit var myPageViewModel : MyPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        myPageViewModel = ViewModelProvider(this, MyPageViewModelFactory())[MyPageViewModel::class.java]

        getData()
        observeData()
        setListener()
    }

    //Repository에서 데이터 get
    private fun getData() {
        myPageViewModel.getMyPageUserData(user.uid)
        myPageViewModel.getAttendanceData(user.uid)
    }

    //LiveData 감지
    @SuppressLint("SetTextI18n")
    private fun observeData() {
        myPageViewModel.userData.observe(viewLifecycleOwner) { user->
            binding.tvNickname.text = user.nickname
            user.exp?.let { myPageViewModel.initializeUIWithLocalExp(it) }
        }

        myPageViewModel.attendanceData.observe(viewLifecycleOwner) {list ->
            val dateList = ArrayList<CalendarDay>()
            for(date in list) {
                convertStringToCalendarDay(date.date)?.let { dateList.add(it) }
            }
            Log.d("dateList",dateList.toString())
            binding.calendarView.addDecorator(Decorator(dateList, requireContext()))
        }

        myPageViewModel.exp.observe(viewLifecycleOwner) {exp ->
            binding.pbExp.progress = exp
            currentExp = exp
            updateExpUI()
        }

        myPageViewModel.level.observe(viewLifecycleOwner) {level ->
            binding.tvLevel.text = "레벨 $level"
            binding.pbExp.max = calculateMaxExpForLevel(level)
            currentLevel = level
            updateLevelImage(level)
            updateExpUI()
        }
    }

    //리스너 부착
    private fun setListener() {
        binding.ivSetting.setOnClickListener {
            navigateToSettingActivity(requireContext())
        }
    }

    // 마이페이지를 눌렀을 때 보이는 레벨, 경험치, 프로그레스 바
    @SuppressLint("SetTextI18n")
    private fun updateExpUI() {
        binding.tvExperience.text = "$currentExp / ${calculateMaxExpForLevel(currentLevel)} xp"
    }

    //레벨 범위에 따른 인장 설정
    private fun updateLevelImage(level : Int) {
        when (level) {
            in 1..10 -> binding.ivProfileBorder1.setBackgroundResource(R.drawable.ic_insignia1)
            in 11..20 -> binding.ivProfileBorder1.setBackgroundResource(R.drawable.ic_insignia2)
            in 21..30 -> binding.ivProfileBorder1.setBackgroundResource(R.drawable.ic_insignia3)
            in 31..40 -> binding.ivProfileBorder1.setBackgroundResource(R.drawable.ic_insignia4)
            else -> binding.ivProfileBorder1.setBackgroundResource(R.drawable.ic_insignia5)
        }
    }

    // 레벨 당 경험치 양 계산
    private fun calculateMaxExpForLevel(level: Int): Int {
        return when (level) {
            in 1..10 -> 100
            in 11..20 -> 150
            in 21..30 -> 200
            in 31..40 -> 250
            else -> 300
        }
    }

    //String 값을 CalendarDay로 전환
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    //날짜 Custom Decorator
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
}