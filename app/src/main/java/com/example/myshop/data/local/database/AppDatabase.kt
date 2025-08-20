package com.example.myshop.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myshop.data.local.model.Product
import com.example.myshop.data.local.dao.ProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "shop.db")
                .addCallback(ProductDbCallback(context))
                .build()
    }

    private class ProductDbCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context).productDao().insertAll(initialProducts)
            }
        }

        private val initialProducts = listOf(
            Product(name = "Рубашка Classic", price = 1999.0, category = "Одежда"),
            Product(name = "Джинсы Slim Fit", price = 3499.0, category = "Одежда"),
            Product(name = "Футболка Basic", price = 999.0, category = "Одежда"),
            Product(name = "Платье Evening", price = 5999.0, category = "Одежда"),
            Product(name = "Пальто Winter", price = 8999.0, category = "Одежда"),

                // Обувь
            Product(name = "Кроссовки Runner", price = 4999.0, category = "Обувь"),
            Product(name = "Ботинки Leather", price = 7999.0, category = "Обувь"),
            Product(name = "Туфли Classic", price = 4599.0, category = "Обувь"),
            Product(name = "Сапоги Winter", price = 6999.0, category = "Обувь"),
            Product(name = "Сандалии Summer", price = 2999.0, category = "Обувь"),

                // Головные уборы
            Product(name = "Кепка Sport", price = 899.0, category = "Головные уборы"),
            Product(name = "Шапка Wool", price = 1299.0, category = "Головные уборы"),
            Product(name = "Шляпа Panama", price = 1599.0, category = "Головные уборы"),
            Product(name = "Берет Fashion", price = 1199.0, category = "Головные уборы"),
            Product(name = "Бейсболка Classic", price = 999.0, category = "Головные уборы"),

                // Аксессуары
            Product(name = "Ремень Classic", price = 1499.0, category = "Аксессуары"),
            Product(name = "Сумка City", price = 5999.0, category = "Аксессуары"),
            Product(name = "Галстук Silk", price = 1299.0, category = "Аксессуары"),
            Product(name = "Шарф Wool", price = 1799.0, category = "Аксессуары"),
            Product(name = "Перчатки Leather", price = 2499.0, category = "Аксессуары")
            )
        }

    }

