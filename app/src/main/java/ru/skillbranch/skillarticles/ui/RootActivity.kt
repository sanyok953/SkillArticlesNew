package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        setupToolbar()

        btn_like.setOnClickListener {
            Snackbar.make(coordinator_container, "btn_like", Snackbar.LENGTH_LONG).setAnchorView(bottombar).show() // setAnchorView Якорная view
        }
        btn_bookmark.setOnClickListener {
            Snackbar.make(coordinator_container, "btn_bookmark", Snackbar.LENGTH_LONG).show()
        }
        btn_share.setOnClickListener {
            Snackbar.make(coordinator_container, "btn_share", Snackbar.LENGTH_LONG).show()
        }
        btn_settings.setOnClickListener {
            submenu.toggle() // 54 45
        }

        switch_mode.setOnClickListener {
            delegate.localNightMode = if (switch_mode.isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Кнопка назад на тулбаре
        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null // Иконка по индексом 2
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = this.dpToIntPx(resources.getDimension(R.dimen.avatar_round_size).toInt())
            it.height = this.dpToIntPx(resources.getDimension(R.dimen.avatar_round_size).toInt())
            it.marginEnd = this.dpToIntPx(resources.getDimension(R.dimen.spacing_normal_16).toInt())
            logo.layoutParams = it
        }
    }
}