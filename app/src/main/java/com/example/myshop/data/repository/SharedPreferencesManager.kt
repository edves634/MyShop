package com.example.myshop.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "MyShopPrefs"
        private const val KEY_LAUNCH_COUNT = "launch_count"
    }

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun incrementLaunchCount() {
        val currentCount = sharedPreferences.getInt(KEY_LAUNCH_COUNT, 0)
        sharedPreferences.edit {
            putInt(KEY_LAUNCH_COUNT, currentCount + 1)
        }
    }

    fun shouldShowIntro(): Boolean {
        val count = getLaunchCount()
        // Показывать каждый третий запуск (3, 6, 9, ...)
        return count > 0 && count % 3 == 0
    }

    fun clear() {
        sharedPreferences.edit {
            clear()
        }
    }

    fun getLaunchCount(): Int {
        return sharedPreferences.getInt(KEY_LAUNCH_COUNT, 0)
    }
}