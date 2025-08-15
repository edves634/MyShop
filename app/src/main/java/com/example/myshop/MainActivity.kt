package com.example.myshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private var shouldCheckIntro = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Удаляем неработающий observer (sortStates не используется)
        // viewModel.sortStates.observe(this) { ... }

        if (savedInstanceState != null) {
            shouldCheckIntro = savedInstanceState.getBoolean("SHOULD_CHECK_INTRO", true)
        }

        val prefs = SharedPreferencesManager(this)

        if (shouldCheckIntro) {
            shouldCheckIntro = false

            if (prefs.shouldShowIntro()) {
                prefs.incrementLaunchCount()
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
                return
            } else {
                prefs.incrementLaunchCount()
            }
        }

        // Исправление 1: Явно указываем тип параметра в лямбде
        adapter = ProductAdapter { column: String ->
            Log.d("SORT", "Sort requested for column: $column")
            viewModel.sort(column)
        }

        binding.recyclerView2.layoutManager = LinearLayoutManager(this)
        binding.recyclerView2.adapter = adapter

        // Обработка поиска
        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }
        })

        binding.searchView2.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch()
                true
            } else false
        }

        // Подписка на данные продуктов
        viewModel.products.observe(this) { products ->
            Log.d("DATA", "Received ${products.size} products")
            val items = mutableListOf<Any>().apply {
                add("HEADER")
                addAll(products)
            }
            adapter.submitList(items)
        }

        // Исправление 2 и 3: Правильная работа с sortState
        // Предполагается что viewModel.sortState имеет тип LiveData<Pair<String, Boolean>>
        viewModel.sortState.observe(this) { sortParams ->
            sortParams?.let { (column, ascending) ->
                Log.d("SORT", "Sort state changed: $column, $ascending")
                // Исправление 4: Используем правильный метод адаптера
                adapter.updateSortParams(column, ascending)
            }
        }

        // Загрузка данных
        viewModel.loadProducts()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("SHOULD_CHECK_INTRO", shouldCheckIntro)
    }

    private fun performSearch() {
        val query = binding.searchView2.query.toString()
        Log.d("SEARCH", "Search query: $query")
        viewModel.search(query)

        // Скрыть клавиатуру
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView2.windowToken, 0)
    }
}