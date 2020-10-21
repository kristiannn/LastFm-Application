package com.neno.lastfmapp.network.utils

enum class LastFmPeriodParams(val tag: String)
{
    Overall("overall"),
    Week("7day"),
    Month("1month"),
    Quarter("3month"),
    HalfYear("6month"),
    Year("12month")
}