package ru.skillbranch.skillarticles.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {
    //private lateinit var viewModel: ArticleViewModel
    private val viewModel: ArticleViewModel by viewModels { ViewModelFactory("0") }
    private val vb: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)
    private val vbBottombar
        get() = vb.bottombar.binding
    private val vbSubmenu
        get() = vb.submenu.binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //vb = ActivityRootBinding.inflate(layoutInflater)
        //setContentView(vb.root) 39 37

        setupToolbar()
        setupBottombar()
        setupSubmenu()

        //val vmFactory = ViewModelFactory("0")
        //viewModel = ViewModelProvider(this, vmFactory).get(ArticleViewModel::class.java)


        // Подписывается на данные нашей Вьюмодели
        viewModel.observeState(this) { // Функция предоставляет более короткую запись
            renderUI(it)
        }

        // Подписываемся на наши нотификации
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        if (viewModel.currentState.isSearch) {
            searchItem.expandActionView()
            searchView.setQuery(viewModel.currentState.searchQuery, false)
            searchView.requestFocus()
        } else searchView.clearFocus()

        with(searchView) {
            //viewModel.currentState.searchQuery?.let { setQuery(it, false) }

            queryHint = "Введите слово для поиска"

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.handleSearch(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.handleSearch(newText)
                    return true
                }
            })
        }

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                Log.d("M_RootActivity", "onMenuItemActionExpand")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                Log.d("M_RootActivity", "onMenuItemActionCollapse")
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(vb.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(vb.bottombar)

        when (notify) { // Перебираем наши sealed классы
            is Notify.TextMessage -> {

            }
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                // В snackbar передаём наш actionLabel
                snackbar.setAction(notify.actionLabel) {
                    // actionHandler не может быть nullable потому что когда передаём ActionMessage мы точно знаем название нашего actionLabel и какую то функцию обработчик
                    notify.actionHandler.invoke()
                    // Будет работать следующим образом что метод у снекбара setAction он принимает название кнопки которая на снекбаре
                    // и ту функцию (переданную из viewModel) которая будет вызвана когда мы на эту кнопку нажмём
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(com.google.android.material.R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun renderUI(data: ArticleState) {
        vbBottombar.btnSettings.isChecked = data.isShowMenu
        if (data.isShowMenu) vb.submenu.open() else vb.submenu.close()

        vbBottombar.btnLike.isChecked = data.isLike
        vbBottombar.btnBookmark.isChecked = data.isBookmark
        vbSubmenu.switchMode.isChecked = data.isDarkMode
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (data.isBigText) {
            vb.tvTextContent.textSize = 18f
            vbSubmenu.btnTextUp.isChecked = true
            vbSubmenu.btnTextDown.isChecked = false
        } else {
            vb.tvTextContent.textSize = 14f
            vbSubmenu.btnTextUp.isChecked = false
            vbSubmenu.btnTextDown.isChecked = true
        }

        vb.tvTextContent.text =
            if (data.isLoadingContent) "Loading..." else data.content.first() as String
        vb.toolbar.title = data.title ?: "Loading..."
        vb.toolbar.subtitle = data.category ?: "Loading..."
        data.categoryIcon?.let {
            vb.toolbar.logo = getDrawable(it as Int)

        }

    }

    private fun setupToolbar() {
        setSupportActionBar(vb.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Кнопка назад на тулбаре
        val logo =
            if (vb.toolbar.childCount > 2) vb.toolbar.getChildAt(2) as ImageView else null // Иконка по индексом 2
        //logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        logo?.let {
            it.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val lp = logo?.layoutParams
        lp?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            //it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }

    private fun setupBottombar() {
        vbBottombar.btnLike.setOnClickListener { viewModel.handleLike() }
        vbBottombar.btnBookmark.setOnClickListener { viewModel.handleBookmark() }
        vbBottombar.btnShare.setOnClickListener { viewModel.handleShare() }
        vbBottombar.btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
    }

    private fun setupSubmenu() {
        vbSubmenu.btnTextUp.setOnClickListener { viewModel.handleUpText() }
        vbSubmenu.btnTextDown.setOnClickListener { viewModel.handleDownText() }
        vbSubmenu.switchMode.setOnClickListener { viewModel.handleNightMode() }
    }
}