package com.example.myshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myshop.data.local.model.Product

/**
 * Data Access Object (DAO) для операций с таблицей продуктов в локальной базе данных.
 * Предоставляет методы для вставки и получения данных о продуктах.
 */
@Dao
interface ProductDao {

    /**
     * Получает все продукты из локальной базы данных.
     *
     * @return Список всех продуктов, хранящихся в базе данных.
     *         Если записи отсутствуют, возвращается пустой список.
     * @throws android.database.SQLException При возникновении ошибок во время выполнения запроса
     */
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<Product>

    /**
     * Вставляет один продукт в базу данных.
     * При возникновении конфликта (например, продукт с таким же ID уже существует)
     * существующая запись будет заменена новой.
     *
     * @param product Продукт для вставки или обновления
     * @return Идентификатор строки вставленного элемента (не гарантируется для Room)
     * @throws android.database.SQLException При возникновении ошибок во время операции вставки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    /**
     * Массовая вставка списка продуктов в базу данных.
     * При возникновении конфликтов существующие записи будут заменены новыми.
     *
     * @param products Список продуктов для вставки или обновления
     * @return Массив идентификаторов вставленных строк (не гарантируется для Room)
     * @throws android.database.SQLException При возникновении ошибок во время операции вставки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)
}