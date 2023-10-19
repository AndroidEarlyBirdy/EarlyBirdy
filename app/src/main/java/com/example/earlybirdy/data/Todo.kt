package com.example.earlybirdy.data

import com.prolificinteractive.materialcalendarview.CalendarDay

data class Todo(
    var tid : String?,
    val date : CalendarDay,
    val title : String,
    val isChecked : Boolean
)
