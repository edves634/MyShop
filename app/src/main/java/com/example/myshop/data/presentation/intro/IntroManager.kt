package com.example.myshop.data.presentation.intro

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.myshop.data.repository.SharedPreferencesManager

class IntroManager(private val context: Context) {
    var prefs = SharedPreferencesManager(context)

    fun checkAndShowIntro(activity: FragmentActivity) {
        prefs.incrementLaunchCount()
        if (prefs.shouldShowIntro()) {
            showIntroDialog(activity)
        }
    }

    private fun showIntroDialog(activity: FragmentActivity) {
        val dialog = IntroDialogFragment()
        dialog.show(activity.supportFragmentManager, IntroDialogFragment.TAG)
    }
}