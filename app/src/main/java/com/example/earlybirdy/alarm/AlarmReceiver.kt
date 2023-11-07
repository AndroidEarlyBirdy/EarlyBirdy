package com.example.earlybirdy.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.earlybirdy.R
import com.example.earlybirdy.main.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        sendNotifivation(context)
    }

    private fun sendNotifivation(context: Context){
        val pref = context.getSharedPreferences("alarmSetting", 0)

        val ringPref = pref.getBoolean("ringtoneSwitch", false)
        val vibePref = pref.getBoolean("vibeSwitch", false)
        Log.d("ring1", "${ringPref}")
        Log.d("vibe1", "${vibePref}")

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 26 버전 이상
            val channelId = "Alarm-channel"
            val channelName = "Alarm channel"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                // 채널에 다양한 정보 설정
                description = "Alarm channel Description"
                setShowBadge(true)
                // 소리 끄기 스위치의 상태에 따라 소리 설정
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(uri, audioAttributes)
                enableVibration(true)
            }
            // 채널을 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            // 채널을 이용하여 builder 생성
            builder = NotificationCompat.Builder(context, channelId)

        } else {
            // 26 버전 이하
            builder = NotificationCompat.Builder(context)
        }

        // 그림 넣기
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // 알림의 기본 정보
        builder.run {
            setSmallIcon(R.mipmap.ic_launcher)
            setWhen(System.currentTimeMillis())
            setContentTitle(
                "${pref.getInt("hour", 0).toString()} : ${pref.getInt("minute", 0).toString()}"
            )
            setContentText("Gooood Morning~~~")
            setLargeIcon(bitmap) // 큰 이미지
            addAction( // 버튼 추가
                R.mipmap.ic_launcher,
                "Action",
                pendingIntent
            )
        }
        manager.notify(1, builder.build())
    }
}