package com.example.earlybirdy.my_page

import android.graphics.Color
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.MetricAffectingSpan
import android.text.style.UpdateAppearance

class BackgroundSpan(private val color: Int) : CharacterStyle(), UpdateAppearance {

    override fun updateDrawState(ds: TextPaint) {
        ds.bgColor = color
    }

    fun updateMeasureState(p: TextPaint) {
        p.bgColor = color
    }
}
