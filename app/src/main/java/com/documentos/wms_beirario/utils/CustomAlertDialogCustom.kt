package com.documentos.wms_beirario.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.LayoutCustomDialogBinding
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugados
import com.example.coletorwms.constants.CustomMediaSonsMp3

class CustomAlertDialogCustom() {

    fun alertMessageSucess(context: Context, message: String) {
        CustomMediaSonsMp3().somSucess(context)
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.layout_alert_sucess_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.show()
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_sucess)
        medit.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        val mText = inflate.findViewById<TextView>(R.id.txt_message_sucess)
        val mButton = inflate.findViewById<Button>(R.id.button_sucess_layout_custom)
        mText.text = message
        mButton.setOnClickListener {
            mShow.dismiss()
            CustomMediaSonsMp3().somClick(context)
        }
        mAlert.create()
    }

    fun alertMessageErrorSimples(context: Context, message: String) {
        CustomMediaSonsMp3().somError(context)
        val mAlert = AlertDialog.Builder(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.show()
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_error)
        medit.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        val mText = inflate.findViewById<TextView>(R.id.txt_message_atencao)
        val mButton = inflate.findViewById<Button>(R.id.button_atencao_layout_custom)
        mText.text = message
        mButton.setOnClickListener {
            mShow.dismiss()
            CustomMediaSonsMp3().somClick(context)
        }
        mAlert.create()
    }

    fun alertMessageErrorCancelFalse(context: Context, message: String) {
        CustomMediaSonsMp3().somError(context)
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.show()
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_error)
        medit.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        val mText = inflate.findViewById<TextView>(R.id.txt_message_atencao)
        val mButton = inflate.findViewById<Button>(R.id.button_atencao_layout_custom)
        mText.text = message
        mButton.setOnClickListener {
            mShow.hide()
            CustomMediaSonsMp3().somClick(context)
        }
        mAlert.create()
    }

    fun progress(context: Context, message: String): Dialog {
        val mDialog = Dialog(context)
        mDialog.setCancelable(false)
        val mInflater = LayoutCustomDialogBinding.inflate(LayoutInflater.from(context))
        val mText = mInflater.txtCustomProgress
        mDialog.setContentView(mInflater.root)
        mText.text = message
        mDialog.show()
        return mDialog
    }


}