package com.example.myshop.data.presentation.main.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Модульные тесты для класса [ProductSortManager].
 *
 * Проверяет корректность работы менеджера сортировки товаров, включая:
 * - Инициализацию начального состояния сортировки
 * - Переключение направления сортировки для той же колонки
 * - Смену колонки сортировки с сохранением состояния
 * - Сохранение состояния направления при переключении между колонками
 *
 * @constructor Создает тестовый набор для проверки функциональности менеджера сортировки
 */
class ProductSortManagerTest {

    // Экземпляр тестируемого класса ProductSortManager
    private lateinit var sortManager: ProductSortManager

    /**
     * Настройка тестового окружения перед каждым тестом.
     *
     * Инициализирует новый экземпляр ProductSortManager для обеспечения изолированности тестов.
     */
    @Before
    fun setup() {
        sortManager = ProductSortManager()
    }

    /**
     * Тест проверяет начальное состояние сортировки.
     *
     * Ожидаемый результат: начальная сортировка по колонке "name" в порядке убывания (descending)
     */
    @Test
    fun `initial sort state is name ascending`() {
        val (column, ascending) = sortManager.getSortParams()
        assertEquals("name", column)
        assertEquals(false, ascending)
    }

    /**
     * Тест проверяет переключение направления сортировки для той же колонки.
     *
     * Ожидаемый результат: при повторном вызове сортировки по той же колонке
     * направление сортировки инвертируется (ascending/descending)
     */
    @Test
    fun `sort toggles direction for same column`() {
        sortManager.sort("name")
        var (_, ascending) = sortManager.getSortParams()
        assertEquals(true, ascending)

        sortManager.sort("name")
        ascending = sortManager.getSortParams().second
        assertEquals(false, ascending)
    }

    /**
     * Тест проверяет сброс направления сортировки при выборе новой колонки.
     *
     * Ожидаемый результат: при выборе новой колонки сортировки устанавливается
     * направление по умолчанию (descending)
     */
    @Test
    fun `sort resets direction for new column`() {
        sortManager.sort("price")
        var (column, ascending) = sortManager.getSortParams()
        assertEquals("price", column)
        assertEquals(false, ascending)

        sortManager.sort("category")
        column = sortManager.getSortParams().first
        ascending = sortManager.getSortParams().second
        assertEquals("category", column)
        assertEquals(false, ascending)
    }

    /**
     * Тест проверяет сохранение состояния направления при переключении между колонками.
     *
     * Ожидаемый результат: при возврате к предыдущей колонке сортировки
     * восстанавливается последнее использованное направление сортировки
     */
    @Test
    fun `sort direction persists when changing columns`() {
        sortManager.sort("name") // toggle to ascending
        sortManager.sort("price") // new column
        sortManager.sort("name") // return to name

        val (column, ascending) = sortManager.getSortParams()
        assertEquals("name", column)
        assertEquals(true, ascending)
    }
}