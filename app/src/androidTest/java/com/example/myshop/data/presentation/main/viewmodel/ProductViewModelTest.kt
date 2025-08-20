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

@RunWith(AndroidJUnit4::class)
class ProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var viewModel: ProductViewModel

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

    private fun replaceDatabaseInViewModel() {
        try {
            val field = viewModel.javaClass.getDeclaredField("database")
            field.isAccessible = true
            field.set(viewModel, database)
        } catch (e: Exception) {
            fail("Не удалось установить тестовую базу данных в ViewModel: ${e.message}")
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Вспомогательная функция для ожидания LiveData
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

    @Test
    fun loadProducts_emptyDatabase_returnsEmptyList() {
        // Загрузка продуктов из пустой базы
        viewModel.loadProducts()

        // Ожидаем и проверяем, что список пустой
        val products = waitForLiveData(viewModel.products)
        assertTrue(products.isEmpty())
    }

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

    @Test
    fun sort_byName_sortsProductsCorrectly() = runBlocking {
        // Добавляем тестовые продукты в разном порядке
        val testProducts = listOf(
            Product(name = "Banana", price = 20.0, category = "Fruits"),
            Product(name = "Apple", price = 10.0, category = "Fruits"),
            Product(name = "Carrot", price = 5.0, category = "Vegetables")
        )

        testProducts.forEach { database.productDao().insert(it) }

        // Сортируем по имени
        viewModel.sort("name")

        // Проверяем результат
        val products = waitForLiveData(viewModel.products)
        assertEquals("Apple", products[0].name)
        assertEquals("Banana", products[1].name)
        assertEquals("Carrot", products[2].name)
    }

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

    @Test
    fun sort_toggleChangesSortOrder() = runBlocking {
        // Добавляем тестовые продукты
        val testProducts = listOf(
            Product(name = "Banana", price = 20.0, category = "Fruits"),
            Product(name = "Apple", price = 10.0, category = "Fruits")
        )

        testProducts.forEach { database.productDao().insert(it) }

        // Первая сортировка (по умолчанию - по убыванию)
        viewModel.sort("name")
        val firstSort = waitForLiveData(viewModel.products)

        // Проверяем порядок после первой сортировки (по убыванию)
        assertEquals("Banana", firstSort[0].name) // Должен быть первым Banana
        assertEquals("Apple", firstSort[1].name)  // Должен быть вторым Apple

        // Вторая сортировка (меняем направление на возрастание)
        viewModel.sort("name")
        val secondSort = waitForLiveData(viewModel.products)

        // Проверяем порядок после второй сортировки (по возрастанию)
        assertEquals("Apple", secondSort[0].name)  // Должен быть первым Apple
        assertEquals("Banana", secondSort[1].name) // Должен быть вторым Banana
    }

    @Test
    fun searchAndSort_combinedWorkCorrectly() = runBlocking {
        // Добавляем тестовые продукты
        val testProducts = listOf(
            Product(name = "Apple", price = 10.0, category = "Fruits"),
            Product(name = "Banana", price = 20.0, category = "Fruits"),
            Product(name = "Avocado", price = 15.0, category = "Vegetables")
        )

        testProducts.forEach { database.productDao().insert(it) }

        // Ищем продукты на "A"
        viewModel.search("A")

        // Сортируем по цене
        viewModel.sort("price")

        // Проверяем результат
        val products = waitForLiveData(viewModel.products)
        assertEquals(2, products.size) // Только Apple и Avocado
        assertEquals(10.0, products[0].price, 0.01) // Apple first (lowest price)
        assertEquals(15.0, products[1].price, 0.01) // Avocado second
    }
}