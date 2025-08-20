package com.example.myshop.presentation.main.viewmodel

import com.example.myshop.data.local.model.Product
import com.example.myshop.data.presentation.main.viewmodel.ProductFilter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Модульные тесты для класса [ProductFilter].
 *
 * Проверяет корректность работы фильтрации товаров по различным критериям:
 * - Фильтрация по имени товара (с учетом регистра)
 * - Обработка пустых запросов
 * - Чувствительность к регистру
 * - Обработка случаев, когда совпадения не найдены
 *
 * @constructor Создает тестовый набор для проверки функциональности фильтрации товаров
 */
class ProductFilterTest {

    // Экземпляр тестируемого класса ProductFilter
    private val filter = ProductFilter()

    // Тестовые данные - список товаров для проверки фильтрации
    private val testProducts = listOf(
        Product(name = "Рубашка Classic", price = 1999.0, category = "Одежда"),
        Product(name = "Кроссовки Runner", price = 4999.0, category = "Обувь"),
        Product(name = "Ремень Classic", price = 1499.0, category = "Аксессуары")
    )

    /**
     * Тест проверяет, что при пустом запросе возвращаются все товары.
     *
     * Ожидаемый результат: возвращается исходный список из 3 товаров без фильтрации
     */
    @Test
    fun `filter returns all products when query is empty`() {
        val result = filter.filter(testProducts, "")
        assertEquals(3, result.size)
    }

    /**
     * Тест проверяет фильтрацию товаров по имени.
     *
     * Ожидаемый результат: возвращаются только товары, содержащие в имени "Classic"
     */
    @Test
    fun `filter returns matching products by name`() {
        val result = filter.filter(testProducts, "Classic")
        assertEquals(2, result.size)
        assertEquals("Рубашка Classic", result[0].name)
        assertEquals("Ремень Classic", result[1].name)
    }

    /**
     * Тест проверяет, что фильтрация не зависит от регистра вводимого запроса.
     *
     * Ожидаемый результат: товары находятся независимо от регистра поискового запроса
     */
    @Test
    fun `filter is case insensitive`() {
        val result = filter.filter(testProducts, "кРоссовКи")
        assertEquals(1, result.size)
        assertEquals("Кроссовки Runner", result[0].name)
    }

    /**
     * Тест проверяет обработку ситуации, когда товары не соответствуют запросу.
     *
     * Ожидаемый результат: возвращается пустой список при отсутствии совпадений
     */
    @Test
    fun `filter returns empty list when no matches`() {
        val result = filter.filter(testProducts, "Ноутбук")
        assertEquals(0, result.size)
    }
}