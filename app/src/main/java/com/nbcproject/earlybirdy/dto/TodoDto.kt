package com.nbcproject.earlybirdy.dto

import com.google.firebase.Timestamp

data class TodoDto(
    val Date : Timestamp? = null,
    val IsChecked : Boolean? = null,
    var Title : String = "",
    var TodoId : String? = ""
)
