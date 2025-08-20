package com.example.myshop.data.presentation.main.controller

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.data.presentation.main.viewmodel.ProductViewModel
import com.example.myshop.data.presentation.main.adapter.ProductAdapter

/**
 * Контроллер для управления списком продуктов.
 * Координирует взаимодействие между View (RecyclerView, SearchView), ViewModel и Adapter.
 * Инкапсулирует логику настройки и обновления UI компонентов.
 *
 * @property recyclerView RecyclerView для отображения списка продуктов
 * @property searchView SearchView для фильтрации продуктов
 * @property viewModel ViewModel содержащая бизнес-логику и данные о продуктах
 * @property lifecycleOwner Владелец жизненного цикла для наблюдения за LiveData
 */
class ProductListController(
    private val recyclerView: RecyclerView,
    private val searchView: SearchView,
    private val viewModel: ProductViewModel,
    private val lifecycleOwner: LifecycleOwner
) {
    private lateinit var adapter: ProductAdapter

    /**
     * Инициализирует все компоненты списка продуктов.
     * Выполняет настройку адаптера, поиска и наблюдателей за данными.
     * Загружает продукты из ViewModel.
     */
    fun setup() {
        setupAdapter()
        setupSearchView()
        setupObservers()
        viewModel.loadProducts()
    }

    /**
     * Настраивает адаптер для RecyclerView.
     * Создает экземпляр ProductAdapter с колбэком для сортировки.
     * Устанавливает LinearLayoutManager и адаптер для RecyclerView.
     */
    private fun setupAdapter() {
        adapter = ProductAdapter { column -> viewModel.sort(column) }
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    /**
     * Настраивает обработчики событий для SearchView.
     * Реагирует на изменения текста поиска и передает запрос в ViewModel.
     */
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            /**
             * Вызывается при изменении текста в SearchView.
             *
             * @param newText новый текст поискового запроса
             * @return true - событие обработано
             */
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }
        })
    }

    /**
     * Настраивает наблюдателей за LiveData из ViewModel.
     * Обновляет UI при изменении данных о продуктах или параметров сортировки.
     */
    private fun setupObservers() {
        // Наблюдатель за списком продуктов
        viewModel.products.observe(lifecycleOwner) { products ->
            // Добавляем заголовок в начало списка
            adapter.submitList(listOf("HEADER") + products)
        }

        // Наблюдатель за состоянием сортировки
        viewModel.sortState.observe(lifecycleOwner) { sortParams ->
            sortParams?.let {
                adapter.updateSortParams(it.first, it.second)
            }
        }
    }
}