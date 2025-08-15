package com.example.myshop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products WHERE category = :category AND name LIKE '%' || :query || '%'")
    fun searchProducts(category: String, query: String): Flow<List<Product>>

    // Добавьте этот метод для подсчета продуктов
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchAllProducts(query: String): Flow<List<Product>>

}
