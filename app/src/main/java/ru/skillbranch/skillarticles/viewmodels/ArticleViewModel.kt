package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.asMap
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf

class ArticleViewModel(private val articleId: String, savedStateHandle: SavedStateHandle) :
    BaseViewModel<ArticleState>(ArticleState(), savedStateHandle),
    IArticleViewModel {

    private val repository = ArticleRepository

    init {
        savedStateHandle.setSavedStateProvider("state") {
            currentState.toBundle()
        }
        // реализовать связывание получаемых данных с текущим стейтом в методах subscribeOnDataSource
        /*
          функция принимает источник данных и лямбда выражение обрабатывающее поступающие данные источника
          лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
          изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
         */
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format(),
                author = article.author,
                poster = article.poster
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
    }

    override fun getArticleContent(): LiveData<List<String>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    override fun handleLike() {
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }
        toggleLike()

        val msg = if (currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don`t like it anymore",
                "No, still like it",
                toggleLike
            )
        }
        notify(msg)
    }

    override fun handleBookmark() {
        //updateState { it.copy(isBookmark = !it.isBookmark) }
        val toggleBookmark = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        }
        toggleBookmark()

        val msg =
            if (currentState.isBookmark) Notify.TextMessage("Add to bookmarks")
            else Notify.TextMessage("Remove from bookmarks")

        notify(msg)
    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) } // state это дата класс на нём можно использовать copy в котором указываем какое поле меняем
    }

    override fun handleSearchMode(isSearch: Boolean) {
        //searchMode.value = isSearch
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0) }
    }

    override fun handleSearch(query: String?) {
        //queryString.value = query
        query ?: return

        val result = currentState.content.firstOrNull().indexesOf(query)
            .map { it to it + query.length }

        updateState { it.copy(searchQuery = query, searchResults = result) }
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    override fun handleNightMode() {
        // Берём текущий state преобразуем его в toAppSettings
        val settings = currentState.toAppSettings() // Содержит 2 поля внутри себя
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode)) // Вызывает у репозитория обновление и передаём туда необходимые состояния
        // и MediatorLiveData узнает что данные были изменены и изменится общее состояние и после изменится ui
    }

}

data class ArticleState(
    val isAuth: Boolean = false, // Пользователь авторизован
    val isLoadingContent: Boolean = true, // Контент загружается
    val isLoadingReviews: Boolean = true, // Отзывы загружаются
    val isLike: Boolean = false, // Отмечено как like
    val isBookmark: Boolean = false, // В закладках
    val isShowMenu: Boolean = false, // Отображается меню
    val isBigText: Boolean = false, // Шрифт увеличен
    val isDarkMode: Boolean = false, // Темный режим
    val isSearch: Boolean = false, // Режим поиска
    val searchQuery: String? = null, // Поисковый запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(), // Результаты поиска, стартовая и конечная позиции
    val searchPosition: Int = 0, // Текущая позиция найденного результата
    val shareLink: String? = null, // Ссылка share
    val title: String? = null, // Заголовок статьи
    val category: String? = null, // Категория
    val categoryIcon: Any? = null, // Иконка категории
    val date: String? = null, // Дата публикации
    val author: Any? = null, // Автор статьи
    val poster: String? = null, // Обложка статьи
    val content: List<String> = emptyList(), // Контент
    val reviews: List<Any> = emptyList() // Комментарий
) : VMState {
    override fun toBundle(): Bundle {
        val map = copy(content = emptyList(), isLoadingContent = true)
            .asMap()
            .toList()
            .toTypedArray()

        return bundleOf(*map)
    }

    override fun fromBundle(bundle: Bundle): VMState {
        val map = bundle.keySet().associateWith { bundle[it] }
        return copy(
            isAuth = map["isAuth"] as Boolean,
            isLoadingContent = map["isLoadingContent"] as Boolean,
            isLoadingReviews = map["isLoadingReviews"] as Boolean,
            isLike = map["isLike"] as Boolean,
            isBookmark = map["isBookmark"] as Boolean,
            isShowMenu = map["isShowMenu"] as Boolean,
            isBigText = map["isBigText"] as Boolean,
            isDarkMode = map["isDarkMode"] as Boolean,
            isSearch = map["isSearch"] as Boolean,
            searchQuery = map["searchQuery"] as String,
            searchResults = map["searchResults"] as List<Pair<Int, Int>>,
            searchPosition = map["searchPosition"] as Int,
            shareLink = map["shareLink"] as String,
            title = map["title"] as String,
            category = map["category"] as String,
            categoryIcon = map["categoryIcon"] as Any,
            date = map["date"] as String,
            author = map["author"] as Any,
            poster = map["poster"] as String,
            content = map["content"] as List<String>,
            reviews = map["reviews"] as List<Any>
        )
    }
}

data class BottombarData(
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isSearch: Boolean = false,
    val resultsCount: Int = 0,
    val searchPosition: Int = 0
)

data class SubmenuData(
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false
)

fun ArticleState.toBottombarData() =
    BottombarData(isLike, isBookmark, isShowMenu, isSearch, searchResults.size, searchPosition)

fun ArticleState.toSubmenuData() = SubmenuData(isShowMenu, isBigText, isDarkMode)

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()

    if (defaultValue != null)
        data.value = defaultValue!!

    return data
}