package com.example.myshop

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getLaunchCount(): Int = prefs.getInt("launch_count", 0)

    fun incrementLaunchCount() {
        val newCount = getLaunchCount() + 1
        prefs.edit().putInt("launch_count", newCount).apply()
    }

    fun shouldShowIntro(): Boolean {
        val launchCount = getLaunchCount()
        // Показывать интро на 3-й, 6-й, 9-й и т.д. запуски
        return launchCount > 0 && launchCount % 3 == 0
    }
}