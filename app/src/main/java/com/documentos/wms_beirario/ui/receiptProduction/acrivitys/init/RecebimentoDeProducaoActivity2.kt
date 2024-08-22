package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.init

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRecebimentoDeProducao2Binding
import com.documentos.wms_beirario.model.receiptproduct.ListFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.PostFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct2
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters.AdapterReceiptProduct2
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.viewModels.ReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*

class RecebimentoDeProducaoActivity2 : AppCompatActivity() {

    private lateinit var mBinding: ActivityRecebimentoDeProducao2Binding
    val TAG = "ReceiptProductFragment2"
    private lateinit var mAdapter: AdapterReceiptProduct2
    private var mIdTarefa: String = ""
    private lateinit var mListItensValid: List<ReceiptProduct2>
    private var mListItensFinish = mutableListOf<ListFinishReceiptProduct3>()
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var viewModel: ReceiptProductViewModel2
    private lateinit var progressDialog: Dialog
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mItemClicqueReb1: ReceiptProduct1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecebimentoDeProducao2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)

        getIntentItem()
        clickButton()
        initConst()
        setRecyclerView()
        setupToolbar()
        callApi()
        setObservables()
    }

    private fun getIntentItem() {
        try {
            if (intent.extras != null){
                val item = intent.extras?.getSerializable("ITEM_CLICADO") as ReceiptProduct1
                mItemClicqueReb1 = item
                Log.e(TAG, "RECEBEU DE RECEBIMENTO 1 -> ${mItemClicqueReb1.pedido}")
            }
        }catch (e:Exception){
            toastError(this, "Erro ao receber item!")
        }
    }


    private fun initConst() {
        viewModel = ViewModelProvider(
            this, ReceiptProductViewModel2.ReceiptProductViewModel1Factory2(
                ReceiptProductRepository()
            )
        )[ReceiptProductViewModel2::class.java]

        mDialog = CustomAlertDialogCustom()
        progressDialog = CustomAlertDialogCustom().progress(this)
        mSharedPreferences = CustomSharedPreferences(this)
    }

    override fun onResume() {
        super.onResume()
        progressDialog.hide()
    }


    private fun setRecyclerView() {
        mAdapter = AdapterReceiptProduct2()
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecebimentoDeProducaoActivity2)
            adapter = mAdapter
        }
    }

    private fun clickButton() {
        mBinding.buttonFinishReceipt2.setOnClickListener {
            alertArmazenar()
        }
    }


    private fun callApi() {
        val idOperador = mSharedPreferences.getString(CustomSharedPreferences.ID_OPERADOR)
        Log.e(TAG, "callApi --> ${mItemClicqueReb1.pedido} + $idOperador")
        viewModel.getItem(
            idOperador = idOperador ?: "0",
            filtrarOperario = true,
            pedido = mItemClicqueReb1.pedido
        )
    }

    /**VALIDA SE O USUARIO FOI LOGADO RETORNA TRUE OU FALSE NO ARGUMENTO -->*/
    private fun setupToolbar() {
        mBinding.toolbar2.apply {
            subtitle = getVersionNameToolbar()
            this.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        mBinding.txtInf.text = getString(R.string.order_receipt2_toolbar, mItemClicqueReb1.pedido)
    }

    private fun setObservables() {
        /**GET ITENS / VALIDA SE A LISTA FOR VAZIA BUTTON FINISH INATIVO-->*/
        viewModel.mSucessReceiptShow2.observe(this) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding.buttonFinishReceipt2.isEnabled = false
                mBinding.txtInf.isVisible = false
            } else {
                mBinding.txtInf.isVisible = true
                listSucess.forEach { itens ->
                    mListItensFinish.add(
                        ListFinishReceiptProduct3(
                            itens.numeroSerie,
                            itens.sequencial
                        )
                    )
                }
                mListItensValid = listSucess
                mIdTarefa = listSucess[0].idTarefa
                mAdapter.submitList(listSucess)
            }
        }
        viewModel.mErrorReceiptShow2.observe(this) { messageError ->
            progressDialog.hide()
            vibrateExtension(500)
            mBinding.txtInf.isVisible = true
            mBinding.txtInf.text = messageError
            mDialog.alertMessageErrorSimples(this, messageError, 2000)
        }
        viewModel.mValidaProgressReceiptShow2.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        /**--------READING FINISH---------------->*/
        viewModel.mSucessFinishShow.observe(this) {
            CustomMediaSonsMp3().somSucess(this)
            progressDialog.hide()
            callApi()
            setRecyclerView()
            //Valida se todos itens forem armazenados o button fica inativo -->
            mBinding.buttonFinishReceipt2.isEnabled = mListItensValid.isNotEmpty()
            CustomSnackBarCustom().snackBarSucess(
                this,
                mBinding.root,
                "${mListItensValid.size} itens finalizados!"
            )
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, RecebimentoDeProducaoActivity1::class.java))
                extensionBackActivityanimation(this)
            }, 1500)


        }
        viewModel.mErrorFinishShow.observe(this) { messageError ->
            progressDialog.hide()
            mDialog.alertMessageErrorSimplesAction(
                this,
                message = messageError,
                action = {
                    alertArmazenar()
                })
        }
    }

    /**-----------------------ALERT CAIXA PARA FINALIZAR LEITURA ENDEREÇO------------------------>*/
    private fun alertArmazenar() {
        mDialog.alertReadingAction(
            context = this,
            actionBipagem = { qrCode ->
                viewModel.postFinishReceipt(
                    PostFinishReceiptProduct3(
                        codigoBarrasEndereco = qrCode,
                        itens = mListItensFinish,
                        idTarefa = mIdTarefa
                    )
                )
                progressDialog.show()
            },
            actionCancel = {
                progressDialog.hide()
            },
            tittle = "Área destino: ${mItemClicqueReb1.areaDestino}"
        )
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }

}