package com.example.earlybirdy.alarm

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityAlarmBinding
import com.example.earlybirdy.databinding.ActivitySigninBinding
import com.example.earlybirdy.util.navigateToMainActivity
import java.text.SimpleDateFormat
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAlarmBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setTimeChangedListener()

        //오전 오후 설정
        binding.tpSetTime.setIs24HourView(true)

        // 취소 버튼 누르면 액티비티 종료
        binding.tvCancel.setOnClickListener {
            finish()
        }

        // 저장버튼 누르면 상태 저장
        binding.tvSave.setOnClickListener {
            saveTime()
//            setNotification()
            navigateToMainActivity(this)
            finish()
        }
    }
//    private fun setTimeChangedListener() {
//        binding.tpSetTime.setOnTimeChangedListener { view, hourOfDay, minute ->
//            Log.d("hour", "${hourOfDay}")
//            if (hourOfDay > 7 || hourOfDay < 4){
//                binding.tpSetTime.hour = 4
//            }
//        }
//    }

    private fun saveTime(){
        val pref = getSharedPreferences("alarmTime",0)
        val edit = pref.edit()

        edit.putInt("hour", binding.tpSetTime.hour)
        edit.putInt("min", binding.tpSetTime.minute)

        edit.apply()
    }

    private fun setNotification() {

    }
}
