package com.example.coletorwms.constants

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.documentos.wms_beirario.R
import com.google.android.material.snackbar.Snackbar

class CustomSnackBarCustom {

    fun snackBarErrorAction(view: View, str: String) {
        val snackbar = Snackbar.make(
            view, str,
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Action", null)
        snackbar.apply {
            this.setBackgroundTint(Color.RED)
            this.dismiss()
            this.setActionTextColor(Color.WHITE)
            this.setTextColor(Color.WHITE)
            this.setActionTextColor(Color.WHITE)
            this.setAction("Ok", {})
        }.show()
    }

    fun snackBarErrorSimples(view: View, str: String) {
        val snackbar = Snackbar.make(
            view, str,
            Snackbar.LENGTH_SHORT
        ).setAction("Action", null)
        snackbar.apply {
            this.setBackgroundTint(Color.RED)
            this.setActionTextColor(Color.WHITE)
            this.setTextColor(Color.WHITE)
        }.show()
    }

    fun snackBarSimplesBlack(view: View, str: String) {
        val snackbar = Snackbar.make(
            view, str,
            Snackbar.LENGTH_SHORT
        ).setAction("Action", null)
        snackbar.apply {
            this.setBackgroundTint(Color.BLACK)
            this.setActionTextColor(Color.WHITE)
            this.setTextColor(Color.WHITE)
        }.show()
    }

    fun snackBarPadraoSimplesBlack(view: View, str: String) {
        val snackbar = Snackbar.make(
            view, str,
            Snackbar.LENGTH_SHORT
        ).setAction("Action", null)
        snackbar.apply {
            this.setBackgroundTint(Color.BLACK)
            this.setActionTextColor(Color.WHITE)
            this.setTextColor(Color.WHITE)
        }.show()
    }

    fun snackBarSucess(context: Context, view: View, str: String) {
        val snackbar = Snackbar.make(
            view, str,
            Snackbar.LENGTH_SHORT
        ).setAction("Action", null)
        snackbar.apply {
            this.setBackgroundTint(ContextCompat.getColor(context, R.color.green_verde_padrao))
            this.setActionTextColor(Color.WHITE)
            this.setTextColor(Color.WHITE)
            this.setActionTextColor(Color.WHITE)
        }.show()
    }
}