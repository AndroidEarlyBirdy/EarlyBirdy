package com.example.earlybirdy.alarm

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


        // 취소 버튼 누르면 액티비티 종료
        binding.tvCancel.setOnClickListener {
            finish()
        }

        // 저장버튼 누르면 상태 저장
        binding.tvSave.setOnClickListener {

            navigateToMainActivity(this)
            finish()
        }
    }

    private fun setTime() {
        binding.tpSetTime.setOnTimeChangedListener { view, hourOfDay, minute ->

        }
    }

    private fun saveTime(){
//        val pref = getPreferences("alarm time", 0)

    }

}
