package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoriaApontVolBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueCv
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.hideKeyBoardFocus
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import com.documentos.wms_beirario.utils.extensions.toastSucess
import java.util.Observable
import java.util.Observer

class AuditoriaApontVolActivity : AppCompatActivity(), Observer {

    private val TAG = "AuditoriaAPontVolActivity"
    private lateinit var binding: ActivityAuditoriaApontVolBinding
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var somMp3: CustomMediaSonsMp3
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var idArmazem: Int? = null
    private var token: String? = null
    private var andress: ListEnderecosAuditoriaEstoque3Item? = null
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditoria_apont_vol)
        initConst()
        getIntentActivity()
        setLayout()
        initDataWedge()
        setupDataWedge()
        clickKey()
    }

    private fun initConst() {
        binding.editApontVol.hideKeyBoardFocus()
        alertDialog = CustomAlertDialogCustom()
        somMp3 = CustomMediaSonsMp3()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
    }


    private fun clickKey() {
        binding.imageView3.setOnClickListener {
            alertDialog.alertEditText(
                context = this,
                title = "Auditoria de estoque/Aponta volume",
                subTitle = "Digite um Volume que deseja apontar",
                actionYes = { sendScan(it) },
                actionNo = { hideKeyExtensionActivity(binding.editApontVol) }
            )
        }
    }


    private fun getIntentActivity() {
        try {
            if (intent != null) {
                auditoria = intent.getSerializableExtra("AUDITORIA_SELECT") as ListaAuditoriasItem?
                estante = intent.getStringExtra("ESTANTE")
                andress =
                    intent.getSerializableExtra("ANDRESS_SELECT") as ListEnderecosAuditoriaEstoque3Item
                if (auditoria != null && estante != null && andress != null) {
//                    getData()
                } else {
                    errorInitScreen()
                }
            } else {
                errorInitScreen()
            }
        } catch (e: Exception) {
            errorInitScreen()
        }
    }

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }

    private fun setLayout() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width: Int = displayMetrics.widthPixels
        val height: Int = displayMetrics.heightPixels

        window.setLayout((width * 0.9).toInt(), (height * 0.8).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = 75

        window.attributes = params
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            sendScan(scanData.toString())
        }
    }

    private fun sendScan(scan: String) {
        toastSucess(this, scan)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}