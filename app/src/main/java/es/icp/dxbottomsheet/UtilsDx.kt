package es.icp.dxbottomsheet

import android.view.View

fun View.visible() { this.visibility = View.VISIBLE }
fun View.hide() { this.visibility = View.GONE }
fun View.show(show: Boolean) = if (show) visible() else hide()

fun View.goneAlpha() = this.animate().alpha(0f).setDuration(250).withEndAction { this.hide() }.start()

fun View.visibleAlpha(){
    this.apply {
        alpha = 0f
        visible()
        animate().alpha(1f).setDuration(400).start()
    }

}

