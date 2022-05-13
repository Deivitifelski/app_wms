package com.documentos.wms_beirario.ui.armazens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityArmazensBinding
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import com.documentos.wms_beirario.ui.armazens.adapter.ArmazemAdapter
import com.documentos.wms_beirario.ui.tipoTarefa.TipoTarefaActivity
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ArmazensActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityArmazensBinding
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mAdapter: ArmazemAdapter
    private lateinit var mViewModel: ArmazemViewModel
    private lateinit var mToast: CustomSnackBarCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazensBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        initRecyclerView()
        initToolbar()
        initViewModel()
        observables()
        initData()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Default) {
            initToken()
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ArmazemViewModel.ArmazensViewModelFactory(ArmazensRepository())
        )[ArmazemViewModel::class.java]
    }

    private fun initData() {
        mViewModel.getArmazens()
    }

    private fun observables() {
        mViewModel.mSucessShow.observe(this, { responseArmazens ->
            when {
                responseArmazens.isEmpty() -> {
                    mToast.snackBarPadraoSimplesBlack(mBinding.root, getString(R.string.list_emply))
                }
                responseArmazens.size == 1 -> {
                    enviarparaTipoTarefa(responseArmazens[0])
                }
                else -> {
                    initClickStartActivity()
                    mBinding.rvArmazem.apply {
                        this.layoutManager = LinearLayoutManager(this@ArmazensActivity)
                        this.adapter = mAdapter
                    }
                    mAdapter.update(responseArmazens)
                }
            }
        })
        mViewModel.mErrorHttpShow.observe(this) { response ->
            mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }

        mViewModel.mProgressShow.observe(this, { progress ->
            if (progress)
                mBinding.progressBarInitArmazens.visibility = View.VISIBLE
            else mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
        })
    }


    private fun initClickStartActivity() {
        mAdapter = ArmazemAdapter { responseArmazens ->
            ServiceApi.IDARMAZEM = responseArmazens.id
            CustomSnackBarCustom().toastCustomSucess(this, "Armazem: ${responseArmazens.id}")
            mSharedPreferences.saveInt(CustomSharedPreferences.ID_ARMAZEM, responseArmazens.id)
            val intent = Intent(this, TipoTarefaActivity::class.java)
            intent.putExtra("NAME_ARMAZEM", responseArmazens.nome)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
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

    private fun initToolbar() {
        val tipoBanco = mSharedPreferences.getString("TIPO_BANCO")
        mBinding.txtTipobanco.text = tipoBanco
        mToast = CustomSnackBarCustom()
        mBinding.toolbarArmazem.apply {
            subtitle = "Selecione o armazém [${getVersion()}]"
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initToken() {
        try {
            val mDecode = DecodeToken().decodeToken(ServiceApi.TOKEN)
            val test = JSONObject(mDecode).getString("operador")
            val mDecodeTokenOk = JSONObject(test).getString("id")
            /** SALVANDO ID_OPERADOR */
            mSharedPreferences.saveString(CustomSharedPreferences.ID_OPERADOR, mDecodeTokenOk)
            Log.e("ID_OPERADOR -->", mDecodeTokenOk.toString())
        } catch (e: Exception) {
            vibrateExtension(500)
            CustomSnackBarCustom().toastCustomError(
                this,
                "Erro ao receber (COD.OPERADOR)/${e.cause}"
            )
        }
    }


    private fun initRecyclerView() {
        /**-------------------RESGATANDO NOME E COLOCANDO NA TOOLBAR------------------------------*/
        val mNameDigitado = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER)
        val mNameReplace = mNameDigitado?.replace("_", " ")
        mBinding.txtNameUsuario.text = mNameReplace
        /** ONCLICK ADAPTER -->*/
        mAdapter = ArmazemAdapter {

        }

        mBinding.rvArmazem.apply {
            this.layoutManager = LinearLayoutManager(this@ArmazensActivity)
            this.adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


}