package com.documentos.wms_beirario.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.os.Vibrator
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.LayoutAlertAtencaoBinding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.databinding.LayoutCustomDialogBinding
import com.documentos.wms_beirario.databinding.LayoutCustomImpressoraBinding
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.bluetooh.BluetoohTeste

class CustomAlertDialogCustom {

    /**
     * MODAL QUANDO FINALIZOU TODOS OS ITENS -->
     */
    fun alertMessageSucessFinishBack(
        context: Context,
        activity: Activity? = null,
        message: String
    ) {
        val mAlert = AlertDialog.Builder(activity)
        mAlert.setCancelable(false)
        val binding = LayoutAlertSucessCustomBinding.inflate(LayoutInflater.from(context))
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        mAlert.create()
        binding.editCustomAlertSucess.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        binding.txtMessageSucess.text = message
        binding.buttonSucessLayoutCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()
            if (activity != null) {
                activity.onBackPressed()
            }
        }
    }

    /**
     * MODAL ATENÇÃO E VOLTA TELA ANTERIOR -->
     */
    fun alertMessagemAtencaoFinishBack(
        context: Context,
        activity: Activity? = null,
        message: String
    ) {
        val mAlert = AlertDialog.Builder(activity)
        mAlert.setCancelable(false)
        val binding = LayoutAlertAtencaoBinding.inflate(LayoutInflater.from(context))
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        mAlert.create()
        binding.editCustomAlertAlert.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        binding.txtMessageAtencao.text = message
        binding.buttonAtencaoLayoutCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(context)
            mShow.dismiss()
            if (activity != null) {
                activity.onBackPressed()
            }
        }
    }


    fun alertErroInitBack(
        context: Context,
        activity: Activity? = null,
        title: String? = null
    ) {
        this.vibrar(context)
        val mAlertDialog = AlertDialog.Builder(activity)
            .setCancelable(false)
            .setTitle("ALERTA!")
            .setIcon(R.drawable.ic_alert_warning)
        if (title != null) {
            mAlertDialog.setMessage(title)
        } else {
            mAlertDialog.setMessage("Erro! \nVoltando a tela anterior! ")
        }

            .setPositiveButton("Ok") { dialog, which ->
                activity?.onBackPressed()
            }
        mAlertDialog.create().show()
    }


    fun vibrar(context: Context): Vibrator {
        return (context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator).also {
            it.vibrate(500)
        }
    }

    fun alertMessageSucess(context: Context, message: String, timer: Long? = null) {
        CustomMediaSonsMp3().somSucess(context)
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.layout_alert_sucess_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.create()
        mShow.show()
        if (mShow.isShowing) {
            if (timer != null) {
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    mShow.dismiss()
                }, timer)
            }
        }
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

    fun alertMessageErrorSimples(
        context: Context,
        message: String,
        timer: Long? = null,
        show: Boolean? = false
    ) {
        CustomMediaSonsMp3().somError(context)
        this.vibrar(context)
        val mAlert = AlertDialog.Builder(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.create()
        mShow.show()
        if (mShow.isShowing) {
            if (timer != null) {
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    mShow.dismiss()
                }, timer)
            }
        } else if (show == true) {
            mShow.dismiss()
        }
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_error)
        medit.requestFocus()
        medit.addTextChangedListener {
            if (it.toString() != "") {
                CustomMediaSonsMp3().somClick(context)
                mShow.dismiss()
                mShow.hide()
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

    fun alertMessageErrorCancelFalse(context: Context, message: String, timer: Long? = null) {
        CustomMediaSonsMp3().somError(context)
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.create()
//        mShow.window!!.attributes.windowAnimations = R.style.MyAnimationAlertDialogOk
        mShow.show()
        if (mShow.isShowing) {
            if (timer != null) {
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    mShow.dismiss()
                }, timer)
            }
        }
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

    fun progress(context: Context, message: String = "Aguarde..."): Dialog {
        val mDialog = Dialog(context)
        mDialog.setCancelable(false)
        val mInflater = LayoutCustomDialogBinding.inflate(LayoutInflater.from(context))
        val mText = mInflater.txtCustomProgress
        mDialog.setContentView(mInflater.root)
        mText.text = message
        mDialog.show()
        return mDialog
    }

    fun alertMessageAtencao(context: Context, message: String, timer: Long? = null) {
        CustomMediaSonsMp3().somAlerta(context)
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_alert_atencao, null)
        mAlert.apply {
            setView(inflate)

        }
        val mShow = mAlert.create()
//        mShow.window!!.attributes.windowAnimations = R.style.MyAnimationAlertDialogOk
        mShow.show()
        if (mShow.isShowing) {
            if (timer != null) {
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    mShow.dismiss()
                }, timer)
            }
        }
        val mEdit = inflate.findViewById<EditText>(R.id.edit_custom_alert_alert)
        val mText = inflate.findViewById<TextView>(R.id.txt_message_atencao)
        val mButton = inflate.findViewById<Button>(R.id.button_atencao_layout_custom)
        mText.text = message
        mEdit.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        mButton.setOnClickListener {
            mShow.dismiss()
            CustomMediaSonsMp3().somClick(context)
        }
        mAlert.create()
    }


    fun alertSelectPrinter(context: Context, msg: String? = null) {
        vibrar(context)
        val mAlert = AlertDialog.Builder(context)
        CustomMediaSonsMp3().somAtencao(context)
        val bindingAlert = LayoutCustomImpressoraBinding.inflate(LayoutInflater.from(context))
        mAlert.setView(bindingAlert.root)
        val mShow = mAlert.create()
        if (msg.isNullOrEmpty()) {
            bindingAlert.textImpressoar1.text = context.getString(R.string.alert_select_printer)
        } else {
            bindingAlert.textImpressoar1.text = msg
        }
        bindingAlert.buttonSimImpressora1.setOnClickListener {
            context.startActivity(Intent(context, BluetoohTeste::class.java))
            mShow.dismiss()
        }
        bindingAlert.buttonNaoImpressora1.setOnClickListener {
            mShow.dismiss()
        }
        mShow.show()
    }


    fun alertSucessFinishBack(
        activity: Activity,
        message: String
    ) {
        CustomMediaSonsMp3().somSucess(activity)
        val mAlert = AlertDialog.Builder(activity)
        mAlert.setCancelable(false)
        val inflate = LayoutInflater.from(activity)
            .inflate(R.layout.layout_alert_sucess_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.create()
        mShow.show()
        val mText = inflate.findViewById<TextView>(R.id.txt_message_sucess)
        val mButton = inflate.findViewById<Button>(R.id.button_sucess_layout_custom)
        mText.text = message
        mAlert.create()
        mButton.setOnClickListener {
            mShow.hide()
            mShow.dismiss()
            activity.onBackPressed()
        }

    }

    fun alertErrorFinishBack(
        fragment: Fragment,
        message: String
    ) {
        CustomMediaSonsMp3().somError(fragment.requireContext())
        val mAlert = AlertDialog.Builder(fragment.requireContext())
        mAlert.setCancelable(false)
        val inflate = LayoutInflater.from(fragment.requireContext())
            .inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.create()
        mShow.show()
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
            mShow.dismiss()
            fragment.findNavController().navigateUp()
        }
        mAlert.create()
    }


}



