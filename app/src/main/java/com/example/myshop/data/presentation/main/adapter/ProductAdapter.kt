package com.example.myshop.data.presentation.main.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.data.local.model.Product
import com.example.myshop.databinding.ItemProductBinding
import com.example.myshop.databinding.ItemTableHeaderBinding

class ProductAdapter(
    private val onSortRequested: (String) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PRODUCT = 1
    }

    private var sortColumn: String = "name"
    private var sortAscending: Boolean = false

    fun updateSortParams(column: String, ascending: Boolean) {
        sortColumn = column
        sortAscending = ascending
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemTableHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onSortRequested
            )
            else -> ProductItemViewHolder(
                ItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is ProductItemViewHolder -> holder.bind(getItem(position) as Product)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_PRODUCT
    }

    inner class ProductItemViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvName.text = product.name
                tvPrice.text = "%,.2f ₽".format(product.price)
                tvCategory.text = product.category
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemTableHeaderBinding,
        private val sortCallback: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            setupColumn(binding.headerName, "name")
            setupColumn(binding.headerPrice, "price")
            setupColumn(binding.headerCategory, "category")

            binding.headerName.setOnClickListener { sortCallback("name") }
            binding.headerPrice.setOnClickListener { sortCallback("price") }
            binding.headerCategory.setOnClickListener { sortCallback("category") }
        }

        private fun setupColumn(textView: TextView, columnName: String) {
            val isActive = sortColumn == columnName
            val iconRes = when {
                !isActive -> R.drawable.ic_arrow_down
                sortAscending -> R.drawable.ic_arrow_up
                else -> R.drawable.ic_arrow_down
            }

            val color = if (isActive) Color.BLACK else Color.GRAY
            val typeface = if (isActive) Typeface.BOLD else Typeface.NORMAL

            val drawable = ContextCompat.getDrawable(textView.context, iconRes)?.apply {
                setTint(color)
            }

            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            textView.setTextColor(color)
            textView.setTypeface(null, typeface)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Product && newItem is Product -> oldItem.id == newItem.id
                oldItem is String && newItem is String -> oldItem == newItem
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Product && newItem is Product -> oldItem == newItem
                else -> true
            }
        }
    }
}