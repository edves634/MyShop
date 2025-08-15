package com.example.myshop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.databinding.FragmentProductListBinding

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        // Настройка адаптера с явным указанием типа параметра
        adapter = ProductAdapter { column: String ->
            Log.d("ProductListFragment", "Sort requested for column: $column")
            viewModel.sort(column)
        }

        // Настройка RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductListFragment.adapter
        }

        // Настройка поиска
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText.orEmpty())
                return true
            }
        })

        // Подписка на данные продуктов
        viewModel.products.observe(viewLifecycleOwner) { products ->
            Log.d("ProductListFragment", "Received ${products.size} products")
            val items = mutableListOf<Any>().apply {
                add("HEADER")
                addAll(products)
            }
            adapter.submitList(items)
        }

        // ИСПРАВЛЕНИЕ: Используем правильное имя метода и обработку параметров
        viewModel.sortState.observe(viewLifecycleOwner) { sortParams ->
            sortParams?.let { (column, ascending) ->
                Log.d("ProductListFragment", "Sort state changed: $column, $ascending")
                // Исправленный вызов метода
                adapter.updateSortParams(column, ascending)
            }
        }

        // Загрузка начальных данных
        viewModel.loadProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}