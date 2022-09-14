package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.init

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRecebimentoDeProducao2Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishAndressBinding
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
    private lateinit var mViewModel: ReceiptProductViewModel2
    private lateinit var mProgress: Dialog
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
            mErroToastExtension(this,"Erro ao receber item!")
        }
    }


    private fun initConst() {
        mViewModel = ViewModelProvider(
            this, ReceiptProductViewModel2.ReceiptProductViewModel1Factory2(
                ReceiptProductRepository()
            )
        )[ReceiptProductViewModel2::class.java]

        mDialog = CustomAlertDialogCustom()
        mProgress = CustomAlertDialogCustom().progress(this)
        mSharedPreferences = CustomSharedPreferences(this)
    }

    override fun onResume() {
        super.onResume()
        mProgress.hide()
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
        mViewModel.getItem(
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
        mViewModel.mSucessReceiptShow2.observe(this) { listSucess ->
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
        mViewModel.mErrorReceiptShow2.observe(this) { messageError ->
            mProgress.hide()
            vibrateExtension(500)
            mBinding.txtInf.isVisible = true
            mBinding.txtInf.text = messageError
            mDialog.alertMessageErrorSimples(this, messageError, 2000)
        }
        mViewModel.mValidaProgressReceiptShow2.observe(this) { validProgress ->
            mBinding.progress.isVisible = validProgress
        }
        /**--------READING FINISH---------------->*/
        mViewModel.mSucessFinishShow.observe(this) {
            CustomMediaSonsMp3().somSucess(this)
            mProgress.hide()
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
        mViewModel.mErrorFinishShow.observe(this) { messageError ->
            mProgress.hide()
            vibrateExtension(500)
            alertMessageErrorSimples(this, messageError)
        }
    }

    /**-----------------------ALERT CAIXA PARA FINALIZAR LEITURA ENDEREÇO------------------------>*/
    private fun alertArmazenar() {
        vibrateExtension(500)
        CustomMediaSonsMp3().somAtencao(this)
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(this)
        val mBinding =
            LayoutCustomFinishAndressBinding.inflate(LayoutInflater.from(this))
        mAlert.setCancelable(false)
        mAlert.setView(mBinding.root)
        hideKeyExtensionActivity(mBinding.editQrcodeCustom)
        mBinding.txtCustomAlert.text = "Área destino: ${mItemClicqueReb1.areaDestino}"
        mBinding.editQrcodeCustom.requestFocus()
        val showDialog = mAlert.create()
        showDialog.show()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBinding.editQrcodeCustom.addTextChangedListener { qrCode ->
            if (qrCode!!.isNotEmpty()) {
                mProgress.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    mViewModel.postFinishReceipt(
                        PostFinishReceiptProduct3(
                            codigoBarrasEndereco = qrCode.toString(),
                            itens = mListItensFinish,
                            idTarefa = mIdTarefa
                        )
                    )
                }, 600)
                showDialog.dismiss()

                mBinding.editQrcodeCustom.setText("")
                mBinding.editQrcodeCustom.requestFocus()
            }
        }
        mBinding.buttonCancelCustom.setOnClickListener {
            mProgress.hide()
            showDialog.dismiss()
        }
    }

    /**-----------------------ALERT ERRO CUSTOMIZADO NO BUTTON OK-------------------------------->*/
    fun alertMessageErrorSimples(context: Context, message: String) {
        CustomMediaSonsMp3().somError(context)
        val mAlert = AlertDialog.Builder(context)
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.show()
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_error)
        medit.addTextChangedListener {
            if (it.toString() != "") {
                alertArmazenar()
                mShow.dismiss()
            }
        }
        val mText = inflate.findViewById<TextView>(R.id.txt_message_atencao)
        val mButton = inflate.findViewById<Button>(R.id.button_atencao_layout_custom)
        mText.text = message
        mButton.setOnClickListener {
            alertArmazenar()
            mShow.dismiss()
        }
        mAlert.create()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
    }

}