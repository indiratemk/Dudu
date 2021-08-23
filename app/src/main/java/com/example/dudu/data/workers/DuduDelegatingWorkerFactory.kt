package com.example.dudu.data.workers

import androidx.work.DelegatingWorkerFactory
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.workers.reminder.ReminderWorkerFactory
import com.example.dudu.data.workers.synchronization.SynchronizationWorkerFactory
import javax.inject.Inject

class DuduDelegatingWorkerFactory @Inject constructor(
    repository: TasksRepository
) : DelegatingWorkerFactory() {

    init {
        addFactory(ReminderWorkerFactory(repository))
        addFactory(SynchronizationWorkerFactory(repository))
    }
}