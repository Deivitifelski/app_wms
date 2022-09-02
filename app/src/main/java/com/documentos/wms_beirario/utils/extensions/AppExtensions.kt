package com.documentos.wms_beirario.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object AppExtensions {

    @SuppressLint("NewApi")
    fun formatDataEHora(date: String): String {
        val dateTime =
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm ")
        val formatted = dateTime.format(formatter)
        return formatted.toString()
    }

    fun formatData(date: String): String {
        val dateTime =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.parse(
                    date,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val formatted = dateTime.format(formatter)
        return formatted.toString()
    }

    @SuppressLint("NewApi")
    fun formatHora(hora: String): String {
        val dateTime =
            LocalDateTime.parse(hora, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formatted = dateTime.format(formatter)
        return formatted.toString()
    }

    fun Date.formatData(): String {
        val locale = Locale("pt", "BR")
        return SimpleDateFormat("dd/MM/yyyy", locale).format(this)
    }

    fun visibilityProgressBar(progressBar: ProgressBar, visibility: Boolean = true) {
        progressBar.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    fun visibilityTxt(texto: TextView, visibility: Boolean) {
        texto.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    fun vibrar(context: Context): Vibrator {
        return (context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator).also {
            it.vibrate(500)
        }
    }

    fun onBackToolbar(context: AppCompatActivity, toolbar: androidx.appcompat.widget.Toolbar) {
        toolbar.setNavigationOnClickListener {
            context.onBackPressed()
            CustomMediaSonsMp3().somClick(context)
            context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    fun Activity.transitionBack(context: AppCompatActivity) {
        context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


}