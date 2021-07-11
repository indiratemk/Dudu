package com.example.dudu.data

import androidx.work.DelegatingWorkerFactory
import com.example.dudu.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class DuduDelegatingWorkerFactory @Inject constructor(
    repository: TasksRepository
) : DelegatingWorkerFactory() {

    init {
        addFactory(ReminderWorkerFactory(repository))
    }
}