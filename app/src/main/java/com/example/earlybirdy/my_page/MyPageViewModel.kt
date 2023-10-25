package com.example.earlybirdy.my_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.earlybirdy.data.MyPageData
import com.example.earlybirdy.dto.AttendanceDto
import com.example.earlybirdy.repository.UserRepositoryImpl

class MyPageViewModel(private val userRepository : UserRepositoryImpl) : ViewModel() {

    val userData: LiveData<MyPageData> = userRepository.myPageUserData
    val attendanceData : LiveData<List<AttendanceDto>> = userRepository.attendanceListData

    private val _expMap = MutableLiveData<Map<String,Int>>()
    val expMap get() = _expMap

    fun getMyPageUserData(userId : String) {
        userRepository.getMyPageUserData(userId)
    }

    fun getAttendanceData(userId : String) {
        userRepository.getAttendanceData(userId)
    }

    fun initializeUIWithLocalExp(localExp: Int) {
        var tempExp = localExp
        var tempLevel = 1
        while (tempExp >= calculateMaxExpForLevel(tempLevel)) {
            tempExp -= calculateMaxExpForLevel(tempLevel)
            tempLevel++
        }

        val expMap = mapOf("exp" to tempExp, "level" to tempLevel, "maxExp" to calculateMaxExpForLevel(tempLevel))
        _expMap.value = expMap

    }

    private fun calculateMaxExpForLevel(level: Int) : Int{
        return when (level) {
            in 1..10 -> 100
            in 11..20 -> 150
            in 21..30 -> 200
            in 31..40 -> 250
            else -> 300
        }
    }

}