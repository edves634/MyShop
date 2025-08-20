package com.example.myshop.data.presentation.main.viewmodel

/**
 * Менеджер для управления параметрами сортировки товаров.
 *
 * Сохраняет состояние сортировки (колонка и направление) для различных полей товаров,
 * позволяя переключаться между колонками с сохранением индивидуальных настроек сортировки.
 *
 * @property currentSortColumn текущая колонка для сортировки (по умолчанию "name")
 * @property currentSortAscending направление сортировки (true - возрастание, false - убывание)
 * @property columnStates карта для хранения состояний сортировки для каждой колонки
 */
class ProductSortManager {
    private var currentSortColumn: String = "name"
    private var currentSortAscending: Boolean = true
    private val columnStates = mutableMapOf<String, Boolean>()

    init {
        // Инициализация начального состояния для колонки "name"
        columnStates["name"] = true
    }

    /**
     * Устанавливает параметры сортировки для указанной колонки.
     *
     * Если выбрана та же колонка, что и текущая - инвертирует направление сортировки.
     * Если выбрана новая колонка - сохраняет состояние предыдущей и восстанавливает
     * состояние для новой колонки (если оно было сохранено ранее).
     *
     * @param column название колонки для сортировки
     */
    fun sort(column: String) {
        if (currentSortColumn == column) {
            // Инвертируем текущее состояние для колонки
            currentSortAscending = !currentSortAscending
        } else {
            // Сохраняем текущее состояние перед сменой колонки
            columnStates[currentSortColumn] = currentSortAscending
            currentSortColumn = column
            // Восстанавливаем сохранённое состояние для новой колонки (если есть)
            currentSortAscending = columnStates[column] != false // По умолчанию возрастание
        }
        // Обновляем состояние для текущей колонки
        columnStates[column] = currentSortAscending
    }

    /**
     * Возвращает текущие параметры сортировки.
     *
     * @return пара значений (колонка, направление), где:
     *         first - название колонки сортировки,
     *         second - направление (true - по возрастанию, false - по убыванию)
     */
    fun getSortParams() = currentSortColumn to currentSortAscending
}