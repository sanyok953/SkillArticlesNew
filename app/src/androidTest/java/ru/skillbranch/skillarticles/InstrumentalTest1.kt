package ru.skillbranch.skillarticles

import android.text.Spannable
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.text.getSpans
import androidx.core.view.marginBottom
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.*
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.jraska.livedata.TestObserver
import com.jraska.livedata.test
import io.mockk.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.ui.custom.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.SearchSpan
import ru.skillbranch.skillarticles.viewmodels.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class InstrumentedTest1 {
    val content =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nibh sapien, consectetur et ultrices quis, convallis sit amet augue. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum et convallis augue, eu hendrerit diam. Curabitur ut dolor at justo suscipit commodo."

    val longContent =
        """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nibh sapien, consectetur et ultrices quis, convallis sit amet augue. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum et convallis augue, eu hendrerit diam. Curabitur ut dolor at justo suscipit commodo. Curabitur consectetur, massa sed sodales sollicitudin, orci augue maximus lacus, ut elementum risus lorem nec tellus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Praesent accumsan tempor lorem, quis pulvinar justo. Vivamus euismod risus ac arcu pharetra fringilla.
                Maecenas cursus vehicula erat, in eleifend diam blandit vitae. In hac habitasse platea dictumst. Duis egestas augue lectus, et vulputate diam iaculis id. Aenean vestibulum nibh vitae mi luctus tincidunt. Fusce iaculis molestie eros, ac efficitur odio cursus ac. In at orci eget eros dapibus pretium congue sed odio. Maecenas facilisis, dolor eget mollis gravida, nisi justo mattis odio, ac congue arcu risus sed turpis.
                Sed tempor a nibh at maximus."""

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {


            LocalDataHolder.disableDelay(true)
            NetworkDataHolder.disableDelay(true)
        }
    }

    @Test(timeout = 30000)
    fun module1() {


        var actualIndexes = longContent.indexesOf("sed")
        Assert.assertEquals(listOf(322, 930, 1032, 1060), actualIndexes)

        actualIndexes = longContent.indexesOf("sed", false)
        Assert.assertEquals(listOf(322, 930, 1032), actualIndexes)

        actualIndexes = longContent.indexesOf("")
        Assert.assertEquals(listOf<Int>(), actualIndexes)

        actualIndexes = null.indexesOf("")
        Assert.assertEquals(listOf<Int>(), actualIndexes)
    }

    @Test(timeout = 30000)
    fun module2() {
        val scenario = ActivityScenario.launch(RootActivity::class.java)
        scenario.onActivity { activity ->
            val scroll = activity.findViewById<NestedScrollView>(R.id.scroll)
            Assert.assertEquals(0, scroll.marginBottom)
            scroll.setMarginOptionally(bottom = 112)
            Assert.assertEquals(112, scroll.marginBottom)
        }

        scenario.close()
    }


    @Test(timeout = 30000)
    fun module3() {
        val mockVm = mockk<ArticleViewModel>(relaxed = true, relaxUnitFun = true) {
            every { currentState } returns ArticleState()
        }

        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback { activity, stage ->
            if (stage == Stage.PRE_ON_CREATE) {
                (activity as RootActivity).viewModelFactory = createViewModelFor(mockVm)
            }
        }

        val scenario = ActivityScenario.launch(RootActivity::class.java)

        scenario.onActivity {
            Assert.assertEquals("#FC4C4C", it.bgColor.toHex())
            Assert.assertEquals("#FFFFFF", it.fgColor.toHex())
            it.delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        scenario.onActivity {
            Assert.assertEquals("#BB86FC", it.bgColor.toHex())
            Assert.assertEquals("#FFFFFF", it.fgColor.toHex())
        }

        scenario.close()
    }


    @Test(timeout = 30000)
    fun module4() {
        val mockVm = mockk<ArticleViewModel>(relaxed = true, relaxUnitFun = true) {
            every { currentState } returns ArticleState()
        }

        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback { activity, stage ->
            if (stage == Stage.PRE_ON_CREATE) {
                (activity as RootActivity).viewModelFactory = createViewModelFor(mockVm)
            }
            println(stage)
        }

        val scenario = ActivityScenario.launch(RootActivity::class.java)

        scenario.onActivity { activity ->
            activity.renderSubmenu(
                ArticleState(
                    isShowMenu = true,
                    isBigText = true,
                    isDarkMode = true
                ).toSubmenuData()
            )
        }

        onView(withId(R.id.submenu)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.btn_text_up)).check(ViewAssertions.matches(isChecked()))
        onView(withId(R.id.switch_mode)).check(ViewAssertions.matches(isChecked()))

        scenario.onActivity { activity ->
            activity.renderBotombar(
                ArticleState(
                    isBookmark = true,
                    isLike = true
                ).toBottombarData()
            )
        }

        onView(withId(R.id.btn_like)).check(ViewAssertions.matches(isChecked()))
        onView(withId(R.id.btn_bookmark)).check(ViewAssertions.matches(isChecked()))

        scenario.onActivity { activity ->
            activity.renderBotombar(
                BottombarData(
                    isSearch = true,
                    resultsCount = 4,
                    searchPosition = 3
                )
            )
        }

        onView(withId(R.id.bottom_group)).check(ViewAssertions.matches(not(isDisplayed())))
        onView(withId(R.id.reveal)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.tv_search_result)).check(ViewAssertions.matches(withText("4 of 4")))
        onView(withId(R.id.btn_result_up)).check(ViewAssertions.matches(isEnabled()))
        onView(withId(R.id.btn_result_down)).check(ViewAssertions.matches(not(isEnabled())))

        scenario.onActivity { activity ->
            activity.renderBotombar(
                BottombarData(
                    isSearch = true,
                    resultsCount = 0,
                    searchPosition = 0
                )
            )
        }

        onView(withId(R.id.tv_search_result)).check(ViewAssertions.matches(withText("Not found")))
        onView(withId(R.id.btn_result_up)).check(ViewAssertions.matches(not(isEnabled())))
        onView(withId(R.id.btn_result_down)).check(ViewAssertions.matches(not(isEnabled())))

        scenario.onActivity { activity ->
            activity.renderUi(
                ArticleState(
                    content = listOf(content),
                    isBigText = true,
                    isLoadingContent = false,
                    title = "test title",
                    category = "test category"
                )
            )
        }

        onView(withId(R.id.tv_text_content)).check(ViewAssertions.matches(withFontSize(18f)))
        onView(withId(R.id.tv_text_content)).check(ViewAssertions.matches(withText(content)))
        onView(withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle("test title")))
        onView(withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarSubtitle("test category")))


        scenario.onActivity { activity ->
            activity.renderSearchResult(listOf(0 to 10, 20 to 30, 40 to 50))
            activity.renderSearchPosition(1)
        }

        onView(withId(R.id.tv_text_content)).check(ViewAssertions.matches(withText(content)))
        onView(withId(R.id.tv_text_content)).check(
            ViewAssertions.matches(
                withSearchResult(
                    listOf(
                        0 to 10,
                        20 to 30,
                        40 to 50
                    )
                )
            )
        )
        onView(withId(R.id.tv_text_content)).check(ViewAssertions.matches(withSearchPosition(1)))

        scenario.onActivity { activity ->
            activity.clearSearchResult()
        }

        onView(withId(R.id.tv_text_content)).check(ViewAssertions.matches(withSearchResult(emptyList())))

        scenario.close()
    }

    @Test(timeout = 30000)
    @UiThreadTest
    fun module5() {
        val initialState =
            ArticleState(isLoadingContent = false, content = listOf(content), isShowMenu = true, isSearch = false)
        val vm = ArticleViewModel("0", SavedStateHandle())

        vm.state.value = initialState

        val testObs = vm.state.test()

        vm.handleSearchMode(true)

        testObs
            .awaitValue()
            .assertValue(
                "handleSearchMode",
                mapOf("isSearch" to true, "isShowMenu" to false, "searchPosition" to 0),
                ArticleState::asMap
            )

        vm.handleSearch("lorem")

        testObs
            .assertValue(
                "handleSearch",
                mapOf("isSearch" to true, "searchPosition" to 0, "searchQuery" to "lorem", "searchResults" to listOf(0 to 5)),
                ArticleState::asMap
            )

        vm.handleDownResult()
        vm.handleDownResult()

        testObs
            .assertValue(
                "handleUpResult",
                mapOf("searchPosition" to 2),
                ArticleState::asMap
            )

        vm.handleUpResult()

        testObs
            .assertValue(
                "handleDownResult",
                mapOf("searchPosition" to 1),
                ArticleState::asMap
            )

        vm.handleSearchMode(false)

        testObs
            .assertValue(
                "handleSearchMode false (reset state)",
                mapOf("searchPosition" to 0, "isShowMenu" to false, "searchPosition" to 0),
                ArticleState::asMap
            )
    }

    private fun Int.toHex(): String = String.format("#%06X", 0xFFFFFF and this)

    private fun withFontSize(expectedSize: Float): Matcher<View> {
        return object : BoundedMatcher<View, View>(TextView::class.java) {

            var actSize: Float? = null

            public override fun matchesSafely(target: View): Boolean {
                if (target !is TextView) return false
                val pixels = target.textSize
                val actualSize = pixels / target.getResources().displayMetrics.scaledDensity
                actSize = actualSize
                return actualSize == expectedSize
            }

            override fun describeTo(description: Description) {
                if (actSize == null) {
                    description.appendText("with fontSize, expected fontSize : ")
                    description.appendValue(expectedSize)
                } else {
                    description.appendText("with fontSize, actual fontSize : ")
                    description.appendValue(actSize)
                    description.appendText(" expected fontSize : ")
                    description.appendValue(expectedSize)
                }

            }
        }
    }

    private fun withSearchResult(searchResult: List<Pair<Int, Int>>): Matcher<View> {
        return object : BoundedMatcher<View, View>(TextView::class.java) {
            var actPositions: List<Pair<Int, Int>>? = null

            public override fun matchesSafely(target: View): Boolean {
                if (target !is TextView) return false
                if (target.text !is Spannable) return false
                val content = target.text as Spannable
                val searchSpans = content.getSpans<SearchSpan>()
                    .filter { it !is SearchFocusSpan }
                val spansPositions =
                    searchSpans.map { content.getSpanStart(it) to content.getSpanEnd(it) }
                actPositions = spansPositions
                return searchResult == spansPositions
            }

            override fun describeTo(description: Description) {
                if (actPositions == null) {
                    description.appendText("with search result, expected spans positions:  ")
                    description.appendValue(searchResult)
                } else {
                    description.appendText("with search result, actual spans positions:  ")
                    description.appendValue(actPositions)
                    description.appendText(" expected spans positions : ")
                    description.appendValue(searchResult)
                }
            }
        }
    }

    private fun withSearchPosition(position: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(TextView::class.java) {
            var actPosition: Int? = null

            public override fun matchesSafely(target: View): Boolean {
                if (target !is TextView) return false
                if (target.text !is Spannable) return false

                val content = target.text as Spannable
                val searchSpans = content.getSpans<SearchSpan>()
                    .filter { it !is SearchFocusSpan }
                    .map { content.getSpanStart(it) to content.getSpanEnd(it) }

                val focusSpan = content.getSpans<SearchFocusSpan>()
                    .map { content.getSpanStart(it) to content.getSpanEnd(it) }
                    .firstOrNull()

                actPosition = searchSpans.indexOfFirst { it == focusSpan }
                return actPosition == position
            }

            override fun describeTo(description: Description) {
                if (actPosition == null) {
                    description.appendText("with search position, expected search focus span position: ")
                    description.appendValue(position)
                } else {
                    description.appendText("with search position, actual search focus span position : ")
                    description.appendValue(actPosition)
                    description.appendText(" expected search focus span position : ")
                    description.appendValue(position)
                }
            }
        }
    }

}

fun withToolbarTitle(textMatcher: Matcher<CharSequence?>): Matcher<View?>? {
    return object : BoundedMatcher<View?, Toolbar>(Toolbar::class.java) {
        override fun matchesSafely(toolbar: Toolbar): Boolean {
            return textMatcher.matches(toolbar.title)
        }

        override fun describeTo(description: Description) {
            description.appendText("with toolbar title: ")
            textMatcher.describeTo(description)
        }
    }
}


fun withToolbarSubtitle(textMatcher: Matcher<CharSequence?>): Matcher<View?>? {
    return object : BoundedMatcher<View?, Toolbar>(Toolbar::class.java) {
        override fun matchesSafely(toolbar: Toolbar): Boolean {
            return textMatcher.matches(toolbar.subtitle)
        }

        override fun describeTo(description: Description) {
            description.appendText("with toolbar title: ")
            textMatcher.describeTo(description)
        }
    }
}

fun withToolbarTitle(title: CharSequence?): Matcher<View?>? {
    return withToolbarTitle(`is`(title))
}

fun withToolbarSubtitle(title: CharSequence?): Matcher<View?>? {
    return withToolbarSubtitle(`is`(title))
}


fun <T : ViewModel> createViewModelFor(model: T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(model.javaClass)) {
                return model as T
            }
            throw IllegalArgumentException("unexpected model class " + modelClass)
        }

    }


private fun <T> TestObserver<T>.assertValue(
    description: String = "",
    expectedMap: Map<String, Any?>,
    transform: ((T) -> Map<String, Any?>)
): TestObserver<T> {
    val actual = transform(value())
    expectedMap.forEach { (k, v) ->
        Assert.assertEquals(
            "$description property: $k",
            v,
            actual[k]
        )
    }
    return this
}

private fun ArticleState.asMap(): Map<String, Any?> = mapOf(
    "isAuth" to isAuth,
    "isLoadingContent" to isLoadingContent,
    "isLoadingReviews" to isLoadingReviews,
    "isLike" to isLike,
    "isBookmark" to isBookmark,
    "isShowMenu" to isShowMenu,
    "isBigText" to isBigText,
    "isDarkMode" to isDarkMode,
    "isSearch" to isSearch,
    "searchQuery" to searchQuery,
    "searchResults" to searchResults,
    "searchPosition" to searchPosition,
    "shareLink" to shareLink,
    "title" to title,
    "category" to category,
    "categoryIcon" to categoryIcon,
    "date" to date,
    "author" to author,
    "poster" to poster,
    "content" to content,
    "reviews" to reviews,
)