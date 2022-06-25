package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import java.io.Serializable

abstract class BaseViewModel<T>(initState: T, private val savedStateHandle: SavedStateHandle) :
    ViewModel() where T: VMState {
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val notifications = MutableLiveData<Event<Notify>>()

    /***
     * Инициализация начального состояния аргументом конструктоа, и объявления состояния как
     * MediatorLiveData - медиатор исспользуется для того чтобы учитывать изменяемые данные модели
     * и обновлять состояние ViewModel исходя из полученных данных
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        val restoredState = savedStateHandle.get<Any>("state")?.let {
            if (it is Bundle) initState.fromBundle(it) as? T
            else it as T
        }


        value = restoredState ?: initState
    }

    /***
     * getter для получения not null значения текущего состояния ViewModel
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val currentState
        get() = state.value!!


    /***
     * лямбда выражение принимает в качестве аргумента текущее состояние и возвращает
     * модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState!!
    }

    /***
     * функция для создания уведомления пользователя о событии (событие обрабатывается только один раз)
     * соответсвенно при изменении конфигурации и пересоздании Activity уведомление не будет вызвано
     * повторно
     */
    @UiThread
    protected fun notify(content: Notify) { // В метод передаём необходимый на sealed класс который нужно отобразить в наше вью
        notifications.value = Event(content)
    }

    /***
     * более компактная форма записи observe() метода LiveData принимает последним аргумент лямбда
     * выражение обрабатывающее изменение текущего стостояния
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    // Наблюдать часть стейта и обновлять определённые данные которые которые нас онтересуют в определённом контексте (одной view)
    fun <D> observeSubState(
        owner: LifecycleOwner,
        transform: (T) -> D,
        onChanged: (substate: D) -> Unit
    ) {
        state
            .map(transform) // Трансформируем весь state в необходимую модель substate
            .distinctUntilChanged() // Фильтрация и пропуск дальше только если значение изменилось
            .observe(owner, Observer { onChanged(it!!) })
    }

    /***
     * более компактная форма записи observe() метода LiveData вызывает лямбда выражение обработчик
     * только в том случае если уведомление не было уже обработанно ранее,
     * реализует данное поведение с помощью EventObserver
     */
    // Похож на observeState он будет предназначен для нотификаций в методе observe использует не observer а наш EventObserver
    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
    }

    /***
     * функция принимает источник данных и лямбда выражение обрабатывающее поступающие данные источника
     * лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
     * изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
     */
    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>, // Данные за которыми потом будем следить
        onChanged: (newValue: S, currentState: T) -> T? // Лямбда
    ) {
        state.addSource(source) {
            // MediatorLiveData Добавляет источник этим методом за которым будет следить и при изменении любого добавленного уведомит подписчика...
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    // Сохранение стайта в bundle после смерти процесса
    fun saveState() {
        savedStateHandle.set("state", currentState)
    }

    // Восстановление стайта из bundle после смерти процесса
    /*fun restoreState() {
        val restoredState = savedStateHandle.get<T>("state") // Не проверяет возвращаемый тип
        restoredState ?: return
        state.value = restoredState!!
    }*/

}

class ViewModelFactory(owner: SavedStateRegistryOwner, private val params: String) :
    AbstractSavedStateViewModelFactory(owner, bundleOf()) { // ViewModelProvider.Factory
    /*override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }*/

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//
class Event<out E>(private val content: E) {
    var hasBeenHandled = false // Был обработан контент

    /***
     * возвращает контент который еще не был обработан иначе null
     */
    fun getContentIfNotHandled(): E? { // Вернуть из евента контент который не был обработан или налл
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): E = content
}

/***
 * в качестве аргумента конструктора принимает лямбда выражение обработчик в аргумент которой передается
 * необработанное ранее событие получаемое в реализации метода Observer`a onChanged
 */
// Работает как обыкновенный обсёрвер только в качестве данных которые он обрабатывает использует не просто объекты а наши евент объекты

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) :
    Observer<Event<E>> { // Обёртка над Observer

    override fun onChanged(event: Event<E>?) {
        //если есть необработанное событие (контент) передай в качестве аргумента в лямбду
        // onEventUnhandledContent возвращает либо тот контент который внутри себя либо налл
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

//
sealed class Notify(val message: String) { // sealed похожи на енамы только с отличием что sealed могут сохранить внутри себя какое то состояние (могут хранить экземпляры)
    // удобно перебирать в when конструкции потому что можем там провериять через is каким подтипом sealed класса является тот или иной класс
    data class TextMessage(val msg: String) :
        Notify(msg) // Дата класс может наследоваться если он является сабклассом сеалед класса (показываем как текст в снекбаре)

    data class ActionMessage( // Будет в себе содержать текст сообщени, actionLabel и лямбда выражение обработчик (функция которая будет вызвана когда нажмём на кнопку которая в снекбаре)
        val msg: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify(msg) // Дата класс наследуется от Notify

    data class ErrorMessage(
        val msg: String,
        val errLabel: String?,
        val errHandler: (() -> Unit)?
    ) : Notify(msg)
}

interface VMState : Serializable {
    fun toBundle(): Bundle
    fun fromBundle(bundle: Bundle): VMState
}