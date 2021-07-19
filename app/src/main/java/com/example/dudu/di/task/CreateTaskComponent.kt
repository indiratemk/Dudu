package com.example.dudu.di.task

import com.example.dudu.ui.task.CreateTaskActivity
import dagger.Subcomponent

@Subcomponent(modules = [CreateTaskModule::class])
@CreateTaskActivityScope
interface CreateTaskComponent {
    fun inject(activity: CreateTaskActivity)
}