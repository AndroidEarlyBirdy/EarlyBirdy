package com.example.earlybirdy.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.earlybirdy.databinding.FragmentHomeBinding
import com.example.earlybirdy.util.navigateToAlarmActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        loatTimeDate()

        binding.ivGoAlarm.setOnClickListener {
            navigateToAlarmActivity(this.requireActivity())
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun loatTimeDate() {
        val pref: SharedPreferences = requireContext().getSharedPreferences("alarmTime", Context.MODE_PRIVATE)

        binding.tvAlarm.text = "${pref.getInt("hour", 0).toString()} : ${pref.getInt("min", 0).toString()}"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}