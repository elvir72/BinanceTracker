package com.binancetracker.di

import android.content.Context
import androidx.room.Room
import com.binancetracker.data.api.BinanceApiService
import com.binancetracker.data.api.BinanceWebSocketManager
import com.binancetracker.data.api.OkHttp3Client
import com.binancetracker.data.db.AppDatabase
import com.binancetracker.data.db.SymbolDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fapi.binance.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideBinanceApiService(retrofit: Retrofit): BinanceApiService {
        return retrofit.create(BinanceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttp3Client(): OkHttp3Client = OkHttp3Client()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "binance_tracker.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSymbolDao(db: AppDatabase): SymbolDao = db.symbolDao()
}
