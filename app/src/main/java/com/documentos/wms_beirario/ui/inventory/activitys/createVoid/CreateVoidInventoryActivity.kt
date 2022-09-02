package com.documentos.wms_beirario.ui.inventory.activitys.createVoid

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityCreateVoidBinding
import com.documentos.wms_beirario.databinding.LayoutCorrugadoBinding
import com.documentos.wms_beirario.databinding.LayoutRvSelectQntShoesBinding
import com.documentos.wms_beirario.model.inventario.*
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterCreateVoidItem
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventorySelectNum
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterselectQntShoes
import com.documentos.wms_beirario.ui.inventory.adapter.createVoid.AdapterCorrugadosInventory
import com.documentos.wms_beirario.ui.inventory.adapter.createVoid.AdapterCreateObjectPrinter
import com.documentos.wms_beirario.ui.inventory.viewModel.CreateVoidInventoryViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


class CreateVoidInventoryActivity : AppCompatActivity() {

    private val TAG = "CreateVoidInventoryActivity"
    private lateinit var mBinding: ActivityCreateVoidBinding
    private lateinit var mViewModel: CreateVoidInventoryViewModel
    private lateinit var mAdapterTamShoes: AdapterInventorySelectNum
    private lateinit var mAdapterQntShoes: AdapterselectQntShoes
    private lateinit var mAdapterCreateVoid: AdapterCreateVoidItem
    private lateinit var mAdapterPrinter: AdapterCreateObjectPrinter
    private var mQntTotalShoes: Int = 0
    private var mIdcorrugado: Int = 0
    private var mQntItensListPrinter: Int = 0
    private var mQntCorrugadoTotal: Int = 0
    private var mPositionRv: Int? = null
    private var mPosition: Int? = null
    private lateinit var mDialog: Dialog
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mClickBack: Boolean = false
    private lateinit var mIntentDataActivity1: ResponseInventoryPending1
    private lateinit var mIntentProcessaLeitura: ProcessaLeituraResponseInventario2
    var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter


    /**CREATE-->*/
    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityCreateVoidBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initConst()
        setSupportActionBar(mBinding.toolbar)
        startIntent()
        setViews(visibility = false)
        setObservablesCorrugado()
        getEditText()
        clickButton()
        setupNavigation()
        setupToolbar()
        setLayoutVisible(visibilidade = true)
        /**ADAPTER ITENS ADICIONADOS ->*/
        mAdapterPrinter = AdapterCreateObjectPrinter(this) { _, position ->
            mAdapterPrinter.delete(position)

            Log.e("excluindo posiçao -->", position.toString())
            Toast.makeText(this, "Excluido! ", Toast.LENGTH_SHORT).show()
            mQntItensListPrinter = mAdapterPrinter.retornaList()
            mViewModel.setButtomImprimir(mQntItensListPrinter)
            val totalParesList = mAdapterPrinter.totalQnts()
            mBinding.txtInfAdicionados.text =
                "total de pares adicionados $totalParesList corrugado: $mQntCorrugadoTotal"
            val qntAddPrinter = mAdapterPrinter.totalQnts()
            mQntTotalShoes = qntAddPrinter
            mBinding.txtInfTotalItem.text =
                getString(R.string.quantidade_total_calcados_inventory, mQntTotalShoes)
        }
        mAdapterCreateVoid = AdapterCreateVoidItem { itemClick, position ->
            alertSelectQnt(itemClick.tamanho.toInt(), position)
        }
    }

    override fun onStart() {
        super.onStart()
        verificationsBluetooh()
    }

    override fun onResume() {
        super.onResume()
        setupRvSelectTam()
        clickCorrugado()
    }

    private fun initConst() {
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        mDialog = CustomAlertDialogCustom().progress(this)
        mDialog.hide()
        initViewModel()
        buttonEnable(mBinding.buttomLimpar, visibility = false)
        buttonEnable(mBinding.buttomAdicionar, visibility = false)
        AppExtensions.visibilityProgressBar(mBinding.progressPrinter, false)
        buttonEnable(mBinding.buttomImprimir, visibility = false)
        mBinding.buttonAdicionarInventoryCreate.isChecked = true
    }

    private fun startIntent() {
        try {
            val getData = intent
            val mData = getData.getSerializableExtra("SEND_ANDRESS_RESPONSE_ACTIVITY_1Avulso")
            mIntentDataActivity1 = mData as ResponseInventoryPending1
            val data2 = getData.getSerializableExtra("SEND_ANDRESS_REANDING_QRCODEAvulSo")
            mIntentProcessaLeitura = data2 as ProcessaLeituraResponseInventario2
            Log.e(TAG, "startIntent -> $mIntentDataActivity1 || $mIntentProcessaLeitura")
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados!")
        }
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            CreateVoidInventoryViewModel.CreateVoidViewModelFactory(InventoryoRepository1())
        )[CreateVoidInventoryViewModel::class.java]
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            vibrateExtension(500)
            mAlert.alertSelectPrinter(this)
        } else {
            initConfigPrinter()
        }
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
    }


    private fun setupToolbar() {
        mBinding.toolbar.apply {
            setNavigationOnClickListener {
                onBack()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupNavigation() {
        /**ALTERANDO A VISIBILIDADE -->*/
        mBinding.buttonsNavigationCreateVoid.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) when (checkedId) {
                R.id.button_Adicionar_inventory_create -> {
                    mSonsMp3.somClick(this)
                    setLayoutVisible(visibilidade = true)
                }
                R.id.button_Adicionados_inventory_create -> {
                    mSonsMp3.somClick(this)
                    setLayoutVisible(visibilidade = false)
                }
            }
        }
    }

    private fun setLayoutVisible(visibilidade: Boolean) {
        if (visibilidade) {
            mBinding.layoutAdicionar.visibility = View.VISIBLE
            mBinding.layoutAdicionados.visibility = View.INVISIBLE
        } else {
            mBinding.layoutAdicionar.visibility = View.INVISIBLE
            mBinding.layoutAdicionados.visibility = View.VISIBLE
        }
    }


    /**ALERTA DO CORRUGADO -->*/
    private fun alertCorrugado(listCorrugados: InventoryResponseCorrugados) {
        val mAlert = BottomSheetDialog(
            this,
            R.style.BottomSheetStyle
        )
        val mbindingDialog = LayoutCorrugadoBinding.inflate(layoutInflater)
        mAlert.setContentView(mbindingDialog.root)
        /**CLICK ITEM CORRUGADO -->*/
        val mAdapterCorrugados = AdapterCorrugadosInventory { itemClickCorrugado ->
            mSonsMp3.somClick(this)
            mQntCorrugadoTotal = itemClickCorrugado.quantidadePares
            mIdcorrugado = itemClickCorrugado.id
            mAdapterPrinter.updateCorrugado(itemClickCorrugado.quantidadePares, mIdcorrugado)
            mBinding.buttonSelecioneCorrugado.text = getString(
                R.string.Corrugado_select,
                itemClickCorrugado.id,
                itemClickCorrugado.quantidadePares
            )
            buttonEnable(mBinding.buttomLimpar, visibility = true)
            setViews(visibility = true)
            setTxtInfAdicionados(boolean = true)
            UIUtil.showKeyboard(this, mBinding.editLinha)
            mBinding.editLinha.requestFocus()
            mAlert.dismiss()
        }
        mbindingDialog.rvCorrugados.apply {
            layoutManager = LinearLayoutManager(this@CreateVoidInventoryActivity)
            adapter = mAdapterCorrugados
        }
        mAdapterCorrugados.submitList(listCorrugados)
        mAlert.show()
    }

    /**CONFIGURAÇAO RECYCLERVIEW TAMANHO -->*/
    private fun setupRvSelectTam(position: Int? = null) {
        /**CLICK TAMANHO SELECIONADO -->*/
        mAdapterTamShoes = AdapterInventorySelectNum { tamSelect, position1 ->
            mBinding.rvTamShoe.smoothScrollToPosition(position1 + 6)
            mPositionRv = position1
            alertSelectQnt(tamSelect, position)
        }
        /**CHAMADA PARA CRIAR RECYCLERVIEW DE TAMANHOS -->*/
        mViewModel.getListTamShoes(first = 20, last = 45)
        /**RESULT CRIAÇAO -->*/
        mViewModel.mSucessCreateListShow.observe(this) { listSelectTam ->
            mAdapterTamShoes.update(listSelectTam, mPosition ?: 0)
        }
        mBinding.rvTamShoe.apply {
            layoutManager =
                GridLayoutManager(
                    this@CreateVoidInventoryActivity,
                    2,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
            adapter = mAdapterTamShoes
        }
    }

    /**ALERTA SELECIONE QUNTIDADE -->*/
    private fun alertSelectQnt(tamSelect: Int, position: Int? = null) {
        val alert = AlertDialog.Builder(this)
        val mbindingDialog = LayoutRvSelectQntShoesBinding.inflate(layoutInflater)
        alert.setView(mbindingDialog.root)
        mbindingDialog.txtInf.text = getString(R.string.selecione_qnt_para_numero, tamSelect)
        alert.setCancelable(true)

        alert.create()
        val mAlert = alert.show()
        /**CLICK ITEM ALERT QUANTIDADE DE SAPATATOS -->*/
        mAdapterQntShoes = AdapterselectQntShoes { qntShoes ->
            if (qntShoes == 0 && mAdapterCreateVoid.returList().isEmpty()) {
                vibrateExtension(500)
                Toast.makeText(this, "Lista vazia!!!", Toast.LENGTH_SHORT).show()
            } else if (qntShoes == 0) {
                mAdapterCreateVoid.deleteObject(tamSelect)
                vibrateExtension(500)
                Toast.makeText(this, "Excluido", Toast.LENGTH_SHORT).show()
                setupButtonAdd()
                setTotalQntParesTxt()
            } else {
                setupCreateVoid(tamSelect, qntShoes, position)
                setupButtonAdd()
                setTotalQntParesTxt()
            }
            mAlert.dismiss()
        }
        mbindingDialog.rvSelectAlert.apply {
            layoutManager = GridLayoutManager(this@CreateVoidInventoryActivity, 2)
            adapter = mAdapterQntShoes
        }
        mViewModel.getListQntShoes(first = 0, last = 10)
        mViewModel.mSucessCreateListALertShow.observe(this) { listQntShoes ->
            mAdapterQntShoes.updateAlertDialog(listQntShoes)
        }
    }

    private fun setTotalQntParesTxt() {
        val qntVoid = mAdapterCreateVoid.getQntShoesObject()
        val qntAddPrinter = mAdapterPrinter.totalQnts()
        mQntTotalShoes = qntVoid + qntAddPrinter
        mBinding.txtInfTotalItem.text =
            getString(R.string.quantidade_total_calcados_inventory, mQntTotalShoes)
    }

    /**CRIA OBJETOS A VULSO -->*/
    private fun setupCreateVoid(tamSelect: Int, qntShoes: Int, position: Int?) {
        mBinding.rvCriaObject.apply {
            layoutManager = LinearLayoutManager(
                this@CreateVoidInventoryActivity,
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = mAdapterCreateVoid
        }
        val objectoCreateVoid = Distribuicao(tamanho = tamSelect.toString(), quantidade = qntShoes)
        mAdapterCreateVoid.updateCreateVoid(
            mQntCorrugadoTotal,
            mQntTotalShoes,
            this,
            objectoCreateVoid,
            position
        )
    }

    /**CLICK BUTTON CORRUGADO / FAZ A CHAMADA A API DOS CORRUGADOS -->*/
    private fun clickCorrugado() {
        mBinding.buttonSelecioneCorrugado.setOnClickListener {
            mSonsMp3.somClick(this)
            mViewModel.getCorrugados()
            mDialog.show()
        }
    }

    private fun setObservablesCorrugado() {
        /**RESPOSTA DA API TRAZER CORRUGADOS ---------------------------------------------------->*/
        mViewModel.mSucessCorrugados.observe(this) { listCorrugados ->
            alertCorrugado(listCorrugados)
            mDialog.hide()
        }
        mViewModel.mErrorCorrugados.observe(this) {
            mToast.snackBarErrorSimples(
                mBinding.root,
                getString(R.string.erro_ao_carregar_lista)
            )
            mDialog.hide()
        }
        /**RESPOSTA DA API AO IMPRIMIR --------------------------------------------------------->*/
        mViewModel.mSucessPrinterShow.observe(this) { etiqueta ->
            lifecycleScope.launch(Dispatchers.Default) {
                writer.write(etiqueta)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                mDialog.hide()
            }, 2000)
        }

        mViewModel.mErrorPrinterShow.observe(this) { messageErrorPrinter ->
            mDialog.hide()
            vibrateExtension(500)
            mAlert.alertMessageErrorSimples(this, messageErrorPrinter)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mDialog.hide()
            vibrateExtension(500)
            mAlert.alertMessageErrorSimples(this, error)

        }
    }

    private fun setViews(visibility: Boolean) {
        if (visibility) {
            mBinding.containerParent.visibility = View.VISIBLE
        } else {
            mBinding.containerParent.visibility = View.INVISIBLE
        }
    }


    private fun getEditText() {
        mBinding.editReferencia.addTextChangedListener {
            setupButtonAdd()
        }
        mBinding.editLinha.addTextChangedListener {
            setupButtonAdd()
        }
        mBinding.editCor.addTextChangedListener {
            setupButtonAdd()
        }
        mBinding.editCabecal.addTextChangedListener {
            setupButtonAdd()
        }
    }

    private fun setupButtonAdd() {
        mViewModel.setButtonAdd(
            mBinding.editCabecal.text.toString(),
            mBinding.editCor.text.toString(),
            mBinding.editLinha.text.toString(),
            mBinding.editReferencia.text.toString(),
            mQntTotalShoes,
            mQntCorrugadoTotal,
            mAdapterCreateVoid.returList()

        )
        mViewModel.mResponseButtonAddShow.observe(this) { visibilityButtonAdd ->
            if (visibilityButtonAdd) buttonEnable(
                mBinding.buttomAdicionar,
                true
            ) else
                buttonEnable(mBinding.buttomAdicionar, visibility = false)
        }
    }

    /**CLICK BUTTON ABA ADICIONAR ->*/
    private fun clickButton() {
        mBinding.buttomLimpar.setOnClickListener {
            setTxtInfAdicionados(boolean = false)
            mSonsMp3.somClick(this)
            mBinding.apply {
                editCabecal.setText("")
                editCor.setText("")
                editLinha.setText("")
                editReferencia.setText("")
                mAdapterCreateVoid.clearList()
                mBinding.buttonSelecioneCorrugado.text =
                    getString(R.string.texto_select_corrugado)
            }
            setViews(visibility = false)
        }

        mBinding.buttomAdicionar.setOnClickListener {
            setupRecyclerViewAdicionados()
            mBinding.buttonAdicionadosInventoryCreate.isChecked = true
            setLayoutVisible(visibilidade = false)
            setupfields()
        }

        mBinding.buttomImprimir.setOnClickListener {
            if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
                mErroToastExtension(this, "Nenhuma impressora conectada!")
            } else {
                mDialog.show()
                mSonsMp3.somClick(this)
                setDataPrinter()
            }
        }
    }

    private fun setupfields() {
        mBinding.apply {
            editCabecal.setText("")
            editCor.setText("")
            editLinha.setText("")
            editReferencia.setText("")
            mAdapterCreateVoid.clearList()
        }
        //testes -->
        setTxtInfAdicionados(boolean = true)

    }

    @SuppressLint("SetTextI18n")
    private fun setTxtInfAdicionados(boolean: Boolean) {
        if (boolean) {
            val totalParesList = mAdapterPrinter.totalQnts()
            mBinding.txtInfAdicionados.text =
                "Total de pares adicionados: $totalParesList corrugado: $mQntCorrugadoTotal"
        } else {
            val totalParesList = mAdapterPrinter.totalQnts()
            mBinding.txtInfAdicionados.text =
                "Total de pares adicionados: $totalParesList corrugado: 0 "
        }
    }

    private fun setupRecyclerViewAdicionados() {
        /**ENVIANDO LISTA ->*/
        mBinding.rvObjetoImpressao.apply {
            layoutManager = LinearLayoutManager(this@CreateVoidInventoryActivity)
            adapter = mAdapterPrinter
        }
        val listAdicionados = Combinacoes(
            mBinding.editCabecal.text.toString().toInt(),
            mBinding.editCor.text.toString().toInt(),
            mBinding.editLinha.text.toString().toInt(),
            mBinding.editReferencia.text.toString().toInt(),
            mQntCorrugadoTotal,
            mIdcorrugado,
            distribuicao = mAdapterCreateVoid.returList().toMutableList()
        )
        mAdapterPrinter.update(listAdicionados)
        mQntItensListPrinter = mAdapterPrinter.retornaList()
        mViewModel.setButtomImprimir(mQntItensListPrinter)
        mViewModel.mResponseListImprimirClearoffShow.observe(this) { validButtonPrinter ->
            mBinding.buttomImprimir.isEnabled = validButtonPrinter
        }
    }

    /**
     * Enviando Objeto para impressao -->
     */
    private fun setDataPrinter() {
        val list = mAdapterPrinter.retornaListPrinter()
        mViewModel.postPrinter(
            idInventario = mIntentDataActivity1.id,
            createVoidPrinter = CreateVoidPrinter(
                combinacoes = list,
                codigoCorrugado = mIdcorrugado
            ),
            idEndereco = mIntentProcessaLeitura.idEndereco!!,
            numeroContagem = mIntentDataActivity1.numeroContagem
        )
    }

    private fun onBack() {
        if (mClickBack) {
            onBackPressed()
        } else {
            mClickBack = true
            Handler(Looper.getMainLooper()).postDelayed({ mClickBack = false }, 2000)
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(
                mBinding.root,
                "Clique novamente para voltar!"
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_printer -> {
                extensionStartActivity(BluetoohPrinterActivity())
            }
        }
        return true
    }

    /**CLICK MENU ----------->*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_open_printer, menu)
        return true
    }

    /**funcao que retorna a primeira tela de separacao a lista -->*/
    private fun returIventory2() {
        val intent = Intent()
        intent.putExtra("DATA_INVENTORY_2", mIntentProcessaLeitura)
        Log.e("CRIANDO A VULSO PARA INVENTORY 2", "returInventory --> $mIntentProcessaLeitura ")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        returIventory2()
        mDialog.dismiss()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog.dismiss()
    }
}