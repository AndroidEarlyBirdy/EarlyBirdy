package com.example.earlybirdy.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import com.example.earlybirdy.R
import com.example.earlybirdy.databinding.ActivityAlarmBinding
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.main.SplashActivity
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAlarmBinding.inflate(layoutInflater) }

    private var alarmMgr: AlarmManager? = null
    private lateinit var pendingIntent: PendingIntent

    private var hour = 0
    private var minute = 0

    companion object {
        const val REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pref = getSharedPreferences("alarmTime", 0)

        setTimeChangedListener()

        if (pref != null) {
            //binding.tpSetTime.setOnTimeChangedListener(this)
        }


//        if (binding.switchAlarm.isChecked){
//            handler.postDelayed({
//                setNotification()
//            }, binding.tpSetTime.minute.toLong())
//        }

        //오전 오후 설정
        binding.tpSetTime.setIs24HourView(true)

        // 취소 버튼 누르면 액티비티 종료
        binding.tvCancel.setOnClickListener {
            finish()
        }

        // 저장버튼 누르면 상태 저장
        binding.tvSave.setOnClickListener {
            saveTime()
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

    @SuppressLint("ScheduleExactAlarm")
    private fun saveTime() {
        val pref = getSharedPreferences("alarmTime", 0)
        val edit = pref.edit()

        edit.putInt("hour", binding.tpSetTime.hour)
        edit.putInt("min", binding.tpSetTime.minute)

        edit.apply()

        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("alarmMgr", "$alarmMgr")
        pendingIntent = Intent(this, SplashActivity::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // Set the alarm to start at 8:30 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()

            set(Calendar.HOUR_OF_DAY, binding.tpSetTime.hour)
            set(Calendar.MINUTE, binding.tpSetTime.minute)

            Log.d("hour", "${binding.tpSetTime.hour}")
            Log.d("minute", "${binding.tpSetTime.minute}")

        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 20,
            pendingIntent
        )
    }
}

//        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager // as 뒤에 지정한 형 변환
//
//        hour = binding.tpSetTime.hour
//        minute = binding.tpSetTime.minute
//
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, hour)
//        calendar.set(Calendar.MINUTE, minute)
//
//        val currentTime = System.currentTimeMillis()
//        val triggerTime = currentTime + 10000
//
//        // 지정한 시간에 매일 알림
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            triggerTime,
//            pendingIntent
//        )
//        val currentTime = System.currentTimeMillis()
//        val triggerTime = currentTime + 10000 //알림 작동 시간 테스트로 10초 후로 지정
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)



//    private fun sendNotification() {
//        val pref = getSharedPreferences("alarmTime", 0)
//
//        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        val builder: NotificationCompat.Builder
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // 26 버전 이상
//            val channelId = "one-channel"
//            val channelName = "My Channel One"
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                if (!binding.switchAlarm.isChecked){
//                    NotificationManager.IMPORTANCE_DEFAULT
//                }else{
//                    NotificationManager.IMPORTANCE_LOW
//                }
//            ).apply {
//                // 채널에 다양한 정보 설정
//                description = "My Channel One Description"
//                setShowBadge(true)
//                val uri: Uri =
//                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) //소리 설정
//                val audioAttributes = AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_ALARM)
//                    .build()
//                setSound(uri, audioAttributes)
//                enableVibration(true) // 진동 설정
//            }
//            // 채널을 NotificationManager에 등록
//            manager.createNotificationChannel(channel)
//
//            // 채널을 이용하여 builder 생성
//            builder = NotificationCompat.Builder(this, channelId)
//
//        } else {
//            // 26 버전 이하
//            builder = NotificationCompat.Builder(this)
//        }
//
//        // 그림 넣기
//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_logo)
//        val intent = Intent(this, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        // 알림의 기본 정보
//        builder.run {
//            setSmallIcon(R.mipmap.ic_launcher)
//            setWhen(System.currentTimeMillis())
//            setContentTitle(
//                "${pref.getInt("hour", 0).toString()} : ${
//                    pref.getInt("min", 0).toString()
//                }"
//            )
//            setContentText("Gooood Morning~~~")
//            setLargeIcon(bitmap) // 큰 이미지
//            addAction( // 버튼 추가
//                R.mipmap.ic_launcher,
//                "Action",
//                pendingIntent
//            )
//        }
//        manager.notify(1, builder.build())
//    }

//    fun unregist(view: View) {
//        val intent = Intent(this, AlarmReceiver::class.java)
//        val pIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
//        alarmManager.cancel(pIntent)
//    }

