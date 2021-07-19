package com.example.dudu.di.tasks

import com.example.dudu.ui.tasks.TasksActivity
import dagger.Subcomponent

@Subcomponent(modules = [TasksModule::class])
@TasksActivityScope
interface TasksComponent {
    fun inject(activity: TasksActivity)
}