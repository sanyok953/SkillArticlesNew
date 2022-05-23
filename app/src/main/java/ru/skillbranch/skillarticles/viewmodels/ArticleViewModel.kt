package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String) : BaseViewModel<ArticleState>(ArticleState()),
    IArticleViewModel {

    private val repository = ArticleRepository
    private val queryString = mutableLiveData("")
    private val searchMode = mutableLiveData(false)

    init {
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

        /*subscribeOnDataSource(queryString) { query, state ->
            query ?: return@subscribeOnDataSource null
            state.copy(
                searchQuery = query
            )
        }

        subscribeOnDataSource(searchMode) { search, state ->
            search ?: return@subscribeOnDataSource null
            state.copy(
                isSearch = search
            )
        }*/

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

    override fun getArticleContent(): LiveData<List<Any>?> {
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
        updateState { it.copy(isBookmark = !it.isBookmark) }
    }

    override fun handleShare() {
        val msg = "Share isn`t implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) } // state это дата класс на нём можно использовать copy в котором указываем какое поле меняем
    }

    override fun handleSearchMode(isSearch: Boolean) {
        //searchMode.value = isSearch
        updateState { it.copy(isSearch = isSearch) }
    }

    override fun handleSearch(query: String?) {
        //queryString.value = query
        updateState { it.copy(searchQuery = query) }
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
    val content: List<Any> = emptyList(), // Контент
    val reviews: List<Any> = emptyList() // Комментарий
)

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()

    if (defaultValue != null)
        data.value = defaultValue

    return data
}