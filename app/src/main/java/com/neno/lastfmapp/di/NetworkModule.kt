package com.neno.lastfmapp.di

import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.network.*
import com.neno.lastfmapp.network.utils.HttpResultConverter
import com.neno.lastfmapp.network.utils.LastFmResultConverter
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory { provideRetrofit() }

    factory { provideLastFmService(get()) }

    factory { provideLastFmAuthService(get()) }

    single<HttpResultConverter> { LastFmResultConverter() }

    single<LastFmServiceOperations> { provideLastFmDataFetcher(get(), get()) }

    single<LastFmAuthOperations> { provideLastFmAuth(get(), get(), get()) }
}

fun provideRetrofit(): Retrofit
{
    return Retrofit.Builder()
        .baseUrl("https://ws.audioscrobbler.com/2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideLastFmService(retrofit: Retrofit): LastFmService
{
    return retrofit.create(LastFmService::class.java)
}

fun provideLastFmAuthService(retrofit: Retrofit): LastFmAuthService
{
    return retrofit.create(LastFmAuthService::class.java)
}

fun provideLastFmDataFetcher(service: LastFmService, lastFmResultConverter: HttpResultConverter): LastFmDataFetcher
{
    return LastFmDataFetcher(service, lastFmResultConverter)
}

fun provideLastFmAuth(
    service: LastFmAuthService,
    lastFmResultConverter: HttpResultConverter,
    accountManager: AccountManager
): LastFmAuth
{
    return LastFmAuth(service, lastFmResultConverter, accountManager)
}