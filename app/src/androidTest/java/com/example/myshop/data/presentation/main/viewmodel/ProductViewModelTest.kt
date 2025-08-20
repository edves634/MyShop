// ProductViewModelTest.kt
package com.example.myshop.data.presentation.main.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myshop.data.local.database.AppDatabase
import com.example.myshop.data.local.model.Product
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Инструментальные тесты для [ProductViewModel].
 *
 * Проверяют корректность работы ViewModel с базой данных, включая:
 * - Загрузку продуктов
 * - Поиск продуктов
 * - Сортировку продуктов
 *
 * Для изоляции тестов используется in-memory база данных.
 * Для работы с LiveData применяется [InstantTaskExecutorRule] и вспомогательная функция ожидания.
 */
@RunWith(AndroidJUnit4::class)
class ProductViewModelTest {

    /**
     * Правило для синхронного выполнения задач LiveData
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var viewModel: ProductViewModel

    /**
     * Настройка тестового окружения перед каждым тестом
     * - Создает in-memory базу данных
     * - Инициализирует ViewModel
     * - Заменяет базу данных во ViewModel на тестовую
     */
    @Before
    fun setup() {
        // Создаем in-memory базу данных для тестов
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        // Создаем ViewModel с тестовым контекстом
        val context = ApplicationProvider.getApplicationContext() as Application
        viewModel = ProductViewModel(context)

        // Заменяем базу данных в ViewModel на тестовую (через рефлексию)
        replaceDatabaseInViewModel()
    }

    /**
     * Вспомогательная функция для замены базы данных во ViewModel через рефлексию
     * Позволяет использовать тестовую in-memory базу вместо реальной
     */
    private fun replaceDatabaseInViewModel() {
        try {
            val field = viewModel.javaClass.getDeclaredField("database")
            field.isAccessible = true
            field.set(viewModel, database)
        } catch (e: Exception) {
            fail("Не удалось установить тестовую базу данных в ViewModel: ${e.message}")
        }
    }

    /**
     * Очистка ресурсов после каждого теста
     */
    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Вспомогательная функция для ожидания значений LiveData
     * @param liveData LiveData для наблюдения
     * @param timeout максимальное время ожидания в секундах
     * @return полученное значение из LiveData
     * @throws RuntimeException если превышено время ожидания
     */
    private fun <T> waitForLiveData(liveData: androidx.lifecycle.LiveData<T>, timeout: Long = 2): T {
        val latch = CountDownLatch(1)
        var data: T? = null

        val observer = object : androidx.lifecycle.Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                liveData.removeObserver(this)
            }
        }

        liveData.observeForever(observer)
        latch.await(timeout, TimeUnit.SECONDS)

        return data ?: throw RuntimeException("Timeout waiting for LiveData")
    }

    /**
     * Тест загрузки продуктов из пустой базы данных
     * Проверяет, что возвращается пустой список
     */
    @Test
    fun loadProducts_emptyDatabase_returnsEmptyList() {
        // Загрузка продуктов из пустой базы
        viewModel.loadProducts()

        // Ожидаем и проверяем, что список пустой
        val products = waitForLiveData(viewModel.products)
        assertTrue(products.isEmpty())
    }

    /**
     * Тест загрузки продуктов из базы с данными
     * Проверяет корректность загрузки существующих продуктов
     */
    @Test
    fun loadProducts_withProducts_returnsProducts() = runBlocking {
        // Добавляем тестовые продукты
        val testProducts = listOf(
            Product(name = "Product 1", price = 10.0, category = "Category A"),
            Product(name = "Product 2", price = 20.0, category = "Category B")
        )

        // Используем runBlocking для вызова suspend функций
        testProducts.forEach {
            database.productDao().insert(it)
        }

        // Загружаем продукты
        viewModel.loadProducts()

        // Проверяем, что продукты загружены
        val products = waitForLiveData(viewModel.products)
        assertEquals(2, products.size)
    }

    /**
     * Тест поиска продуктов по запросу
     * Проверяет корректность фильтрации продуктов
     */
    @Test
    fun search_withQuery_filtersProducts() = runBlocking {
        // Добавляем тестовые продукты
        val testProducts = listOf(
            Product(name = "Apple", price = 10.0, category = "Fruits"),
            Product(name = "Banana", price = 20.0, category = "Fruits"),
            Product(name = "Carrot", price = 5.0, category = "Vegetables")
        )

        testProducts.forEach { database.productDao().insert(it) }

        // Ищем "Apple"
        viewModel.search("Apple")

        // Проверяем результат
        val products = waitForLiveData(viewModel.products)
        assertEquals(1, products.size)
        assertEquals("Apple", products[0].name)
    }

    /**
     * Тест сортировки продуктов по цене
     * Проверяет правильность порядка сортировки
     */
    @Test
    fun sort_byPrice_sortsProductsCorrectly() = runBlocking {
        // Добавляем тестовые продукты
        val testProducts = listOf(
            Product(name = "Banana", price = 20.0, category = "Fruits"),
            Product(name = "Apple", price = 10.0, category = "Fruits"),
            Product(name = "Carrot", price = 5.0, category = "Vegetables")
        )

        testProducts.forEach { database.productDao().insert(it) }

        // Сортируем по цене
        viewModel.sort("price")

        // Проверяем результат
        val products = waitForLiveData(viewModel.products)
        assertEquals(5.0, products[0].price, 0.01)
        assertEquals(10.0, products[1].price, 0.01)
        assertEquals(20.0, products[2].price, 0.01)
    }
}