package com.technowave.decathlon.di

import android.content.Context
import android.content.SharedPreferences
import com.technowave.decathlon.api.Api
import com.technowave.decathlon.api.UrlInterceptor
import com.technowave.decathlon.storage.SharedPrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences("stock", Context.MODE_PRIVATE) as SharedPreferences

    @Singleton
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Api =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://localhost/")
            .client(okHttpClient)
            .build()
            .create(Api::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.MINUTES)
            .readTimeout(15, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            // .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @Singleton
    @Provides
    fun provideUrlInterceptor(sharedPrefManager: SharedPrefManager): Interceptor =
        UrlInterceptor(sharedPrefManager)





}

