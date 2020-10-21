package com.neno.lastfmapp.di

import android.annotation.SuppressLint
import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.modules.utils.TimeCalculator
import com.neno.lastfmapp.modules.utils.UnixTimeCalculator
import com.neno.lastfmapp.modules.utils.UserAccountManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")

val appModule = module {
    single<AccountManager> { UserAccountManager(androidApplication()) }

    single<Calendar> { GregorianCalendar() }
    single<DateFormat> { SimpleDateFormat("MMM d, HH:mm") }
    single<TimeCalculator> { UnixTimeCalculator(get(), get()) }
}