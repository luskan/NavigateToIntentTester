package com.androidauto.navigatetointenttester

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.HostException
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.androidauto.navigatetointenttester.db.AppDatabase
import com.androidauto.navigatetointenttester.db.NavigateTo
import com.androidauto.navigatetointenttester.db.NavigateToDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class NavigateToScreenPresenter(private val callback: NavigateToScreenCallback) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val itemsToDisplay: MutableList<NavigateTo> = mutableListOf()
    private lateinit var db: AppDatabase

    private val roomDatabaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(sqliteDb: SupportSQLiteDatabase) {
            super.onCreate(sqliteDb)
            prepopulateDb(db)
        }
    }
    lateinit var navigateToDao: NavigateToDao

    fun init() {
        scope.launch {
            withContext(Dispatchers.IO) {
                db = Room
                    .databaseBuilder(
                        callback.context().applicationContext,
                        AppDatabase::class.java,
                        "database-navigate-to"
                    )
                    .addCallback(roomDatabaseCallback)
                    .build()
                navigateToDao = db.navigateToDao()
                updateList()
            }
        }
    }

    fun prepopulateDb(db: AppDatabase) {
        scope.launch {
            withContext(Dispatchers.IO) {
                db.navigateToDao().insert(
                    NavigateTo(term = "Puławska+174,+Warszawa,+Polska"),
                    NavigateTo(term = "Warszawska+1a,+Gdańsk,+Polska"),
                    NavigateTo(term = "Grocery store")
                )
                updateList()
            }
        }
    }

    fun onSearchSubmitted(searchTerm: String) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val navTo = NavigateTo(term = searchTerm,
                    lastModified = System.currentTimeMillis() // why is it needed?
                )
                navigateToDao.insert(navTo)
                updateList()
                withContext(Dispatchers.Main) {
                    onItemClicked(navTo)
                }
            }
        }
    }

    fun deleteLast() {
        scope.launch {
            withContext(Dispatchers.IO) {
                navigateToDao.deleteLatest()
                updateList()
            }
        }
    }

    private fun updateList() {
        scope.launch {
            withContext(Dispatchers.IO) {
                navigateToDao.getAll().collect {
                    withContext(Dispatchers.Main) {
                        itemsToDisplay.clear()
                        itemsToDisplay.addAll(it)
                        callback.invalidateList()
                    }
                }
            }
        }
    }

    fun onItemClicked(it: NavigateTo) {
        try {
            val uri = Uri.parse("geo:0,0?q=${it.term}")
            val intent = Intent(CarContext.ACTION_NAVIGATE, uri)

            try {
                callback.startCarApp(intent)
            } catch (e: HostException) {
                callback.showToast("Navigate failure: " + e.message)
            }
        }
        catch (e: Exception) {
            callback.showToast("Term failure: " + e.message)
        }
    }

    fun updateItem(navTo: NavigateTo) {
        scope.launch {
            withContext(Dispatchers.IO) {
                navigateToDao.update(navTo.copy(lastModified = System.currentTimeMillis()))
                updateList()
            }
        }
    }

    fun onDestroy() {
        job.cancel()
    }

    fun getItemsToDisplay(): List<NavigateTo> = itemsToDisplay
}