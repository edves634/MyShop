package com.example.myshop

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val database: AppDatabase) {
    fun searchAllProducts(query: String): Flow<List<Product>> {
        return database.productDao().searchAllProducts(query)
    }
}