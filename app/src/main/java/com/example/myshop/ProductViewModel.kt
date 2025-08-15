package com.example.myshop

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    // Единое состояние сортировки
    val sortState: MutableLiveData<Pair<String, Boolean>> = MutableLiveData(Pair("name", false))

    private var currentQuery = ""

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            val allProducts = database.productDao().getAll()
            applySortAndFilter(allProducts)
        }
    }

    fun search(query: String) {
        currentQuery = query
        loadProducts()
    }

    fun sort(column: String) {
        val currentState = sortState.value ?: Pair("name", false)
        val (currentColumn, currentAscending) = currentState

        val newAscending = if (currentColumn == column) {
            // Если кликнули на текущий столбец - меняем направление
            !currentAscending
        } else {
            // Если кликнули на другой столбец - устанавливаем направление по убыванию
            false
        }

        // Обновляем состояние сортировки
        sortState.value = Pair(column, newAscending)
        loadProducts()
    }

    private fun applySortAndFilter(products: List<Product>) {
        val (column, ascending) = sortState.value ?: Pair("name", false)

        // Фильтрация
        val filtered = if (currentQuery.isBlank()) {
            products
        } else {
            products.filter {
                it.name.contains(currentQuery, ignoreCase = true)
            }
        }

        // Сортировка
        val sorted = when (column) {
            "name" -> if (ascending) {
                filtered.sortedBy { it.name }
            } else {
                filtered.sortedByDescending { it.name }
            }
            "price" -> if (ascending) {
                filtered.sortedBy { it.price }
            } else {
                filtered.sortedByDescending { it.price }
            }
            "category" -> if (ascending) {
                filtered.sortedBy { it.category }
            } else {
                filtered.sortedByDescending { it.category }
            }
            else -> filtered
        }

        // Логирование для отладки
        Log.d("SORTING", "Sorted by $column ${if (ascending) "ASC" else "DESC"}")
        sorted.take(5).forEach {
            Log.d("SORTING", "${it.name} - ${it.price}")
        }

        _products.value = sorted
    }
}