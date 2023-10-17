package com.example.earlybirdy.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.earlybirdy.create_plan.CreatePlanActivity
import com.example.earlybirdy.databinding.FragmentHomeBinding
import com.example.earlybirdy.util.navigateToAlarmActivity
import com.example.earlybirdy.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("UNREACHABLE_CODE")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("h:mm a", Locale.US)

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

        loatTimeDate()

        binding.ivGoAlarm.setOnClickListener {
            navigateToAlarmActivity(this.requireActivity())
        }

        binding.btnCreate.setOnClickListener {
            val intent = Intent(requireContext(), CreatePlanActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        binding.btnAttend.setOnClickListener {
            // tv_alarm 시간 가져오기 (가정)
            val alarmTime = "8:00 AM"
            // 현재 시간 가져오기
            val currentTime = getCurrentTime()
            // 현재 시간을 분 단위로 변환
            val currentMinutes = convertToMinutes(currentTime)
            // tv_alarm 시간을 분 단위로 변환
            val alarmMinutes = convertToMinutes(alarmTime)

            // sharedData에 데이터 저장
            val data = calculateProgress(alarmMinutes, currentMinutes)
            homeViewModel.setSharedData(data)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loatTimeDate() {
        val pref: SharedPreferences = requireContext().getSharedPreferences("alarmTime", Context.MODE_PRIVATE)

        binding.tvAlarm.text = "${pref.getInt("hour", 0).toString()} : ${pref.getInt("min", 0).toString()} AM"
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun convertToMinutes(time: String): Int {
        val date = dateFormat.parse(time)
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    }

    private fun calculateProgress(alarmMinutes: Int, currentMinutes: Int): Int {
        val timeDifference = currentMinutes - alarmMinutes

        return when {
            timeDifference in 0..5 -> 30
            timeDifference in 6..10 -> 20
            timeDifference in 11..15 -> 10
            else -> 1
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

}