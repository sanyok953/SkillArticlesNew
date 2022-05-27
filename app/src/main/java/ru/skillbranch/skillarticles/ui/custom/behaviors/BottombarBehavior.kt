package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import ru.skillbranch.skillarticles.ui.custom.Bottombar
import androidx.core.math.MathUtils

class BottombarBehavior() : CoordinatorLayout.Behavior<Bottombar>() {

    constructor(context: Context, attrs: AttributeSet) : this()

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: Bottombar,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: Bottombar,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if(!child.isSearchMode){
            val offset = MathUtils.clamp(child.translationY + dy, 0f, child.height.toFloat())
            if (offset != child.translationY) child.translationY = offset
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    /*private var height: Float = 0f

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, consumed, type)
        child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dyConsumed))
        /*when {
            dyConsumed > 0 -> slideDown(child)
            dyConsumed < 0 -> slideUp(child)
        }*/
    }


    private fun slideUp(child: V) {
        child.clearAnimation()
        child.animate().translationY(0f).duration = 200
    }

    private fun slideDown(child: V) {
        child.clearAnimation()
        child.animate().translationY(height).duration = 200
    }*/
}