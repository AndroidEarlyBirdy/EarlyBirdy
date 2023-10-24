package com.example.earlybirdy.my_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.earlybirdy.data.MyPageData
import com.example.earlybirdy.dto.AttendanceDto
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl : UserRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    private var _myPageUserData = MutableLiveData<MyPageData>()
    val myPageUserData : LiveData<MyPageData> get() =  _myPageUserData

    private var _attendanceListData = MutableLiveData<List<AttendanceDto>>()
    val attendanceListData : LiveData<List<AttendanceDto>> get() = _attendanceListData

    override fun getMyPageUserData(userId: String) {
        var user = MyPageData()
        fireStore.collection("UserDto").document(userId).
        addSnapshotListener { value, _ ->
            if (value != null) {
                user.nickname = value.getString("nickname")
                user.exp = value.getLong("exp")?.toInt()
                user.profile = value.getLong("profile")?.toInt()
            }
            _myPageUserData.value = user
        }
    }

    override fun getAttendanceData(userId: String) {
        fireStore.collection("UserDto").document(userId).collection("Attendance").
        addSnapshotListener { value, _ ->
            val demoList = ArrayList<AttendanceDto>()
                for (snapshot in value!!.documents) {
                    var item = snapshot.toObject(AttendanceDto::class.java)
                    if (item != null) {
                        val attendance = AttendanceDto(
                            item.AttendanceId,
                            item.date
                        )
                        demoList.add(attendance)
                    }
                }

            _attendanceListData.value = demoList

        }
    }
}