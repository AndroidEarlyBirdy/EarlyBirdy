package com.example.earlybirdy.dto

data class UserDto(

    val uid: String,
    val profile: String? = null,
    val nickname: String,
    val email: String,
    val password: String,
    val exp : Int,
)
