package com.example.myshop.presentation.main.viewmodel

import com.example.myshop.data.local.model.Product
import com.example.myshop.data.presentation.main.viewmodel.ProductFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFilterTest {

    private val filter = ProductFilter()
    private val testProducts = listOf(
        Product(name = "Рубашка Classic", price = 1999.0, category = "Одежда"),
        Product(name = "Кроссовки Runner", price = 4999.0, category = "Обувь"),
        Product(name = "Ремень Classic", price = 1499.0, category = "Аксессуары")
    )

    @Test
    fun `filter returns all products when query is empty`() {
        val result = filter.filter(testProducts, "")
        assertEquals(3, result.size)
    }

    @Test
    fun `filter returns matching products by name`() {
        val result = filter.filter(testProducts, "Classic")
        assertEquals(2, result.size)
        assertEquals("Рубашка Classic", result[0].name)
        assertEquals("Ремень Classic", result[1].name)
    }

    @Test
    fun `filter is case insensitive`() {
        val result = filter.filter(testProducts, "кРоссовКи")
        assertEquals(1, result.size)
        assertEquals("Кроссовки Runner", result[0].name)
    }

    @Test
    fun `filter returns empty list when no matches`() {
        val result = filter.filter(testProducts, "Ноутбук")
        assertEquals(0, result.size)
    }
}