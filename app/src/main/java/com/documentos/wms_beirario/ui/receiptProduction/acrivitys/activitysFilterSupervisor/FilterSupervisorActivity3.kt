package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.activitysFilterSupervisor

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityFilterSupervisor3Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishAndressBinding
import com.documentos.wms_beirario.model.receiptproduct.*
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters.AdapterReceiptProduct2
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.init.RecebimentoDeProducaoActivity1
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.viewModels.ReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*

class FilterSupervisorActivity3 : AppCompatActivity() {

    private lateinit var mBinding: ActivityFilterSupervisor3Binding
    private lateinit var mAdapter: AdapterReceiptProduct2
    private var mIdTarefa: String = ""
    private lateinit var mListItensValid: List<ReceiptProduct2>
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mViewModel: ReceiptProductViewModel2
    private lateinit var mDialog: Dialog
    private var mListItensFinish = mutableListOf<ListFinishReceiptProduct3>()
    private lateinit var mIdOPerador: ReceiptIdOperadorSeriazable
    private lateinit var mItemCliqueTask: ReceiptProduct1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFilterSupervisor3Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        mDialog = CustomAlertDialogCustom().progress(this)
        getIntentItens()
        clickButton()
        setRecyclerView()
        setupToolbar()
        callApi()
        setObservables()
    }

    private fun getIntentItens() {
        try {
            if (intent.extras != null) {
                val mIdOp =
                    intent.extras!!.getSerializable("ID_OPERADOR_SELECT") as ReceiptIdOperadorSeriazable
                val mClick =
                    intent.extras!!.getSerializable("PRODUTO_CLICADO_PARA_FINALIZAR") as ReceiptProduct1
                mIdOPerador = mIdOp
                mItemCliqueTask = mClick
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados!")
        }
    }

    override fun onResume() {
        super.onResume()
        mDialog.hide()
    }

    private fun setRecyclerView() {
        mAdapter = AdapterReceiptProduct2()
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FilterSupervisorActivity3)
            adapter = mAdapter
        }
    }

    private fun clickButton() {
        mBinding.buttonFinishReceipt2.setOnClickListener {
            alertArmazenar()
        }

        mBinding.buttonBackFrag1Receipt.setOnClickListener {
            Toast.makeText(this, "Tela inicial recebimento", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RecebimentoDeProducaoActivity1::class.java))
            extensionBackActivityanimation(this)
        }
    }

    private fun callApi() {
        mViewModel.getItem(
            idOperador = mIdOPerador.idOperadorColetor.toString(),
            filtrarOperario = true,
            pedido = mItemCliqueTask.pedido
        )
    }

    /**RETORNAR AO FRAGMENTO FILTER 2 COM OS ITENS PARA ELE SER RECRIADO -->*/
    private fun setupToolbar() {
        mViewModel = ViewModelProvider(
            this, ReceiptProductViewModel2.ReceiptProductViewModel1Factory2(
                ReceiptProductRepository()
            )
        )[ReceiptProductViewModel2::class.java]
        val getNameSupervisor =
            mSharedPreferences.getString(CustomSharedPreferences.NOME_SUPERVISOR_LOGADO)
        mBinding.toolbar2.subtitle = getString(R.string.supervisor_name, getNameSupervisor)
        mBinding.toolbar2.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
        mBinding.txtInf.text =
            getString(R.string.order_receipt2_toolbar, mItemCliqueTask.pedido)
    }

    private fun setObservables() {
        /**--------GET ITENS---------------->*/
        mViewModel.mSucessReceiptShow2.observe(this) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding.buttonFinishReceipt2.isEnabled = false
            } else {
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
            mDialog.hide()
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError)
        }
        mViewModel.mValidaProgressReceiptShow2.observe(this) { validProgress ->
            if (validProgress) AppExtensions.visibilityProgressBar(mBinding.progress)
            else AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
        }
        /**--------READING FINISH---------------->*/
        mViewModel.mSucessFinishShow.observe(this) {
            CustomMediaSonsMp3().somSucessReading(this)
            mDialog.hide()
            callApi()
            setRecyclerView()
            //Valida se todos itens forem armazenados o button fica inativo -->
            mBinding.buttonFinishReceipt2.isEnabled = mListItensValid.isNotEmpty()
            CustomSnackBarCustom().snackBarSucess(
                this,
                mBinding.root,
                "${mListItensValid.size} itens Armazenados!"
            )
        }
        mViewModel.mErrorFinishShow.observe(this) { messageError ->
            mDialog.hide()
            vibrateExtension(500)
            alertMessageErrorSimples(this, messageError)
        }
    }

    /**-----------------------ALERT CAIXA PARA FINALIZAR LEITURA ENDEREÇO------------------------>*/
    private fun alertArmazenar() {
        AppExtensions.vibrar(this)
        CustomMediaSonsMp3().somAtencao(this)
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(this)
        val mBinding =
            LayoutCustomFinishAndressBinding.inflate(LayoutInflater.from(this))
        mAlert.setCancelable(false)
        mAlert.setView(mBinding.root)
        hideKeyExtensionActivity(mBinding.editQrcodeCustom)
        mBinding.txtCustomAlert.text = mItemCliqueTask.areaDestino
        mBinding.editQrcodeCustom.requestFocus()
        val showDialog = mAlert.create()
        showDialog.show()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBinding.editQrcodeCustom.addTextChangedListener { qrCode ->
            if (qrCode!!.isNotEmpty()) {
                mDialog.show()
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
            mDialog.hide()
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
        val intent = Intent()
        intent.putExtra("ID_OPERADOR_SELECT", mIdOPerador)
        intent.putExtra("PRODUTO_CLICADO_PARA_FINALIZAR", mItemCliqueTask)
        setResult(RESULT_OK, intent)
        finish()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog.dismiss()
    }
}