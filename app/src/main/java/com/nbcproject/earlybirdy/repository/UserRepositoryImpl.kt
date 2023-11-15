package com.nbcproject.earlybirdy.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nbcproject.earlybirdy.data.MyPageData
import com.nbcproject.earlybirdy.dto.AttendanceDto
import com.google.firebase.firestore.FirebaseFirestore
import com.nbcproject.earlybirdy.sealedclass.CheckDelete

class UserRepositoryImpl : UserRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    private var _myPageUserData = MutableLiveData<MyPageData>()
    val myPageUserData : LiveData<MyPageData> get() =  _myPageUserData

    private var _attendanceListData = MutableLiveData<List<AttendanceDto>>()
    val attendanceListData : LiveData<List<AttendanceDto>> get() = _attendanceListData

    private var _userEmail = MutableLiveData<String>()
    val userEmail : LiveData<String> get() = _userEmail

    private val _checkDeleteData = MutableLiveData<CheckDelete>()
    val checkDeleteData : LiveData<CheckDelete> get() = _checkDeleteData

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

    override fun getUserEmail(userId: String) {
        fireStore.collection("UserDto").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val loadEmail = document.getString("email") ?: ""
                    _userEmail.value = loadEmail
                }
            }
    }

    override fun deleteUserData(userId: String) {
        fireStore.collection("UserDto").document(userId)
            .delete()
            .addOnSuccessListener {
                _checkDeleteData.value = CheckDelete.DeleteSuccess
            }
    }
}