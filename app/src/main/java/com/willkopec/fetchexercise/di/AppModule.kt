package com.willkopec.fetchexercise.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.willkopec.fetchexercise.data.api.ApiService
import com.willkopec.fetchexercise.data.datastore.DataStore
import com.willkopec.fetchexercise.data.repository.DataRepository
import com.willkopec.fetchexercise.ui.viewmodels.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    //Network components
    single { provideGson() }
    single { provideOkHttpClient() }
    single { provideRetrofit(get(), get()) }
    single { provideApiService(get()) }

    //DataStore manager
    single { DataStore(get()) }

    //Repository
    single { DataRepository(get(), get()) }

    //ViewModel
    viewModel { MainViewModel(get()) }
}

private fun provideGson(): Gson {
    return GsonBuilder().setLenient().create()
}

private fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://hiring.fetch.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

private fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}