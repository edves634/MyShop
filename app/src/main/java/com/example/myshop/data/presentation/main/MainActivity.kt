package com.example.myshop.data.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.myshop.data.presentation.main.controller.ProductListController
import com.example.myshop.data.presentation.main.viewmodel.ProductViewModel
import com.example.myshop.data.presentation.intro.IntroManager
import com.example.myshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var introManager: IntroManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        introManager = IntroManager(this)
        introManager.checkAndShowIntro(this)

        setupProductController()
    }

    private fun setupProductController() {
        val controller = ProductListController(
            recyclerView = binding.recyclerView2,
            searchView = binding.searchView2 as SearchView,
            viewModel = viewModel,
            lifecycleOwner = this
        )
        controller.setup()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("SHOULD_CHECK_INTRO", false)
    }
}