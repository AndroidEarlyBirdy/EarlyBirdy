package com.example.earlybirdy.my_page

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.FragmentMyPageBinding
import com.example.earlybirdy.dto.AttendanceDto
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.example.earlybirdy.home.HomeViewModel
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
    private lateinit var homeViewModel: HomeViewModel

    private var localExp = 3450
    private var currentExp = 0
    private var currentLevel = 1

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private var nickname : String? = ""
    private var exp : Int? = 0
    private var profile : Int? = 0

    private var getList = ArrayList<AttendanceDto>()
    private var dateList2 = ArrayList<CalendarDay>()

    private val dateList = mutableListOf(
        CalendarDay.from(2023, 10, 14),
        CalendarDay.from(2023, 10, 15),
        CalendarDay.from(2023, 10, 20)
    )

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



        // ViewModel 초기화
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

//        // LiveData를 관찰하여 데이터 업데이트
//        homeViewModel.sharedData.observe(viewLifecycleOwner) { data ->
//            binding.tvSharedData.text = data
//        }

//        fireStore.collection("UserDto").document("vlKOuWtxe1b6flDCwHoPRwOYsWt2").get()
//            .addOnSuccessListener {document ->
//                if (document != null) {
//                    nickname = document.getString("nickname")
//                    exp = document.getLong("exp")?.toInt()
//                    profile = document.getLong("profile")?.toInt()
//                }
//                v
//            }

        fireStore.collection("UserDto").document("vlKOuWtxe1b6flDCwHoPRwOYsWt2").
        addSnapshotListener { value, _ ->
            if (value != null) {
                nickname = value.getString("nickname")
                exp = value.getLong("exp")?.toInt()
                profile = value.getLong("profile")?.toInt()
            }
            exp?.let { initializeUIWithLocalExp(it) }
            binding.tvNickname.text = nickname
        }

        fireStore.collection("UserDto").document("vlKOuWtxe1b6flDCwHoPRwOYsWt2").collection("Attendance")
            .addSnapshotListener {value, _ ->
                for(snapshot in value!!.documents) {
                    var item = snapshot.toObject(AttendanceDto::class.java)
                    if (item != null) {
                        val attendence = AttendanceDto(
                            item.AttendanceId,
                            item.date
                        )
                        getList.add(attendence)
                    }
                }
                for(date in getList) {
                    convertStringToCalendarDay(date.date)?.let { dateList2.add(it) }
                }
                setCalendar()
            }




    }

    private fun setCalendar() = with(binding) {
        calendarView.addDecorator(Decorator(dateList2, requireContext()))
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

    private var imgBorder: Int = R.drawable.ic_insignia1
    fun expBorder(exp: Int) {
        imgBorder = when {
            exp in 0..1000 -> R.drawable.ic_insignia1
            exp in 1001..2500 -> R.drawable.ic_insignia2
            exp in 2501..4500 -> R.drawable.ic_insignia3
            exp in 4501..7500 -> R.drawable.ic_insignia4
            exp > 7500 -> R.drawable.ic_insignia5
            else -> R.drawable.ic_insignia1
        }
    }

    //경험치에 따라 테두리
    fun expLevel(exp: Int): Int {
        return when (exp) {
            in 0..1000 -> 1 + (exp - 1) / 100
            in 1001..2500 -> 11 + (exp - 1001) / 150
            in 2501..4500 -> 21 + (exp - 2501) / 200
            in 4501..7500 -> 31 + (exp - 4501) / 300
            else -> 41 + (exp - 7501) / 300
        }
    }

    // expLevel : 경험치값에 따라 레벨을 리턴
    fun expBar(level: Int): Int {
        return when {
            level in 1..10 -> 100 * level
            level in 21..30 -> 1000 + 150 * (level - 20)
            level in 31..40 -> 2500 + 200 * (level - 30)
            level in 41..50 -> 4500 + 300 * (level - 40)
            level >= 51 -> 7500 + 300 * (level - 50)
            else -> 0
        }
    }

    // expBar 레벨에 따라 경험치 총량을 리턴
    fun expBar2(level: Int): Int {
        return when {
            level in 1..10 -> 100
            level in 21..30 -> 150
            level in 31..40 -> 250
            level in 41..50 -> 300
            level >= 51 -> 300
            else -> 0
        }
    }

    // expBar2 레벨업에 필요한 경험치량을 리턴
    fun restOfExp(exp: Int): Int {
        return when {
            exp in 0..1000 -> exp % 100
            exp in 1001..2500 -> (exp - 1000) % 150
            exp in 2501..4500 -> (exp - 2500) % 200
            exp in 4501..7500 -> (exp - 4500) % 300
            exp > 7500 -> (exp - 7500) % 300
            else -> 0
        }
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

    fun convertStringToCalendarDay(dateString: String): CalendarDay? {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        try {
            val date = dateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작하므로 +1 해야 합니다.
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            return CalendarDay.from(year, month, day)
        } catch (e: Exception) {
            // 날짜 파싱에 실패하면 null을 반환하거나 오류 처리를 수행할 수 있습니다.
            return null
        }
    }

}