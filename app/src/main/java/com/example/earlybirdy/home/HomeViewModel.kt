package com.example.earlybirdy.home
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _sharedData = MutableLiveData<String>()
    val sharedData: LiveData<String> get() = _sharedData

    fun setSharedData(data: Int) {
        _sharedData.value = data.toString()
    }
}
