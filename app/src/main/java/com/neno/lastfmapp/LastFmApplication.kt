package com.neno.lastfmapp

import android.app.Application
import com.neno.lastfmapp.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LastFmApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@LastFmApplication)
            modules(
                appModule,
                databaseModule,
                networkModule,
                repositoryModule,
                viewModelsModule
            )
        }
    }
}