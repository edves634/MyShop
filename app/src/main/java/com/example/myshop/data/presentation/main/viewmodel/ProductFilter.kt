package com.example.myshop.data.presentation.main.viewmodel

import com.example.myshop.data.local.model.Product

class ProductFilter {
    fun filter(products: List<Product>, query: String) =
        if (query.isBlank()) products
        else products.filter { it.name.contains(query, ignoreCase = true) }
}