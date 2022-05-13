package com.documentos.appwmsbeirario.ui.configuracoes.temperature

import android.app.AlertDialog
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

public open class BaseActivity : AppCompatActivity() {


    fun showAlertDialogWithAutoDismiss(msg: String, dismiss: String? = null, timer: Long? = 2000) {
        if (dismiss == null) {
            val builder =
                AlertDialog.Builder(this)
            builder.setTitle("Alerta!")
                .setMessage(msg)
                .setCancelable(false).setCancelable(false)
                .setPositiveButton("Ok") { dialog, id -> //this for skip dialog
                    dialog.cancel()
                }
            val alertDialog = builder.create()
            alertDialog.show()
            Handler().postDelayed({
                if (alertDialog.isShowing) {
                    alertDialog.dismiss()
                }
            }, timer!!)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alerta!")
                .setMessage(msg)
                .setCancelable(true)
                .setTitle("Alerta!")
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


}