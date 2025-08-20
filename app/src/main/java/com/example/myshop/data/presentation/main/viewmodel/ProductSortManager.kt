package com.example.myshop.data.presentation.main.viewmodel

class ProductSortManager {
    private var currentSortColumn: String = "name"
    private var currentSortAscending: Boolean = false
    // Добавляем словарь для сохранения состояний сортировки
    private val columnStates = mutableMapOf<String, Boolean>()

    init {
        // Инициализируем начальное состояние для колонки "name"
        columnStates["name"] = false
    }

    fun sort(column: String) {
        if (currentSortColumn == column) {
            // Инвертируем текущее состояние для колонки
            currentSortAscending = !currentSortAscending
        } else {
            // Сохраняем текущее состояние перед сменой колонки
            columnStates[currentSortColumn] = currentSortAscending
            currentSortColumn = column
            // Восстанавливаем сохранённое состояние для новой колонки (если есть)
            currentSortAscending = columnStates[column] ?: false
        }
        // Обновляем состояние для текущей колонки
        columnStates[column] = currentSortAscending
    }

    fun getSortParams() = currentSortColumn to currentSortAscending
}