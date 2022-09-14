package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.activitysFilterSupervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityFilterSupervisor1Binding
import com.documentos.wms_beirario.model.receiptproduct.ListReceiptIdOperadorSeriazable
import com.documentos.wms_beirario.model.receiptproduct.ReceiptIdOperadorSeriazable
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters.AdapterFilterReceiptProduct
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation

class FilterSupervisorActivity1 : AppCompatActivity() {

    private val TAG = "FilterSupervisorActivity1"
    private lateinit var mBinding: ActivityFilterSupervisor1Binding
    private lateinit var mAdapter: AdapterFilterReceiptProduct
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var listOperadores: MutableList<ReceiptIdOperadorSeriazable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFilterSupervisor1Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        getIntentBack()
        setupRecyclerView()
        setupToolbar()
    }

    private fun getIntentBack() {
        try {
            if (intent.extras != null) {
                val test =
                    intent.extras?.getSerializable("LISTA_USER_PENDENCIAS") as ListReceiptIdOperadorSeriazable
                listOperadores = mutableListOf()
                test.list.forEach {
                    listOperadores.add(it)
                }
                Log.e(TAG, "getIntentBack: $listOperadores ")
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "ERRO AO RECEBER LISTA DE USUÁRIOS COM PENDÊNCIAS",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * CLICK NO OPERADOR SELECIONADO -->
     */
    private fun setupRecyclerView() {
        mAdapter = AdapterFilterReceiptProduct { operatorClick ->
            val intent = Intent(this, FilterSupervisorActivity2::class.java)
            intent.putExtra("OPERADOR_SELECT", operatorClick)
            startActivity(intent)
            extensionSendActivityanimation()
        }
        mBinding.rvOperator1.apply {
            layoutManager = LinearLayoutManager(this@FilterSupervisorActivity1)
            adapter = mAdapter
        }
        mAdapter.update(listOperadores)
    }

    private fun setupToolbar() {
        /**SET NAME LOGADO DO SUPERVISOR --->*/
        mSharedPreferences = CustomSharedPreferences(this)
        val getNameSupervisor =
            mSharedPreferences.getString(CustomSharedPreferences.NOME_SUPERVISOR_LOGADO)
        mBinding.toolbarSetOperator.subtitle =
            getString(R.string.supervisor_name, getNameSupervisor)
        mBinding.toolbarSetOperator.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}