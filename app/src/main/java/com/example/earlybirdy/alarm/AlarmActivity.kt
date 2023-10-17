package com.example.earlybirdy.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent.getActivity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.earlybirdy.R
import com.example.earlybirdy.alarm.AlarmContants.CHANNEL_ID
import com.example.earlybirdy.alarm.AlarmContants.CHANNEL_NAME
import com.example.earlybirdy.databinding.ActivityAlarmBinding
import com.example.earlybirdy.databinding.ActivitySigninBinding
import com.example.earlybirdy.main.MainActivity
import com.example.earlybirdy.util.navigateToMainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Random

class AlarmActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAlarmBinding.inflate(layoutInflater) }

    private var handler: Handler = Handler() // Notification delay적용을 위한 Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        setTimeChangedListener()

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
            createNotificationChannel()
//            navigateToMainActivity(this)
//            finish()
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

    private fun createNotificationChannel() {
        val pref = getSharedPreferences("alarmTime",0)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //26버전 이상
            val channelId="channel"
            val channelName="Number Channel"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply{
                description = "My Channel One Description"
                setShowBadge(true)
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(uri, audioAttributes)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(this, channelId)

        }else{
            builder = NotificationCompat.Builder(this)
        }

        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_logo)
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        builder.run{
            setSmallIcon(R.mipmap.ic_launcher) // 작은 아이콘
            setWhen(System.currentTimeMillis())
            setContentTitle("${pref.getInt("hour", 0).toString()} : ${pref.getInt("min", 0).toString()}") // 제목
            setContentText("일어나세요 용사여!") // 본문
//            setLargeIcon(bitmap)
            addAction(R.mipmap.ic_launcher,"Action",pendingIntent)
        }
        manager.notify(1,builder.build())

    }

//    private fun sendNotification() {
//        val pref = getSharedPreferences("alarmTime",0)
//
//        val title = "${pref.getInt("hour", 0).toString()} : ${pref.getInt("min", 0).toString()} AM"
//        val message = "일어나세요 용사여"
//
//        val intent = Intent(this, MainActivity::class.java)
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
//                as NotificationManager
//        val notificationID = 1
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(notificationManager)
//        }
//
//        val pendingIntent = getActivity(this, 0, intent, FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.drawable.ic_logo)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()
//
//        notificationManager.notify(notificationID, notification)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
//            description = "Channel Description"
//            enableLights(true)
//            lightColor = Color.GREEN
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
}
