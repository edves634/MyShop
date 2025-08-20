package com.example.myshop.data.presentation.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.local.database.AppDatabase
import com.example.myshop.data.local.model.Product
import kotlinx.coroutines.launch

/**
 * ViewModel для управления списком товаров.
 *
 * Отвечает за загрузку, сортировку и фильтрацию товаров из базы данных.
 * Предоставляет LiveData для наблюдения за изменениями списка товаров и состояния сортировки.
 *
 * @property application контекст приложения для доступа к базе данных
 */
class ProductViewModel(application: Application) : AndroidViewModel(application) {
    // Инициализация зависимостей
    private val database = AppDatabase.Companion.getDatabase(application)
    private val sortManager = ProductSortManager()
    private val productFilter = ProductFilter()

    // LiveData для наблюдения за списком товаров
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    // LiveData для наблюдения за состоянием сортировки
    val sortState = MutableLiveData(sortManager.getSortParams())

    // Текущий поисковый запрос
    private var currentQuery = ""

    /**
     * Загружает все товары из базы данных и применяет текущие настройки сортировки и фильтрации.
     *
     * Выполняется асинхронно в viewModelScope для избежания блокировки основного потока.
     */
    fun loadProducts() {
        viewModelScope.launch {
            val allProducts = database.productDao().getAll()
            applySortAndFilter(allProducts)
        }
    }

    /**
     * Выполняет поиск товаров по заданному запросу.
     *
     * @param query поисковый запрос для фильтрации товаров
     */
    fun search(query: String) {
        currentQuery = query
        loadProducts()
    }

    /**
     * Сортирует товары по указанной колонке.
     *
     * Обновляет состояние сортировки и перезагружает товары с применением новой сортировки.
     *
     * @param column название колонки для сортировки ("name", "price" или "category")
     */
    fun sort(column: String) {
        sortManager.sort(column)
        sortState.value = sortManager.getSortParams()
        loadProducts()
    }

    /**
     * Применяет текущие настройки сортировки и фильтрации к списку товаров.
     *
     * @param products исходный список товаров для обработки
     */
    private fun applySortAndFilter(products: List<Product>) {
        // Фильтрация товаров по поисковому запросу
        val filtered = productFilter.filter(products, currentQuery)

        // Получение текущих параметров сортировки
        val (column, ascending) = sortManager.getSortParams()

        // Применение сортировки в зависимости от выбранной колонки и направления
        val sorted = when (column) {
            "name" -> if (ascending) filtered.sortedBy { it.name } else filtered.sortedByDescending { it.name }
            "price" -> if (ascending) filtered.sortedBy { it.price } else filtered.sortedByDescending { it.price }
            "category" -> if (ascending) filtered.sortedBy { it.category } else filtered.sortedByDescending { it.category }
            else -> filtered
        }

        // Обновление LiveData с отсортированным и отфильтрованным списком
        _products.value = sorted
    }
}