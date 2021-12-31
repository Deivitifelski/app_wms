package com.documentos.wms_beirario.utils.extensions

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.documentos.wms_beirario.R
import com.example.coletorwms.constants.CustomMediaSonsMp3

private val navOptions = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_in_right)
    .setExitAnim(R.anim.slide_out_left)
    .setPopEnterAnim(R.anim.slide_in_left)
    .setPopExitAnim(R.anim.slide_out_right)
    .build()


fun NavController.navAnimationCreate(destination: NavDirections) {
    this.navigate(destination, navOptions)
}

private val navOptionsBack = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_in_left)
    .setExitAnim(R.anim.slide_out_right)
    .setPopEnterAnim(R.anim.slide_out_left)
    .setPopExitAnim(R.anim.slide_in_right)
    .build()


fun NavController.navAnimationCreateback(destination: NavDirections) {
    this.navigate(destination, navOptionsBack)
}

fun Activity.onBackTransitionExtension() {
    this.onBackPressed()
    this.overridePendingTransition(
        R.anim.slide_in_left,
        R.anim.slide_out_right
    )

}
