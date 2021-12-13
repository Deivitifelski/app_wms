package com.documentos.wms_beirario.extensions

import android.app.Activity
import android.content.Intent
import com.documentos.wms_beirario.R
import com.example.coletorwms.constants.CustomMediaSonsMp3

fun Activity.extensionStartActivity(activity: Activity){
    CustomMediaSonsMp3().somClick(this)
    startActivity(Intent(this,activity::class.java))
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

}

fun Activity.extensionStarBacktActivity(activity: Activity){
    CustomMediaSonsMp3().somClick(this)
    startActivity(Intent(this,activity::class.java))
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

}