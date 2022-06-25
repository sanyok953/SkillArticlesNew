package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.*

object ArticleRepository { // Singleton
    private val local = LocalDataHolder
    private val network = NetworkDataHolder
    private val prefs: PrefManager = PrefManager()

    fun loadArticleContent(articleId: String): LiveData<List<String>?> {
        return network.loadArticleContent(articleId) //5s delay from network
    }
    fun getArticle(articleId: String): LiveData<ArticleData?> {
        return local.findArticle(articleId) //2s delay from db
    }

    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return local.findArticlePersonalInfo(articleId) //1s delay from db
    }

    fun getAppSettings(): LiveData<AppSettings> = prefs.settings //local.getAppSettings() //from preferences
    fun updateSettings(appSettings: AppSettings) {
        //local.updateAppSettings(appSettings)
        prefs.isBigText = appSettings.isBigText
        prefs.isDarkMode = appSettings.isDarkMode
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        local.updateArticlePersonalInfo(info)
    }
}