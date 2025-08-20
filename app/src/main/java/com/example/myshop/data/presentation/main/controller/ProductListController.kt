package com.example.myshop.data.presentation.main.controller

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.data.presentation.main.viewmodel.ProductViewModel
import com.example.myshop.data.presentation.main.adapter.ProductAdapter

class ProductListController(
    private val recyclerView: RecyclerView,
    private val searchView: SearchView,
    private val viewModel: ProductViewModel,
    private val lifecycleOwner: LifecycleOwner
) {
    private lateinit var adapter: ProductAdapter

    fun setup() {
        setupAdapter()
        setupSearchView()
        setupObservers()
        viewModel.loadProducts()
    }

    private fun setupAdapter() {
        adapter = ProductAdapter { column -> viewModel.sort(column) }
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.products.observe(lifecycleOwner) { products ->
            adapter.submitList(listOf("HEADER") + products)
        }

        viewModel.sortState.observe(lifecycleOwner) { sortParams ->
            sortParams?.let {
                adapter.updateSortParams(it.first, it.second)
            }
        }
    }
}