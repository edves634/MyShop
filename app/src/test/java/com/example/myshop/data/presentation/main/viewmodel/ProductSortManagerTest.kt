package com.example.myshop.data.presentation.main.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductSortManagerTest {

    private lateinit var sortManager: ProductSortManager

    @Before
    fun setup() {
        sortManager = ProductSortManager()
    }

    @Test
    fun `initial sort state is name ascending`() {
        val (column, ascending) = sortManager.getSortParams()
        assertEquals("name", column)
        assertEquals(false, ascending)
    }

    @Test
    fun `sort toggles direction for same column`() {
        sortManager.sort("name")
        var (_, ascending) = sortManager.getSortParams()
        assertEquals(true, ascending)

        sortManager.sort("name")
        ascending = sortManager.getSortParams().second
        assertEquals(false, ascending)
    }

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