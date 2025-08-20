package com.example.myshop.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Менеджер для работы с SharedPreferences приложения.
 *
 * Обеспечивает сохранение, получение и управление настройками и данными приложения
 * в постоянном хранилище SharedPreferences.
 *
 * @property context контекст приложения для доступа к SharedPreferences
 */
class SharedPreferencesManager(context: Context) {

    companion object {
        // Имя файла SharedPreferences
        private const val PREFS_NAME = "MyShopPrefs"

        // Ключ для хранения счетчика запусков приложения
        private const val KEY_LAUNCH_COUNT = "launch_count"
    }

    // Экземпляр SharedPreferences для работы с постоянным хранилищем
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Увеличивает счетчик запусков приложения на 1.
     *
     * Сохраняет обновленное значение в SharedPreferences.
     */
    fun incrementLaunchCount() {
        val currentCount = sharedPreferences.getInt(KEY_LAUNCH_COUNT, 0)
        sharedPreferences.edit {
            putInt(KEY_LAUNCH_COUNT, currentCount + 1)
        }
    }

    /**
     * Определяет, нужно ли показывать вступительный экран (intro).
     *
     * @return true если вступительный экран должен быть показан (каждый третий запуск),
     *         false в противном случае
     */
    fun shouldShowIntro(): Boolean {
        val count = getLaunchCount()
        // Показывать каждый третий запуск (3, 6, 9, ...)
        return count > 0 && count % 3 == 0
    }

    /**
     * Очищает все данные из SharedPreferences.
     *
     * Удаляет все сохраненные значения, включая счетчик запусков.
     */
    fun clear() {
        sharedPreferences.edit {
            clear()
        }
    }

    /**
     * Получает текущее значение счетчика запусков приложения.
     *
     * @return количество запусков приложения (по умолчанию 0)
     */
    fun getLaunchCount(): Int {
        return sharedPreferences.getInt(KEY_LAUNCH_COUNT, 0)
    }
}