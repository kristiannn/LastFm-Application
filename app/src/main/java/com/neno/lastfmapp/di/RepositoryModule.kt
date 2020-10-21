package com.neno.lastfmapp.di

import com.neno.lastfmapp.database.LastFmDatabaseOperations
import com.neno.lastfmapp.network.LastFmServiceOperations
import com.neno.lastfmapp.repository.LastFmDataSource
import com.neno.lastfmapp.repository.LastFmRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<LastFmRepository> { provideLastFmRepository(get(), get()) }
}

fun provideLastFmRepository(
    lastFmServiceOperations: LastFmServiceOperations,
    lastFmDatabaseOperations: LastFmDatabaseOperations
): LastFmDataSource
{
    return LastFmDataSource(lastFmServiceOperations, lastFmDatabaseOperations)
}