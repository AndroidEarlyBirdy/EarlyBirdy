package com.example.earlybirdy.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.earlybirdy.databinding.ActivityAlarmBinding
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAlarmBinding.inflate(layoutInflater) }

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pref = getSharedPreferences("alarmTime", 0)

        if (pref != null) {
            binding.tpSetTime.hour = pref.getInt("hour", 4)
            binding.tpSetTime.minute = pref.getInt("minute", 0)
            binding.switchAlarm.isChecked = pref.getBoolean("alarmSwitch", false)
        }else{
            binding.tpSetTime.hour = 4
            binding.tpSetTime.minute = 0
            binding.switchAlarm.isChecked = false
        }

        setTimeChangedListener()

        //오전 오후 설정
        binding.tpSetTime.setIs24HourView(true)

        // 취소 버튼 누르면 액티비티 종료
        binding.tvCancel.setOnClickListener {
            finish()
        }

        // 저장버튼 누르면 상태 저장
        binding.tvSave.setOnClickListener {
            saveTime()

            if (!binding.switchAlarm.isChecked){
                //알람 매니저 함수
                sendAlarm()
            }
            finish()
        }
    }

    private fun setTimeChangedListener() {
        binding.tpSetTime.setOnTimeChangedListener { view, hourOfDay, minute ->
//            if (hourOfDay > 7 || hourOfDay < 4) {
//                binding.tpSetTime.hour = 4
//
//            }
        }
    }

    private fun saveTime() {
        val pref = getSharedPreferences("alarmSetting", Context.MODE_PRIVATE)
        val editTime = pref.edit()

        editTime.putInt("hour", binding.tpSetTime.hour)
        editTime.putInt("minute", binding.tpSetTime.minute)

        editTime.putBoolean("alarmSwitch", binding.switchAlarm.isChecked)
        editTime.putBoolean("ringtoneSwitch", binding.switchRingtone.isChecked)
        editTime.putBoolean("vibeSwitch", binding.switchVibe.isChecked)

        Log.d("alarmSwitch", "${binding.switchAlarm.isChecked}")
        Log.d("ringtoneSwitch", "${binding.switchRingtone.isChecked}")
        Log.d("vibeSwitch", "${binding.switchVibe.isChecked}")

        editTime.apply()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun sendAlarm(){
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        Log.d("alarmManager", "${alarmManager}")

        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, binding.tpSetTime.hour)
        calendar.set(Calendar.MINUTE, binding.tpSetTime.minute)

        Log.d("hour", "${binding.tpSetTime.hour}")
        Log.d("minute", "${binding.tpSetTime.minute}")

        calendar.set(Calendar.SECOND, 0)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}