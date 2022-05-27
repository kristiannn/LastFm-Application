package com.neno.lastfmapp.di

import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.network.*
import com.neno.lastfmapp.network.utils.HttpResultConverter
import com.neno.lastfmapp.network.utils.LastFmResultConverter
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory(named(LAST_FM_RETROFIT)) { provideLastFmRetrofit() }

    factory(named(DEEZER_RETROFIT)) { provideDeezerRetrofit() }

    factory { provideLastFmService(get(named(LAST_FM_RETROFIT))) }

    factory { provideLastFmAuthService(get(named(LAST_FM_RETROFIT))) }

    factory { provideDeezerService(get(named(DEEZER_RETROFIT))) }

    single<HttpResultConverter> { LastFmResultConverter() }

    single<LastFmServiceOperations> { provideLastFmDataFetcher(get(), get()) }

    single<LastFmAuthOperations> { provideLastFmAuth(get(), get(), get()) }

    single<DeezerServiceOperations> { provideDeezerDataFetcher(get()) }
}

fun provideLastFmRetrofit(): Retrofit
{
    return Retrofit.Builder()
        .baseUrl("https://ws.audioscrobbler.com/2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideDeezerRetrofit(): Retrofit
{
    return Retrofit.Builder()
        .baseUrl("https://api.deezer.com/")
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

fun provideDeezerService(retrofit: Retrofit): DeezerService
{
    return retrofit.create(DeezerService::class.java)
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

fun provideDeezerDataFetcher(service: DeezerService): DeezerDataFetcher
{
    return DeezerDataFetcher(service)
}

const val LAST_FM_RETROFIT = "LAST_FM_RETROFIT"
const val DEEZER_RETROFIT = "DEEZER_RETROFIT"