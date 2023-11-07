package com.nbcproject.earlybirdy.repository

interface UserRepository {
    fun getMyPageUserData(userId : String)
    fun getAttendanceData(userId: String)
}