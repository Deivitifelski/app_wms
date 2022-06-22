package com.documentos.wms_beirario.utils.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun String.convertDataHora(date: String):String {
    val dateTime =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.parse(
                date,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm")
    val formatted = dateTime.format(formatter)
    return formatted.toString()
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.convertData(date: String):String {
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