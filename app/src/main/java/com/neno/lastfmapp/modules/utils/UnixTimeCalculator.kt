package com.neno.lastfmapp.modules.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class UnixTimeCalculator(private val calendar: Calendar, private val dateFormatter: DateFormat) : TimeCalculator
{
    override fun convertToTime(timestamp: Int): String
    {
        calendar.timeInMillis = timestamp * 1000L

        return dateFormatter.format(calendar.time)
    }
}
