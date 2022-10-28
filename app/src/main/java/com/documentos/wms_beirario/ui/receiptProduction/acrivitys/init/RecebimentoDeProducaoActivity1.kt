package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.init

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityRecebimentoDeProducao1Binding
import com.documentos.wms_beirario.databinding.LayoutAlertdialogCustomFiltrarOperadorBinding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishAndressBinding
import com.documentos.wms_beirario.model.receiptproduct.*
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.activitysFilterSupervisor.FilterSupervisorActivity1
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters.AdapterReceiptProduct1
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.viewModels.ReceiptProductViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class RecebimentoDeProducaoActivity1 : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityRecebimentoDeProducao1Binding
    private val TAG = "ReceiptProductFragment1"
    private lateinit var mAdapter: AdapterReceiptProduct1
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mViewModel: ReceiptProductViewModel1
    private lateinit var mProgress: Dialog
    private lateinit var mProgressFinish: Dialog
    private val mSonSucess: CustomMediaSonsMp3 = CustomMediaSonsMp3()
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private var mAlert: AlertDialog? = null
    private var mIdOperador: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecebimentoDeProducao1Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupEditQrCode()
        setupDataWedge()
        setupRecyclerView()
        setToolbar()
        setupObservables()
        setupReflesh()
        clickButtonFinish()

    }

    override fun onStart() {
        super.onStart()
        mProgress.hide()
        mProgressFinish.hide()
        getApi()
    }


    override fun onResume() {
        super.onResume()
        clearEdit()
        initDataWedge()
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    /**REFLESH NA TELA -->*/
    private fun setupReflesh() {
        mBinding.reflesRecProd.apply {
            setOnRefreshListener {
                setColorSchemeColors(
                    ContextCompat.getColor(
                        this@RecebimentoDeProducaoActivity1,
                        R.color.color_default
                    )
                )
                mBinding.txtInf.visibility = View.INVISIBLE
                mBinding.progress.isVisible = true
                setupRecyclerView()
                getApi()
                mBinding.imageLottie.playAnimation()
                isRefreshing = false
            }
        }
    }

    private fun setupEditQrCode() {
        /**INIT VIEWMODEL -->*/
        hideKeyExtensionActivity(mBinding.editRceipt1)
        mProgress = CustomAlertDialogCustom().progress(this, "Validando acesso...")
        mProgressFinish = CustomAlertDialogCustom().progress(this, "Aguarde...")
        mProgress.hide()
        mShared = CustomSharedPreferences(this)
        mIdOperador = mShared.getString(CustomSharedPreferences.ID_OPERADOR).toString()
        mBinding.imageLottie.isVisible = false
        mDialog = CustomAlertDialogCustom()
        mViewModel = ViewModelProvider(
            this, ReceiptProductViewModel1.ReceiptProductViewModel1Factory(
                ReceiptProductRepository()
            )
        )[ReceiptProductViewModel1::class.java]
    }

    private fun clearEdit() {
        mBinding.editRceipt1.setText("")
        mBinding.editRceipt1.requestFocus()
    }

    private fun setupRecyclerView() {
        /**CIQUE NO ITEM -->*/
        mAdapter = AdapterReceiptProduct1 { itemClick ->
            Log.e(TAG, "ENVIANDO PARA RECEBMENTO 2 -> $itemClick")
            val intent = Intent(this, RecebimentoDeProducaoActivity2::class.java)
            intent.putExtra("ITEM_CLICADO", itemClick)
            startActivity(intent)
            extensionSendActivityanimation()
        }
        mBinding.recyclerViewReceipt.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoDeProducaoActivity1)
            adapter = mAdapter
        }
    }

    private fun setToolbar() {
        setSupportActionBar(mBinding.toolbar)
        mBinding.toolbar.subtitle = getVersionNameToolbar()
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
            extensionBackActivityanimation(this)
        }
    }

    /**CONTAGEM TOTAL DE PEDIDOS -->*/
    private fun countllNumberOrder(listReceipt: List<ReceiptProduct1>): Int {
        var mTotalOrder = 0
        listReceipt.forEach {
            mTotalOrder += it.quantidadeVolumes
        }
        return mTotalOrder
    }

    /**PRIMEIRA CHAMADA API TRAS PENDENCIAS DO USUARIO LOGADO -->*/
    private fun getApi() {
        mViewModel.getReceipt1(filtrarOperador = true, mIdOperador = mIdOperador ?: "0")
    }


    private fun setupObservables() {
        mViewModel.mValidaProgressReceiptShow.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }

        mViewModel.mSucessReceiptShow.observe(this) { listReceipt ->
            //Total de pedidos -->
            mBinding.txtTotalOrder.text = "Total de volumes: ${countllNumberOrder(listReceipt)}"
            //CASO VAZIA -->
            if (listReceipt.isEmpty()) {
                mBinding.apply {
                    txtInf.visibility = View.VISIBLE
                    txtInf.text = getText(R.string.list_emply)
                    buttonFinishAll.isEnabled = false
                }
                mBinding.imageLottie.isVisible = true
            } else {
                mAdapter.submitList(listReceipt)
                mBinding.apply {
                    txtInf.visibility = View.INVISIBLE
                    mBinding.txtInf.text = getString(R.string.click_store_order)
                    buttonFinishAll.isEnabled = true
                }
                mBinding.imageLottie.isVisible = false
            }
        }
        mViewModel.mErrorReceiptShow.observe(this) { messageError ->
            vibrateExtension(500)
            mDialog.alertMessageErrorSimples(this, messageError)
        }
        /**
         * RESPOSTA SUCESSO LEITURAS:
         *  - Aqui resposta UNit então é chamado o primeiro endpoint ao entrar na tela
         *  - para recarregar os dados novamente.
         */
        mViewModel.mSucessReceiptReadingShow.observe(this) {
            clearEdit()
            getApi()
            mSonSucess.somSucess(this)
        }

        mViewModel.mErrorReceiptReadingShow.observe(this) { messageError ->
            clearEdit()
            mProgress.hide()
            mDialog.alertMessageErrorSimples(this, messageError)
        }
        /**---VALIDAD LOGIN ACESSO--->*/
        mViewModel.mSucessReceiptValidLoginShow.observe(this) {
            UIUtil.hideKeyboard(this)
            /**CASO SUCESSO IRA ALTERAR O ICONE E VALIDAR SEM PRECISAR EFETUAR O LOGIN NOVAMENTE--->*/
            mViewModel.callPendenciesOperator()
        }

        /**---VALIDA CHAMADA QUE TRAS OPERADORES COM PENDENCIAS OU SEJA,NO CLICK DO MENU --->*/
        mViewModel.mSucessGetPendenceOperatorShow.observe(this) { listOpPendentes ->
            mProgress.hide()
            try {
                val idOpCorrent = mShared.getString(CustomSharedPreferences.ID_OPERADOR).toString()
                val listSemUserCorrent =
                    listOpPendentes.filter { it.idOperadorColetor.toString() != idOpCorrent }
                val listSerializable = ListReceiptIdOperadorSeriazable(listSemUserCorrent)
                when {
                    /**CASO 1 -> LISTA VAZIA */
                    listOpPendentes.isEmpty() -> {
                        vibrateExtension(500)
                        mDialog.alertMessageAtencao(
                            this,
                            getString(R.string.not_operator_pendenc), 2000
                        )
                    }
                    /**CASO 2 -> LISTA TENHA APENAS UM OPERADOR E FOR IGUAL AO USUARIO */
                    listOpPendentes.size == 1 && listOpPendentes[0].idOperadorColetor.toString() == idOpCorrent -> {
                        vibrateExtension(500)
                        mDialog.alertMessageAtencao(
                            this,
                            getString(R.string.not_operator_pendenc), 2000
                        )
                    }
                    /**CASO 3 -> VARIOS OPERADORES ENTAO PRECISO EXCLUIR O DO PROPIO USER --> */
                    else -> {
                        val intent = Intent(this, FilterSupervisorActivity1::class.java)
                        intent.putExtra("LISTA_USER_PENDENCIAS", listSerializable)
                        startActivity(intent)
                        extensionSendActivityanimation()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "setupObservables: $e")
                Toast.makeText(
                    this,
                    "Erro ao enviar lista operadores com pendências!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mViewModel.mErrorGetPendenceOperatorShow.observe(this) { errorOperador ->
            mProgress.hide()
            Toast.makeText(this, errorOperador, Toast.LENGTH_SHORT).show()
        }
        /**RESPONSTAS AO FINALIZAR TODOS --> **/
        mViewModel.mErrorFinishAllSHow.observe(this) { errorFinishALl ->
            UIUtil.hideKeyboard(this)
            mDialog.alertMessageErrorSimples(this, errorFinishALl)
            mProgressFinish.hide()
        }
        mViewModel.mSucessFinishAllOrderShow.observe(this) {
            mProgressFinish.hide()
            mSonSucess.somSucess(this)
            setupRecyclerView()
            getApi()
        }
    }


    /**--------------------OPTION MENU FILTRAR USUARIO------------------------------------------>*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter_usuario_receipt, menu)
        return true
    }

    //Click Menu -->
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_user -> {
                filterUser()
            }
        }
        return false
    }

    /**CLICK BUTTON FINALIZAR TODOS -->**/
    private fun clickButtonFinish() {
        mBinding.buttonFinishAll.setOnClickListener {
            alertArmazenar()
        }
    }

    private fun alertArmazenar() {
        vibrateExtension(500)
        mSonSucess.somAtencao(this)
        mAlert = AlertDialog.Builder(this).create()
        val mBinding = LayoutCustomFinishAndressBinding.inflate(LayoutInflater.from(this))
        mAlert?.setCancelable(false)
        mAlert?.setView(mBinding.root)
        mBinding.txtCustomAlert.text = "Leia um endereço para finalizar todos os pedidos"
        mBinding.editQrcodeCustom.requestFocus()
        hideKeyExtensionActivity(mBinding.editQrcodeCustom)
        mAlert?.show()
        mBinding.buttonCancelCustom.setOnClickListener {
            mAlert?.dismiss()
        }
    }

    /**---------------------------ALERT DIALOG (FILTRAR POR OPERADOR)---------------------------->*/
    private fun filterUser() {
        CustomMediaSonsMp3().somAtencao(this)
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(this)
        val binding =
            LayoutAlertdialogCustomFiltrarOperadorBinding.inflate(LayoutInflater.from(this))
        mAlert.setCancelable(false)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        binding.editUsuarioFiltrar.requestFocus()
        binding.buttonValidad.setOnClickListener {
            when {
                binding.editUsuarioFiltrar.text.toString().isEmpty() -> {
                    binding.editUsuarioFiltrar.error = "Ops! Digite o Usuario"
                }
                binding.editSenhaFiltrar.text.toString().isEmpty() -> {
                    binding.editSenhaFiltrar.error = "Ops! Digite a Senha!"
                }
                else -> {
                    mProgress.show()
                    mViewModel.postValidLoginAcesss(
                        PosLoginValidadREceipPorduct(
                            usuario = binding.editUsuarioFiltrar.text.toString(),
                            senha = binding.editSenhaFiltrar.text.toString()
                        )
                    )
                    mShow.dismiss()
                    binding.editUsuarioFiltrar.requestFocus()
                }
            }
            mShared.saveString(
                CustomSharedPreferences.NOME_SUPERVISOR_LOGADO,
                binding.editUsuarioFiltrar.text.toString()
            )
        }

        binding.buttonClose.setOnClickListener {
            mShow.dismiss()
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
            Log.e(TAG, "RECEBIDO INTENT QRCODE: $scanData")
            if (mAlert?.isShowing == true) {
                mProgressFinish.show()
                mViewModel.finalizeAllOrders(PostCodScanFinish(scanData.toString()))
                mAlert?.dismiss()
            } else {
                sendData(scanData.toString())
            }
            clearEdit()
        }
    }

    private fun sendData(qrCode: String) {
        if (qrCode.isNotEmpty()) {
            mViewModel.postReadingQrCde(QrCodeReceipt1(codigoBarras = qrCode))
        }
        clearEdit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgressFinish.dismiss()
        mProgress.dismiss()
        unregisterReceiver(receiver)
    }
}
