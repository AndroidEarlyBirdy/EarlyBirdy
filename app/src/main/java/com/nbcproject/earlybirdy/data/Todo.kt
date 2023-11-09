package com.nbcproject.earlybirdy.data

import com.prolificinteractive.materialcalendarview.CalendarDay

data class Todo(
    var tid : String?,
    val date : CalendarDay,
    var title : String,
    val isChecked : Boolean
)
