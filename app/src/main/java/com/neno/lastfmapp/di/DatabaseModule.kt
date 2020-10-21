package com.neno.lastfmapp.di

import android.content.Context
import androidx.room.Room
import com.neno.lastfmapp.database.DatabaseDataSource
import com.neno.lastfmapp.database.LastFmDatabase
import com.neno.lastfmapp.database.LastFmDatabaseOperations
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory { provideLastFmDatabase(androidContext()) }

    single<LastFmDatabaseOperations> { provideDatabaseDataSource(get()) }
}

fun provideLastFmDatabase(context: Context): LastFmDatabase
{
    return Room.databaseBuilder(
        context,
        LastFmDatabase::class.java,
        "last-fm-database"
    ).build()
}

fun provideDatabaseDataSource(lastFmDatabase: LastFmDatabase): DatabaseDataSource
{
    return DatabaseDataSource(lastFmDatabase)
}