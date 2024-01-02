package com.nbcproject.earlybirdy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeViewModel : ViewModel() {
    // LiveData 정의
    private val _emptyListMessageVisibility = MutableLiveData<Int>()
    val emptyListMessageVisibility: LiveData<Int> = _emptyListMessageVisibility

    private val _leftArrowVisibility = MutableLiveData<Int>()
    val leftArrowVisibility: LiveData<Int> = _leftArrowVisibility

    private val _rightArrowVisibility = MutableLiveData<Int>()
    val rightArrowVisibility: LiveData<Int> = _rightArrowVisibility

    // 빈 목록 메시지의 가시성을 업데이트하는 메소드
    fun updateEmptyListMessageVisibility(visibility: Int) {
        _emptyListMessageVisibility.value = visibility
    }

    // 왼쪽 화살표의 가시성을 업데이트하는 메소드
    fun updateLeftArrowVisibility(visibility: Int) {
        _leftArrowVisibility.value = visibility
    }

    // 오른쪽 화살표의 가시성을 업데이트하는 메소드
    fun updateRightArrowVisibility(visibility: Int) {
        _rightArrowVisibility.value = visibility
    }
}
