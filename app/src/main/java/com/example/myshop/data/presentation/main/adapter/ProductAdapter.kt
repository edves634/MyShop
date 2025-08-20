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

/**
 * Адаптер для отображения списка продуктов в виде таблицы с заголовками.
 * Поддерживает сортировку по различным колонкам и визуальное отображение текущей сортировки.
 *
 * @param onSortRequested Колбэк функция, вызываемая при запросе сортировки по определенной колонке
 */
class ProductAdapter(
    private val onSortRequested: (String) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0  // Тип элемента: заголовок таблицы
        private const val TYPE_PRODUCT = 1 // Тип элемента: продукт
    }

    // Параметры текущей сортировки
    private var sortColumn: String = "name"      // Текущая колонка для сортировки
    private var sortAscending: Boolean = false   // Направление сортировки (true - по возрастанию)

    /**
     * Обновляет параметры сортировки и уведомляет об изменении заголовка.
     *
     * @param column Колонка для сортировки ("name", "price", "category")
     * @param ascending Направление сортировки (true - по возрастанию, false - по убыванию)
     */
    fun updateSortParams(column: String, ascending: Boolean) {
        sortColumn = column
        sortAscending = ascending
        notifyItemChanged(0) // Обновляем только заголовок (первый элемент)
    }

    /**
     * Создает соответствующий ViewHolder в зависимости от типа элемента.
     *
     * @param parent Родительская ViewGroup
     * @param viewType Тип элемента (TYPE_HEADER или TYPE_PRODUCT)
     * @return Созданный ViewHolder
     */
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

    /**
     * Привязывает данные к ViewHolder в зависимости от типа элемента.
     *
     * @param holder ViewHolder для привязки данных
     * @param position Позиция элемента в списке
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is ProductItemViewHolder -> holder.bind(getItem(position) as Product)
        }
    }

    /**
     * Возвращает тип элемента для заданной позиции.
     *
     * @param position Позиция элемента в списке
     * @return TYPE_HEADER для позиции 0, TYPE_PRODUCT для остальных позиций
     */
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_PRODUCT
    }

    /**
     * ViewHolder для отображения элемента продукта.
     *
     * @property binding Привязка к макету элемента продукта
     */
    inner class ProductItemViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Привязывает данные продукта к элементам интерфейса.
         *
         * @param product Продукт для отображения
         */
        fun bind(product: Product) {
            binding.apply {
                tvName.text = product.name
                tvPrice.text = "%,.2f ₽".format(product.price) // Форматирование цены с разделителями тысяч
                tvCategory.text = product.category
            }
        }
    }

    /**
     * ViewHolder для отображения заголовка таблицы.
     *
     * @property binding Привязка к макету заголовка таблицы
     * @property sortCallback Колбэк для обработки запросов сортировки
     */
    inner class HeaderViewHolder(
        private val binding: ItemTableHeaderBinding,
        private val sortCallback: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Настраивает внешний вид заголовков таблицы и обработчики кликов.
         */
        fun bind() {
            setupColumn(binding.headerName, "name")
            setupColumn(binding.headerPrice, "price")
            setupColumn(binding.headerCategory, "category")

            // Устанавливаем обработчики кликов для сортировки
            binding.headerName.setOnClickListener { sortCallback("name") }
            binding.headerPrice.setOnClickListener { sortCallback("price") }
            binding.headerCategory.setOnClickListener { sortCallback("category") }
        }

        /**
         * Настраивает внешний вид отдельной колонки заголовка.
         *
         * @param textView TextView заголовка колонки
         * @param columnName Имя колонки для сравнения с текущей сортировкой
         */
        private fun setupColumn(textView: TextView, columnName: String) {
            val isActive = sortColumn == columnName
            // Определяем иконку в зависимости от активности и направления сортировки
            val iconRes = when {
                !isActive -> R.drawable.ic_arrow_down
                sortAscending -> R.drawable.ic_arrow_up
                else -> R.drawable.ic_arrow_down
            }

            // Устанавливаем цвет и жирность шрифта в зависимости от активности
            val color = if (isActive) Color.BLACK else Color.GRAY
            val typeface = if (isActive) Typeface.BOLD else Typeface.NORMAL

            // Создаем и настраиваем drawable для иконки
            val drawable = ContextCompat.getDrawable(textView.context, iconRes)?.apply {
                setTint(color)
            }

            // Устанавливаем иконку, цвет текста и жирность шрифта
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            textView.setTextColor(color)
            textView.setTypeface(null, typeface)
        }
    }

    /**
     * Callback для вычисления разницы между двумя списками в DiffUtil.
     */
    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        /**
         * Проверяет, представляют ли два объекта один и тот же элемент.
         *
         * @param oldItem Старый элемент
         * @param newItem Новый элемент
         * @return true если элементы имеют одинаковый идентификатор
         */
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Product && newItem is Product -> oldItem.id == newItem.id
                oldItem is String && newItem is String -> oldItem == newItem
                else -> false
            }
        }

        /**
         * Проверяет, одинаково ли содержание двух элементов.
         *
         * @param oldItem Старый элемент
         * @param newItem Новый элемент
         * @return true если содержимое элементов идентично
         */
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Product && newItem is Product -> oldItem == newItem
                else -> true
            }
        }
    }
}