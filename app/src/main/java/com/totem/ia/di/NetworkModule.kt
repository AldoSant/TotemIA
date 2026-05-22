package com.totem.ia.di

import com.totem.ia.BuildConfig
import com.totem.ia.data.TotemApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://veredasinc.com.br/totemia/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val keyInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val path = original.url.encodedPath
            val newRequestBuilder = original.newBuilder()

            // Attach the X-Totem-Api-Key header if it's not a health-check endpoint
            if (!path.endsWith("/health") && !path.endsWith("health") && BuildConfig.TOTEM_API_KEY.isNotEmpty()) {
                newRequestBuilder.addHeader("X-Totem-Api-Key", BuildConfig.TOTEM_API_KEY)
            }

            chain.proceed(newRequestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(keyInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTotemApiService(retrofit: Retrofit): TotemApiService {
        return retrofit.create(TotemApiService::class.java)
    }
}
