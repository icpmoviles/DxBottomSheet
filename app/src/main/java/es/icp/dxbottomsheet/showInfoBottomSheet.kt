package es.icp.dxbottomsheet

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.fragment.app.FragmentManager

fun FragmentManager.showInfoBottomSheet(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    cancelOnTouchOutSide: Boolean = true,
    cancelable: Boolean = true
) =
    BottomSheetDx.Builder.Info()
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon)
        .setCancelOnTouchOutSide(cancelOnTouchOutSide)
        .setCancelable(cancelable)
        .buildAndShow(this)

fun FragmentManager.showLottieBottomSheet(
    @DrawableRes icon: Int,
    title: String,
    cancelOnTouchOutSide: Boolean = true,
    cancelable: Boolean = true,
    @RawRes lottieFile: Int,
    lottieLoop: Boolean = true
) =
    BottomSheetDx.Builder.LottieOrImage()
        .setIcon(icon)
        .setTitle(title)
        .setLottie(lottieFile)
        .setLottieLoop(lottieLoop)
        .setCancelOnTouchOutSide(cancelOnTouchOutSide)
        .setCancelable(cancelable)
        .buildAndShow(this)


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show(show: Boolean) =
    if (show) visible() else hide()
