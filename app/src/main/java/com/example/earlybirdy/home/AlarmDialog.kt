package com.example.earlybirdy.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.DialogPreference
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.example.earlybirdy.alarm.AlarmReceiver
import com.example.earlybirdy.databinding.DialogAlarmBinding
import java.util.Calendar

class AlarmDialog(context: Context):Dialog(context) {
    private val binding by lazy {DialogAlarmBinding.inflate(layoutInflater) }

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pref = context.getSharedPreferences("alarmSetting", 0)

        if (pref != null) {
            binding.tpSetTime.hour = pref.getInt("hour", 4)
            binding.tpSetTime.minute = pref.getInt("minute", 0)

            binding.switchAlarm.isChecked = pref.getBoolean("alarmSwitch", false)
        }else{
            binding.tpSetTime.hour = 4
            binding.tpSetTime.minute = 0

            binding.switchAlarm.isChecked  = false
        }

        setTimeChangedListener()

        //오전 오후 설정
        binding.tpSetTime.setIs24HourView(true)

        // 취소 버튼 누르면 액티비티 종료
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        // 저장버튼 누르면 상태 저장
        binding.tvSave.setOnClickListener {
            saveTime()



            // 알람 끄기 스위치의 상태에 따라 알람 울림 여부 변경
            if (!this.binding.switchAlarm.isChecked){
                //알람 매니저 함수
                sendAlarm()
            }
            dismiss()
        }
    }

    private fun setTimeChangedListener() {
        binding.tpSetTime.setOnTimeChangedListener { view, hourOfDay, minute ->
//            if (hourOfDay > 7 || hourOfDay < 4) {
//                binding.tpSetTime.hour = 4
//
//            }
//            if (hourOfDay == 7){
//                binding.tpSetTime.minute = 0
//            }
        }
    }

    private fun saveTime() {
        val dPref = context.getSharedPreferences("alarmSetting", Context.MODE_PRIVATE)
        val editTime = dPref.edit()

        editTime.putInt("hour", binding.tpSetTime.hour)
        editTime.putInt("minute", binding.tpSetTime.minute)

        editTime.putBoolean("alarmSwitch", binding.switchAlarm.isChecked)

        editTime.apply()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun sendAlarm(){
        alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, binding.tpSetTime.hour)
        calendar.set(Calendar.MINUTE, binding.tpSetTime.minute)

        calendar.set(Calendar.SECOND, 0)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

}