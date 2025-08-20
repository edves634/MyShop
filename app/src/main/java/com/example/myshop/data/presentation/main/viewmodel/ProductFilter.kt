package com.example.myshop.data.presentation.main.viewmodel

import com.example.myshop.data.local.model.Product

/**
 * Утилитный класс для фильтрации списка продуктов по поисковому запросу.
 * Обеспечивает функциональность поиска по имени продукта без учета регистра.
 *
 * Используется ViewModel для фильтрации данных перед отображением в UI.
 */
class ProductFilter {
    /**
     * Фильтрует список продуктов по заданному поисковому запросу.
     * Если запрос пустой или содержит только пробелы, возвращает исходный список.
     * Поиск выполняется по полю name без учета регистра.
     *
     * @param products Исходный список продуктов для фильтрации
     * @param query Поисковый запрос для фильтрации продуктов
     * @return Отфильтрованный список продуктов, содержащих запрос в названии,
     *         или исходный список если запрос пустой
     *
     * Пример использования:
     * ```kotlin
     * val filteredProducts = ProductFilter().filter(products, "рубашка")
     * ```
     */
    fun filter(products: List<Product>, query: String) =
        if (query.isBlank()) products
        else products.filter { it.name.contains(query, ignoreCase = true) }
}