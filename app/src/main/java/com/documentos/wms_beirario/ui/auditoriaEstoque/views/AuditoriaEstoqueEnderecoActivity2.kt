package com.documentos.wms_beirario.ui.auditoriaEstoque.views

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityAuditoriaEstoqueEndereco2Binding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListEnderecosAuditoriaEstoque3Item
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoque3
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.clearEdit
import com.documentos.wms_beirario.utils.extensions.extensionStarActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.hideKeyBoardFocus
import java.util.Observable
import java.util.Observer

class AuditoriaEstoqueEnderecoActivity2 : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityAuditoriaEstoqueEndereco2Binding
    private lateinit var adapterEnderecos: AdapterAuditoriaEstoque3
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueViewModel2
    private var auditoria: ListaAuditoriasItem? = null
    private var estante: String? = null
    private var idArmazem: Int? = null
    private var token: String? = null
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuditoriaEstoqueEndereco2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setToolbar()
        setRv()
        setEdit()
        getIntentData()
        getData()
        observer()
        clickKeyInput()
        initDataWedge()
        setupDataWedge()
    }

    private fun clickKeyInput() {
        binding.imageView3.setOnClickListener {
            alertDialog.alertEditText(
                context = this,
                title = "Auditoria de estoque",
                subTitle = "Digite um endereço que deseja apontar",
                actionYes = { readingAndress(it.trim()) },
                actionNo = {}
            )
        }
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

    private fun initConst() {
        adapterEnderecos = AdapterAuditoriaEstoque3()
        alertDialog = CustomAlertDialogCustom()
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueViewModel2.AuditoriaEstoqueViewModel2Factory(AuditoriaEstoqueRepository())
        )[AuditoriaEstoqueViewModel2::class.java]
    }

    private fun setEdit() {
        binding.editEndereco.hideKeyBoardFocus()
    }

    private fun setRv() {
        binding.rvEnderecos.apply {
            adapter = adapterEnderecos
            layoutManager = LinearLayoutManager(this@AuditoriaEstoqueEnderecoActivity2)
        }
    }

    private fun setToolbar() {
        binding.toolbarEnderecos.apply {
            setNavigationOnClickListener { onBackPressed() }
            title = "Endereços"
            subtitle = getVersionNameToolbar()
        }
    }

    private fun getIntentData() {
        if (intent != null) {
            auditoria = intent.getSerializableExtra("AUDITORIA_SELECIONADA") as ListaAuditoriasItem?
            estante = intent.getStringExtra("ESTANTE")
        } else {
            errorInitScreen()
        }
    }

    private fun errorInitScreen() {
        alertDialog.alertMessageErrorSimplesAction(
            this,
            "Ocorreu um erro ao receber dados, volte e tente novamente!",
            action = {
                finishAndRemoveTask()
            })
    }

    private fun getData() {
        if (token != null && idArmazem != null && estante != null && auditoria != null) {
            viewModel.getEnderecos(
                idArmazem = idArmazem!!,
                token = token!!,
                idAuditoriaEstoque = auditoria!!.id,
                estante = estante!!
            )
        } else {
            errorInitScreen()
        }
    }

    private fun observer() {
        viewModel.apply {
            emplyAuditoriasDb()
            notEmplyAuditoriasDb()
            errorDb()
            errorAll()
            validaProgress()
        }
    }


    private fun AuditoriaEstoqueViewModel2.emplyAuditoriasDb() {
        sucessGetAuditoriaEmplyShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { emply ->
            binding.txtInfo.apply {
                visibility = View.VISIBLE
                text = emply
            }
        }
    }

    private fun AuditoriaEstoqueViewModel2.notEmplyAuditoriasDb() {
        sucessGetAuditoriaEnderecoShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { list ->
            adapterEnderecos.update(list)
        }
    }

    private fun AuditoriaEstoqueViewModel2.errorDb() {
        errorDbShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { error ->
            binding.txtInfo.visibility = View.GONE
            alertDialog.alertMessageErrorSimples(this@AuditoriaEstoqueEnderecoActivity2, error)
        }
    }

    private fun AuditoriaEstoqueViewModel2.errorAll() {
        errorAllShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { error ->
            binding.txtInfo.visibility = View.GONE
            alertDialog.alertMessageErrorSimples(this@AuditoriaEstoqueEnderecoActivity2, error)
        }
    }

    private fun AuditoriaEstoqueViewModel2.validaProgress() {
        progressShow.observe(this@AuditoriaEstoqueEnderecoActivity2) { progress ->
            binding.txtInfo.visibility = View.GONE
            binding.progress.isVisible = progress
        }
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            readingAndress(scanData.toString().trim())
        }
    }

    private fun readingAndress(scan: String?) {
        if (scan != null) {
            val andress = adapterEnderecos.contaisInList(scan)
            if (andress != null) {
                validateStartActivityTypeAuditoria(andress)
            } else {
                alertDialog.alertMessageErrorSimplesAction(
                    context = this,
                    "Endereço não existe para auditoria selecionada!",
                    action = {
                        clearEdit(binding.editEndereco)
                    }
                )
            }
        }
    }

    private fun validateStartActivityTypeAuditoria(andress: ListEnderecosAuditoriaEstoque3Item) {
        if (auditoria?.tipo == "AP") {
            sendActivityProductAp(andress)
        } else {
            sendActivityProductCp(andress)
        }

    }

    private fun sendActivityProductCp(andress: ListEnderecosAuditoriaEstoque3Item) {
        val intent = Intent(this, ProdutoAndressAuditoriaEstoqueCpActivity::class.java)
        intent.putExtra("ANDRES_SELECT", andress)
        intent.putExtra("AUDITORIA_SELECT", auditoria)
        startActivity(intent)
        extensionStarActivityanimation(this)
    }

    private fun sendActivityProductAp(andress: ListEnderecosAuditoriaEstoque3Item) {
        val intent = Intent(this, ProdutoAndressAuditoriaEstoqueApActivity::class.java)

        intent.putExtra("ANDRESS_SELECT", andress)
        intent.putExtra("AUDITORIA_SELECT", auditoria)
        intent.putExtra("ESTANTE", estante)
        startActivity(intent)
        extensionStarActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}