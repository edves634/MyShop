package com.example.myshop.data.presentation.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.local.database.AppDatabase
import com.example.myshop.data.local.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.Companion.getDatabase(application)
    private val sortManager = ProductSortManager()
    private val productFilter = ProductFilter()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    val sortState = MutableLiveData(sortManager.getSortParams())

    private var currentQuery = ""

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
        sortManager.sort(column)
        sortState.value = sortManager.getSortParams()
        loadProducts()
    }

    private fun applySortAndFilter(products: List<Product>) {
        val filtered = productFilter.filter(products, currentQuery)
        val (column, ascending) = sortManager.getSortParams()

        val sorted = when (column) {
            "name" -> if (ascending) filtered.sortedBy { it.name } else filtered.sortedByDescending { it.name }
            "price" -> if (ascending) filtered.sortedBy { it.price } else filtered.sortedByDescending { it.price }
            "category" -> if (ascending) filtered.sortedBy { it.category } else filtered.sortedByDescending { it.category }
            else -> filtered
        }

        _products.value = sorted
    }
}

