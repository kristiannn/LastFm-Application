package com.neno.lastfmapp.di

import com.neno.lastfmapp.database.LastFmDatabaseOperations
import com.neno.lastfmapp.network.DeezerServiceOperations
import com.neno.lastfmapp.network.LastFmAuthOperations
import com.neno.lastfmapp.network.LastFmServiceOperations
import com.neno.lastfmapp.repository.DeezerDataSource
import com.neno.lastfmapp.repository.DeezerRepository
import com.neno.lastfmapp.repository.LastFmDataSource
import com.neno.lastfmapp.repository.LastFmRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<LastFmRepository> { provideLastFmRepository(get(), get(), get()) }

    single<DeezerRepository> { provideDeezerRepository(get(), get()) }
}

fun provideLastFmRepository(
    lastFmServiceOperations: LastFmServiceOperations,
    lastFmAuthOperations: LastFmAuthOperations,
    lastFmDatabaseOperations: LastFmDatabaseOperations
): LastFmDataSource
{
    return LastFmDataSource(lastFmServiceOperations, lastFmAuthOperations, lastFmDatabaseOperations)
}

fun provideDeezerRepository(
    deezerServiceOperations: DeezerServiceOperations,
    lastFmDatabaseOperations: LastFmDatabaseOperations
): DeezerDataSource
{
    return DeezerDataSource(deezerServiceOperations, lastFmDatabaseOperations)
}