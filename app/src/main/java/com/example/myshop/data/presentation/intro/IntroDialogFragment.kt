package com.example.myshop.data.presentation.intro

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myshop.databinding.DialogIntroBinding

/**
 * Диалоговый фрагмент для отображения вступительного/ознакомительного экрана.
 *
 * Этот диалог предназначен для показа пользователю при первом запуске приложения
 * или для демонстрации важной информации. Содержит кнопку для продолжения работы.
 *
 * Особенности:
 * - Полноэкранный диалог с прозрачным фоном
 * - Затемнение фона на 50%
 * - Автоматическое управление жизненным циклом через DialogFragment
 * - Использование View Binding для работы с layout
 */
class IntroDialogFragment : DialogFragment() {

    /**
     * View Binding объект для доступа к элементам интерфейса.
     * Используется nullable тип для безопасной обработки жизненного цикла фрагмента.
     */
    private var _binding: DialogIntroBinding? = null

    /**
     * Non-null геттер для View Binding объекта.
     *
     * @throws IllegalStateException если обращение происходит вне жизненного цикла onViewCreated
     */
    private val binding get() = _binding!!

    /**
     * Создает и возвращает view иерархию, связанную с фрагментом.
     *
     * @param inflater LayoutInflater для инфлейта view
     * @param container Родительский контейнер для view
     * @param savedInstanceState Сохраненное состояние фрагмента
     * @return Инфлейтнутый view фрагмента
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Вызывается сразу после создания view иерархии фрагмента.
     * Настраивает обработчики событий для элементов интерфейса.
     *
     * @param view Созданный view объекта
     * @param savedInstanceState Сохраненное состояние фрагмента
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Обработчик нажатия на кнопку "Продолжить"
        binding.btnContinue.setOnClickListener {
            dismiss() // Закрывает диалог
        }
    }

    /**
     * Создает диалоговое окно с кастомными настройками.
     *
     * @param savedInstanceState Сохраненное состояние фрагмента
     * @return Настроенный экземпляр Dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Устанавливает прозрачный фон для диалога
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        return dialog
    }

    /**
     * Вызывается когда фрагмент становится видимым пользователю.
     * Настраивает размеры и внешний вид диалогового окна.
     */
    override fun onStart() {
        super.onStart()
        // Настройка параметров окна диалога
        dialog?.window?.apply {
            // Устанавливает ширину на весь экран, высоту по содержимому
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Устанавливает затемнение фона на 50%
            setDimAmount(0.5f)
        }
    }

    /**
     * Вызывается при уничтожении view иерархии фрагмента.
     * Очищает ссылку на binding объект для предотвращения утечек памяти.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Объект-компаньон для хранения констант и фабричных методов.
     */
    companion object {
        /**
         * Тег для идентификации фрагмента в FragmentManager.
         */
        const val TAG = "IntroDialogFragment"
    }
}