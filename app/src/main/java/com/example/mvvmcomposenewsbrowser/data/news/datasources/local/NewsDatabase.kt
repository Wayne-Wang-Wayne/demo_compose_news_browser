package com.example.mvvmcomposenewsbrowser.data.news.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalLikedNews::class], version = 1, exportSchema = false)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun likedArticleDao(): LikedNewsDao

}