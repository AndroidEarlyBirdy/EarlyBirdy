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
import com.example.earlybirdy.home.HomeViewModel

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    val dateList = mutableListOf(
        CalendarDay.from(2023,10,14),
        CalendarDay.from(2023,10,15),
        CalendarDay.from(2023,10,20)
    )

    // 나머지 코드 생략
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCalendar()
    }

    private fun setCalendar() = with(binding) {
        calendarView.addDecorator(Decorator(dateList,requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        // LiveData를 관찰하여 데이터 업데이트
        homeViewModel.sharedData.observe(viewLifecycleOwner) { data ->
            binding.tvSharedData.text = data
        }
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
    class Decorator(dates : List<CalendarDay>,context : Context) : DayViewDecorator {

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