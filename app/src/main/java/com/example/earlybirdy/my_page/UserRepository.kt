package com.example.earlybirdy.my_page

import com.example.earlybirdy.data.MyPageData

interface UserRepository {
    fun getMyPageUserData(userId : String)
    fun getAttendanceData(userId: String)
}