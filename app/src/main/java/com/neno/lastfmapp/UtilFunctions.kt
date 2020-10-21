package com.neno.lastfmapp

import android.content.res.Resources
import java.util.concurrent.TimeUnit

inline val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline val Long.msToTime: String
    get()
    {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)

        return String.format("$minutes minutes, $seconds seconds")
    }

inline val String.toQuery: String
    get()
    {
        return this.replace(" ", "+")
    }