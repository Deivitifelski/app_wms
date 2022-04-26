package com.documentos.wms_beirario.ui.armazens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityArmazensBinding
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity.Companion.mValidaAcess
import com.documentos.wms_beirario.ui.armazens.adapter.AdapterArmazens
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.example.coletorwms.constants.CustomSnackBarCustom
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class ArmazensActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityArmazensBinding
    private val mViewModel: ArmazensViewModel by viewModel()
    private lateinit var mAdapter: AdapterArmazens
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazensBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        initToolbar()
        lifecycleScope.launch {
            initDecodeToken()
        }
        mViewModel.getArmazens()
        responseObservable()
    }

    private fun initToolbar() {
        val nameUser = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER)?.uppercase()
        mBinding.toolbarArmazem.apply {
            subtitle = "$nameUser selecione o armazém"
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun initClickStartActivity() {
        mAdapter = AdapterArmazens { responseArmazens ->
            ServiceApi.IDARMAZEM = responseArmazens.id
            CustomSnackBarCustom().toastCustomSucess(this, "Armazem: ${responseArmazens.id}")
            mSharedPreferences.saveInt(CustomSharedPreferences.ID_ARMAZEM, responseArmazens.id)
            val intent = Intent(this, TipoTarefaActivity::class.java)
            intent.putExtra("NAME_ARMAZEM", responseArmazens.nome)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun responseObservable() {
        mViewModel.mShowSucess.observe(this, { responseArmazens ->
            AppExtensions.visibilityProgressBar(mBinding.progressBarInitArmazens, false)

            if (responseArmazens.isEmpty()) {
                Toast.makeText(this, "Lista Vazia", Toast.LENGTH_SHORT).show()
            } else if (responseArmazens.size == 1 && !mValidaAcess) {
                enviarparaTipoTarefa(responseArmazens[0])
            } else {
                initClickStartActivity()
                mBinding.rvArmazem.apply {
                    this.layoutManager = LinearLayoutManager(this@ArmazensActivity)
                    this.adapter = mAdapter
                }
                mAdapter.update(responseArmazens)
            }
        })
        mViewModel.mShowErrorUser.observe(this) { response ->
            AppExtensions.visibilityProgressBar(mBinding.progressBarInitArmazens, false)
            mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initDecodeToken() {
        val mDecode = DecodeToken().decodeToken(ServiceApi.TOKEN)
        val test = JSONObject(mDecode).getString("operador")
        val mDecodeTokenOk = JSONObject(test).getString("id")
        /** SALVANDO ID_OPERADOR */
        mSharedPreferences.saveString(CustomSharedPreferences.ID_OPERADOR, mDecodeTokenOk)
        Log.e("ID_OPERADOR -->", mDecodeTokenOk.toString())
    }

    private fun enviarparaTipoTarefa(armazensResponse: ArmazensResponse) {
        CustomSnackBarCustom().toastCustomSucess(this, "Armazem: ${armazensResponse.id}")
        mSharedPreferences.saveInt(CustomSharedPreferences.ID_ARMAZEM, armazensResponse.id)
        ServiceApi.IDARMAZEM = armazensResponse.id
        val intent = Intent(this, TipoTarefaActivity::class.java)
        intent.putExtra("NAME_ARMAZEM", armazensResponse.nome)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
    }
}