package com.androidauto.navigatetointenttester.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NavigateToDao {
    @Query("SELECT * FROM NavigateTo ORDER BY last_modified DESC")
    fun getAll(): Flow<List<NavigateTo>>

    @Insert
    suspend fun insert(vararg navigateTo: NavigateTo)

    @Update
    suspend fun update(vararg navigateTo: NavigateTo)

    @Query("DELETE FROM NavigateTo WHERE id = (SELECT id FROM NavigateTo ORDER BY last_modified DESC LIMIT 1)")
    suspend fun deleteLatest()
}