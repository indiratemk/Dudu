package com.example.dudu

import android.app.Application
import com.example.dudu.data.TasksRepositoryImpl
import com.example.dudu.data.local.DuduDatabase

class DuduApplication : Application() {

    private val database by lazy { DuduDatabase.getDatabase(this) }
    val repository by lazy { TasksRepositoryImpl(database.taskDao()) }
}