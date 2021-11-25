package com.documentos.wms_beirario.extensions

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.documentos.wms_beirario.R

private val navOptions = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_in_right)
    .setExitAnim(R.anim.slide_out_left)
    .setPopEnterAnim(R.anim.slide_in_left)
    .setPopExitAnim(R.anim.slide_out_right)
    .build()


fun NavController.navigationAnimationCreate(destination: NavDirections) {
    this.navigate(destination, navOptions)
}

fun Activity.onBackTransition() {
    this.onBackPressed()
    this.overridePendingTransition(
        R.anim.slide_in_left,
        R.anim.slide_out_right
    )

}
