package com.example.myshop.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Модель данных, представляющая товар в системе.
 * Является Entity-классом для Room Persistence Library, отображается на таблицу "products" в базе данных.
 *
 * @property id Уникальный идентификатор товара. Генерируется автоматически базой данных при вставке новой записи.
 *              Значение по умолчанию 0 указывает на то, что ID будет сгенерирован автоматически.
 * @property name Наименование товара. Не может быть null.
 * @property price Цена товара. Не может быть null.
 * @property category Категория товара. Не может быть null.
 *
 * Пример использования:
 * ```
 * val product = Product(name = "Футболка", price = 999.0, category = "Одежда")
 * ```
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String
)