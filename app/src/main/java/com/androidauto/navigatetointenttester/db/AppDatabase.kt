package com.androidauto.navigatetointenttester.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androidauto.navigatetointenttester.db.NavigateTo
import com.androidauto.navigatetointenttester.db.NavigateToDao

@Database(entities = arrayOf(NavigateTo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun navigateToDao(): NavigateToDao
}