package com.androidauto.navigatetointenttester.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NavigateTo (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "term") val term: String?,
    @ColumnInfo(name = "last_modified", defaultValue = "CURRENT_TIMESTAMP" ) val lastModified: Long = 0
)