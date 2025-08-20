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

/**
 * Абстрактный класс базы данных приложения, использующий Room Persistence Library.
 * Определяет структуру базы данных, версию и операции доступа к данным.
 *
 * @property entities Массив классов сущностей, которые будут представлены в базе данных
 * @property version Текущая версия базы данных (при изменении схемы требуется увеличивать версию)
 * @property exportSchema Флаг экспорта схемы базы данных для ведения истории изменений
 */
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Предоставляет экземпляр Data Access Object (DAO) для работы с продуктами.
     *
     * @return Реализация интерфейса ProductDao, сгенерированная Room
     */
    abstract fun productDao(): ProductDao

    /**
     * Объект-компаньон для реализации паттерна Singleton при работе с базой данных.
     * Гарантирует наличие только одного экземпляра базы данных в течение жизненного цикла приложения.
     */
    companion object {
        /**
         * Волатильное поле для хранения единственного экземпляра базы данных.
         * Аннотация @Volatile гарантирует атомарность операций чтения/записи в многопоточной среде.
         */
        @Volatile private var instance: AppDatabase? = null

        /**
         * Возвращает экземпляр базы данных, создавая его при первом вызове.
         * Реализует потокобезопасный паттерн Singleton с двойной проверкой блокировки.
         *
         * @param context Контекст приложения для инициализации базы данных
         * @return Единственный экземпляр AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        /**
         * Создает и настраивает экземпляр базы данных с предустановленными данными.
         *
         * @param context Контекст приложения для инициализации базы данных
         * @return Созданный экземпляр AppDatabase
         */
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "shop.db")
                .addCallback(ProductDbCallback(context)) // Добавляем callback для предзаполнения данных
                .build()
    }

    /**
     * Внутренний класс обратного вызова для выполнения операций при создании базы данных.
     * Используется для предварительного заполнения базы данных начальными данными.
     *
     * @property context Контекст приложения, необходимый для доступа к базе данных
     */
    private class ProductDbCallback(private val context: Context) : Callback() {
        /**
         * Вызывается при создании базы данных. Заполняет базу данных начальными продуктами.
         *
         * @param db Объект базы данных SQLite
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Запускаем корутину в фоновом потоке для вставки начальных данных
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context).productDao().insertAll(initialProducts)
            }
        }

        /**
         * Список начальных продуктов для предзаполнения базы данных при первом запуске.
         * Содержит продукты различных категорий: одежда, обувь, головные уборы и аксессуары.
         */
        private val initialProducts = listOf(
            // Одежда
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