package com.example.mvvmcomposenewsbrowser.di

import android.content.Context
import androidx.room.Room
import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.LikedArticleDao
import com.example.mvvmcomposenewsbrowser.data.news.datasources.local.NewsDatabase
import com.example.mvvmcomposenewsbrowser.data.news.datasources.remote.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val NEWS_BASE_URL = "https://newsapi.org"
const val NEWS_URL_PATH = "/v2/top-headlines"

@Module
@InstallIn(SingletonComponent::class)
object NewsRemoteDataModule {

    @Provides
    fun provideGsonConvertor(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun provideRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(NEWS_BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Singleton
    @Provides
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService = retrofit.create(NewsApiService::class.java)

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): NewsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NewsDatabase::class.java,
            "news_database"
        ).build()
    }

    @Provides
    fun provideLikedArticleDao(database: NewsDatabase): LikedArticleDao = database.likedArticleDao()
}
