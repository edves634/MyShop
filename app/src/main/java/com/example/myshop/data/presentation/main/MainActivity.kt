package com.example.myshop.data.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.myshop.data.presentation.main.controller.ProductListController
import com.example.myshop.data.presentation.main.viewmodel.ProductViewModel
import com.example.myshop.data.presentation.intro.IntroManager
import com.example.myshop.databinding.ActivityMainBinding

/**
 * Главная активность приложения, отображающая список товаров.
 *
 * Координирует работу различных компонентов:
 * - ViewModel для управления данными
 * - ProductListController для отображения списка товаров
 * - IntroManager для управления показом вступительных экранов
 *
 * Реализует паттерн MVVM с использованием компонентов Android Jetpack.
 */
class MainActivity : AppCompatActivity() {
    // Привязка представления с использованием ViewBinding
    private lateinit var binding: ActivityMainBinding

    // ViewModel для управления данными товаров (инициализируется через делегат)
    private val viewModel: ProductViewModel by viewModels()

    // Менеджер для управления показом вступительных экранов
    private lateinit var introManager: IntroManager

    /**
     * Создание активности, инициализация компонентов.
     *
     * @param savedInstanceState сохраненное состояние активности (если есть)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация менеджера вступительных экранов и проверка необходимости показа
        introManager = IntroManager(this)
        introManager.checkAndShowIntro(this)

        // Настройка контроллера списка товаров
        setupProductController()
    }

    /**
     * Настройка контроллера для управления отображением списка товаров.
     *
     * Создает экземпляр ProductListController и передает ему необходимые компоненты:
     * - RecyclerView для отображения списка
     * - SearchView для поиска товаров
     * - ViewModel для доступа к данным
     * - LifecycleOwner для наблюдения за изменениями данных
     */
    private fun setupProductController() {
        val controller = ProductListController(
            recyclerView = binding.recyclerView2,
            searchView = binding.searchView2 as SearchView,
            viewModel = viewModel,
            lifecycleOwner = this
        )
        controller.setup()
    }

    /**
     * Сохранение состояния активности.
     *
     * Устанавливает флаг, предотвращающий повторную проверку показа
     * вступительного экрана при восстановлении активности.
     *
     * @param outState Bundle для сохранения состояния
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("SHOULD_CHECK_INTRO", false)
    }
}