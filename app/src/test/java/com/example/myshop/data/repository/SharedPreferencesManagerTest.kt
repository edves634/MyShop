// SharedPreferencesManagerUnitTest.kt
package com.example.myshop.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class SharedPreferencesManagerUnitTest {

    private lateinit var mockPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var mockContext: Context
    private lateinit var testPrefs: SharedPreferencesManager

    @Before
    fun setup() {
        mockPreferences = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)
        mockContext = mock(Context::class.java)

        // Настраиваем мок Context
        `when`(mockContext.applicationContext).thenReturn(mockContext)
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPreferences)

        // Настраиваем мок Preferences и Editor
        `when`(mockPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.clear()).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then {  }
        `when`(mockEditor.commit()).thenReturn(true)

        // Создаем тестируемый объект
        testPrefs = SharedPreferencesManager(mockContext)
    }

    @Test
    fun `incrementLaunchCount saves incremented value`() {
        `when`(mockPreferences.getInt(KEY_LAUNCH_COUNT, 0)).thenReturn(5)
        testPrefs.incrementLaunchCount()
        verify(mockEditor).putInt(KEY_LAUNCH_COUNT, 6)
        verify(mockEditor).apply()
    }

    @Test
    fun `shouldShowIntro handles zero launches correctly`() {
        `when`(mockPreferences.getInt(KEY_LAUNCH_COUNT, 0)).thenReturn(0)
        assertThat(testPrefs.shouldShowIntro()).isFalse()
    }

    @Test
    fun `getLaunchCount returns default value`() {
        `when`(mockPreferences.getInt(KEY_LAUNCH_COUNT, 0)).thenReturn(0)
        assertThat(testPrefs.getLaunchCount()).isEqualTo(0)
    }

    @Test
    fun `shouldShowIntro returns true on third launch`() {
        `when`(mockPreferences.getInt(KEY_LAUNCH_COUNT, 0)).thenReturn(3)
        assertThat(testPrefs.shouldShowIntro()).isTrue()
    }

    @Test
    fun `shouldShowIntro returns false on second launch`() {
        `when`(mockPreferences.getInt(KEY_LAUNCH_COUNT, 0)).thenReturn(2)
        assertThat(testPrefs.shouldShowIntro()).isFalse()
    }

    companion object {
        private const val KEY_LAUNCH_COUNT = "launch_count"
    }
}