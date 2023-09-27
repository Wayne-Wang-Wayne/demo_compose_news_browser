package com.example.democomposenewsbrowser.di

import android.content.Context
import androidx.room.Room
import com.example.democomposenewsbrowser.data.news.DefaultNewsRepository
import com.example.democomposenewsbrowser.data.news.NewsRepository
import com.example.democomposenewsbrowser.data.news.datasources.local.LikedNewsDao
import com.example.democomposenewsbrowser.data.news.datasources.local.NewsDatabase
import com.example.democomposenewsbrowser.data.news.datasources.remote.DefaultUrlBasicInfoService
import com.example.democomposenewsbrowser.data.news.datasources.remote.NewsApiService
import com.example.democomposenewsbrowser.data.news.datasources.remote.UrlBasicInfoService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val NEWS_BASE_URL = "https://newsapi.org"
const val NEWS_URL_PATH = "/v2/top-headlines"

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindNewsRepository(repository: DefaultNewsRepository): NewsRepository

}

@Module
@InstallIn(SingletonComponent::class)
object NewsRemoteDataModule {

    @Provides
    fun provideGsonConvertor(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun provideRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(NEWS_BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService = retrofit.create(NewsApiService::class.java)

    @Provides
    fun bindUrlBasicInfoService(repository: DefaultUrlBasicInfoService): UrlBasicInfoService = repository
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
    fun provideLikedArticleDao(database: NewsDatabase): LikedNewsDao = database.likedArticleDao()
}
