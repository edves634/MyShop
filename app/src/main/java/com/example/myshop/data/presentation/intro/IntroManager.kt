package com.example.myshop.data.presentation.intro

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.myshop.data.repository.SharedPreferencesManager

/**
 * Менеджер для управления показом вступительного диалога (интро).
 * Отвечает за логику определения необходимости показа интро и его отображения.
 *
 * @property context Контекст приложения для доступа к SharedPreferences
 * @property prefs Экземпляр менеджера SharedPreferences для хранения данных о запусках
 */
class IntroManager(private val context: Context) {
    var prefs = SharedPreferencesManager(context)

    /**
     * Проверяет условия для показа вступительного диалога и показывает его при необходимости.
     * Увеличивает счетчик запусков приложения и проверяет, нужно ли показывать интро
     * на основе логики, определенной в SharedPreferencesManager.
     *
     * @param activity FragmentActivity для отображения диалогового фрагмента
     */
    fun checkAndShowIntro(activity: FragmentActivity) {
        prefs.incrementLaunchCount()
        if (prefs.shouldShowIntro()) {
            showIntroDialog(activity)
        }
    }

    /**
     * Создает и отображает диалоговое окно с вступительной информацией.
     *
     * @param activity FragmentActivity для отображения диалогового фрагмента
     */
    private fun showIntroDialog(activity: FragmentActivity) {
        val dialog = IntroDialogFragment()
        dialog.show(activity.supportFragmentManager, IntroDialogFragment.TAG)
    }
}