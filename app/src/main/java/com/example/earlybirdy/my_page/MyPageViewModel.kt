package com.example.earlybirdy.my_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.earlybirdy.data.MyPageData
import com.example.earlybirdy.dto.AttendanceDto

class MyPageViewModel(private val userRepository : UserRepositoryImpl) : ViewModel() {

    val userData: LiveData<MyPageData> = userRepository.myPageUserData
    val attendanceData : LiveData<List<AttendanceDto>> = userRepository.attendanceListData

    fun getMyPageUserData(userId : String) {
        userRepository.getMyPageUserData(userId)
    }

    fun getAttendanceData(userId : String) {
        userRepository.getAttendanceData(userId)
    }

}