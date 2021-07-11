package com.example.dudu.data.local

import android.app.Application
import android.content.Context
import com.example.dudu.di.scopes.AppScope
import com.example.dudu.util.Constants
import javax.inject.Inject

@AppScope
class SharedPrefs @Inject constructor(
    application: Application
) {

    private val sharedPrefs = application.getSharedPreferences(
        Constants.DUDU_PREFERENCES,
        Context.MODE_PRIVATE
    )

    var isAppFirstOpened
        get() = sharedPrefs.getBoolean(IS_APP_FIRST_OPENED, true)
        set(value) = sharedPrefs.edit().putBoolean(IS_APP_FIRST_OPENED, value).apply()

    companion object {
        const val IS_APP_FIRST_OPENED = "IS_APP_FIRST_OPENED"
    }
}