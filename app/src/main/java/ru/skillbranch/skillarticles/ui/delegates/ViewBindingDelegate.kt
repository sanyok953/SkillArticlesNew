package ru.skillbranch.skillarticles.ui.delegates

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import java.security.PrivateKey
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBindingDelegate<T : ViewBinding>(
    private val activity: AppCompatActivity,
    private val initializer: (LayoutInflater) -> T
) : ReadOnlyProperty<AppCompatActivity, T>, LifecycleObserver {
    private var _value: T? = null

    init {
        // Create binding on lifecycle
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        // Когда активити будет создано когда пройдет колбек onCreate будет вызван ттекущий метод
        // Проверяем если _value ещё не проинициализировано вызовем лямбду инициализатор и получаем
        // значения нашего view binding, после вызвать активити setContentView value!!.root
        // Мы знаем что вию уже создано, после удалим наблюдатель
        if (_value == null)
            _value = initializer(activity.layoutInflater)
        activity.setContentView(_value!!.root)
        activity.lifecycle.removeObserver((this))
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        if (_value == null)
            _value = initializer(thisRef.layoutInflater)

        return _value!!
    }
    // Нам необходим определённый класс делегат который будет возвращать view binding
    // должен уметь обработать наш уже сгенерированный view binding класс, из него надуть view
    // и подставить её в необходимом жизненном цикле нашей активити. Когда активити будет создана
    // необходимо чтобы туда была вставлена уже созданное представление (content view)
    // Сейчас мы сделаем с помощью ReadOnlyProperty
    // Понадобится активити (необходим жизненный цикл, должны знать когда активити будет создано)
    // необходимо заимплементить getValue будет возвращать необходимый binding
    // и тот и тот механизм реализуем внутри нашего кастомного делегата позволит сделать код более переиспользуемым
    // более легко писать основную бизнес логику
    // (Во фрагментах...нужно следить за ЖЦ) 34 11
}

// Чтобы было проще вызывать делегат
inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding(noinline initializer: (LayoutInflater) -> T) = ViewBindingDelegate(this, initializer)