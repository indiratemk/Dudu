package com.example.dudu.util.workers

import androidx.work.DelegatingWorkerFactory
import com.example.dudu.data.TasksRepository
import com.example.dudu.di.core.AppScope
import javax.inject.Inject

@AppScope
class DuduDelegatingWorkerFactory @Inject constructor(
    repository: TasksRepository
) : DelegatingWorkerFactory() {

    init {
        addFactory(ReminderWorkerFactory(repository))
        addFactory(SynchronizationWorkerFactory(repository))
    }
}