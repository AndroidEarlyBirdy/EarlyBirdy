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

    private val _exp = MutableLiveData<Int>()
    val exp : LiveData<Int> get() = _exp

    private val _level = MutableLiveData<Int>()
    val level : LiveData<Int> get() = _level

    private val _maxExp = MutableLiveData<Int>()
    val maxExp : LiveData<Int> get() = _maxExp

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
        _exp.value = tempExp
        _level.value = tempLevel
        _maxExp.value = calculateMaxExpForLevel(tempLevel)
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