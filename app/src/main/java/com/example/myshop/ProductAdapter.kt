package com.example.myshop

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.databinding.ItemProductBinding
import com.example.myshop.databinding.ItemTableHeaderBinding

class ProductAdapter(
    private val onSortRequested: (String) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PRODUCT = 1
    }

    // Текущее состояние сортировки (столбец и направление)
    private var currentSortColumn: String = "name"
    private var currentSortAscending: Boolean = false

    fun updateSortParams(column: String, ascending: Boolean) {
        currentSortColumn = column
        currentSortAscending = ascending
        notifyItemChanged(0) // Обновляем только заголовок таблицы
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
            is HeaderViewHolder -> holder.bind() // Заголовок таблицы
            is ProductItemViewHolder -> holder.bind(getItem(position) as Product) // Элемент продукта
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_PRODUCT
    }

    // ViewHolder для строки продукта
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

    // ViewHolder для заголовка таблицы
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
            // Определяем ресурс иконки и цвет
            val (iconRes, iconColor) = if (currentSortColumn == columnName) {
                // Активный столбец - черная стрелка
                val resId = if (currentSortAscending) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                Pair(resId, Color.BLACK)
            } else {
                // Неактивный столбец - серая стрелка вниз
                Pair(R.drawable.ic_arrow_down, Color.GRAY)
            }

            // Устанавливаем иконку справа от текста
            val drawable = ContextCompat.getDrawable(textView.context, iconRes)
            drawable?.setTint(iconColor) // Применяем цвет к иконке
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

            // Устанавливаем стиль текста
            textView.setTextColor(Color.BLACK)
            textView.setTypeface(
                null,
                if (currentSortColumn == columnName) Typeface.BOLD else Typeface.NORMAL
            )
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