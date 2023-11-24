package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.activitysFilterSupervisor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityFilterSupervior2Binding
import com.documentos.wms_beirario.model.receiptproduct.ReceiptIdOperadorSeriazable
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters.AdapterReceiptProduct1
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.init.RecebimentoDeProducaoActivity1
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.viewModels.FilterReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class FilterSupervisorActivity2 : AppCompatActivity() {

    private lateinit var mBinding: ActivityFilterSupervior2Binding
    private lateinit var mAdapter: AdapterReceiptProduct1
    private lateinit var mViewModel: FilterReceiptProductViewModel2
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mOperadorSelect: ReceiptIdOperadorSeriazable
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val op =
                    result.data!!.getSerializableExtra("ID_OPERADOR_SELECT") as ReceiptIdOperadorSeriazable
                mOperadorSelect = op
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFilterSupervior2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        getOperadorIntent()
        setupToolbar()
        setRecyclerView()
        setupObervable()
        setupTxtInformation()
//        callApiPendenceOperator()
    }

    override fun onResume() {
        super.onResume()
        callApiPendenceOperator()
    }

    private fun getOperadorIntent() {
        try {
            if (intent.extras != null) {
                val op =
                    intent.extras!!.getSerializable("OPERADOR_SELECT") as ReceiptIdOperadorSeriazable
                mOperadorSelect = op
            }
        } catch (e: Exception) {
            toastError(this, "Erro ao receber operador")
        }
    }

    private fun setupToolbar() {
        /**INIT VIEW MODEL -->*/
        mViewModel = ViewModelProvider(
            this, FilterReceiptProductViewModel2.ReceiptProductViewModel1Factory2(
                ReceiptProductRepository()
            )
        )[FilterReceiptProductViewModel2::class.java]

        val getNameSupervisor =
            mSharedPreferences.getString(CustomSharedPreferences.NOME_SUPERVISOR_LOGADO)
        mBinding.toolbarSetOperator.subtitle =
            getString(R.string.supervisor_name, getNameSupervisor)
        mBinding.toolbarSetOperator.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**SET ADAPTER E CLICK NO ITEM PENDENTE PARA FINALIZAR--->*/
    private fun setRecyclerView() {
        mAdapter = AdapterReceiptProduct1 { itemClick ->
            val intent = Intent(this, FilterSupervisorActivity3::class.java)
            intent.putExtra("ID_OPERADOR_SELECT", mOperadorSelect)
            intent.putExtra("PRODUTO_CLICADO_PARA_FINALIZAR", itemClick)
            mResponseBack.launch(intent)
            extensionSendActivityanimation()
        }
        mBinding.rvOperator1.apply {
            layoutManager = LinearLayoutManager(this@FilterSupervisorActivity2)
            adapter = mAdapter
        }
    }

    /**CHAMADA API--->*/
    private fun callApiPendenceOperator() {
        val idOperador = mOperadorSelect.idOperadorColetor.toString()
        mViewModel.getReceipt1(filtrarOperador = true, mIdOperador = idOperador)
    }

    private fun setupTxtInformation() {
        mBinding.txtInfOperator22.text =
            getString(R.string.Pendence_operator, mOperadorSelect.usuario)
    }

    private fun setupObervable() {
        mViewModel.mSucessReceiptShow.observe(this) { listSucess ->
            mAdapter.update(listSucess)
        }
        mViewModel.mErrorReceiptShow.observe(this) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError)
        }
        mViewModel.mValidaProgressReceiptShow.observe(this) { validProgress ->
            if (validProgress) mBinding.progressOperatorfilter2.visibility = View.VISIBLE
            else mBinding.progressOperatorfilter2.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tela inicial recebimento", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, RecebimentoDeProducaoActivity1::class.java))
        extensionBackActivityanimation(this)
    }

}