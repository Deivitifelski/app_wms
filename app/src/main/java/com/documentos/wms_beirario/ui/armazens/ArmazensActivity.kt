package com.documentos.wms_beirario.ui.armazens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ArmazensActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityArmazensBinding
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var mAdapter: ArmazemAdapter? = ArmazemAdapter { }
    private lateinit var viewModel: ArmazemViewModel
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var token: String
    private var mValidaSend: Boolean = false
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getBooleanExtra("RETURT_DATA", false)
                if (data == true) {
                    mValidaSend = data
                    initData()
                    initRecyclerView()
                    observables()
                    Log.e("TAG", "RECEBENDO DADOS TIPO TAREFA --> $mValidaSend")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazensBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        token = mSharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
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
        viewModel = ViewModelProvider(
            this,
            ArmazemViewModel.ArmazensViewModelFactory(ArmazensRepository())
        )[ArmazemViewModel::class.java]
    }

    private fun initData() {
        viewModel.getArmazens(token)
    }

    private fun observables() {
        viewModel.mSucessShow.observe(this) { responseArmazens ->
            when {
                responseArmazens.isEmpty() -> {
                    alertDefaulError(
                        this,
                        message = "Não foi encontrado armazens para serem listados.",
                        onClick = {
                            onBackPressed()
                        })
                }
                responseArmazens.size == 1 && !mValidaSend -> {
                    enviarparaTipoTarefa(responseArmazens[0])
                    Log.e("ARMAZENS", "observables --> $mValidaSend")
                }
                else -> {
                    initClickStartActivity()
                    mBinding.rvArmazem.apply {
                        this.layoutManager = LinearLayoutManager(this@ArmazensActivity)
                        this.adapter = mAdapter
                    }
                    mAdapter?.update(responseArmazens)
                }
            }
        }
        viewModel.mErrorHttpShow.observe(this) { response ->
            mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }

        viewModel.mProgressShow.observe(this) { progress ->
            if (progress)
                mBinding.progressBarInitArmazens.visibility = View.VISIBLE
            else mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
        }
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
        intent.putExtra("A_WAREHOUSE", true)
        mResponseBack.launch(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun initToolbar() {
        val tipoBanco = mSharedPreferences.getString("TIPO_BANCO")
        mBinding.txtTipobanco.text = tipoBanco
        mToast = CustomSnackBarCustom()
        mBinding.toolbarArmazem.apply {
            subtitle = "Selecione o armazém"
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
            Log.e("ARMAZEM", "DECODE: $mDecode")
            Log.e("ID_OPERADOR -->", mDecodeTokenOk.toString())
        } catch (e: Exception) {
            vibrateExtension(500)
            toastError(this, "Erro ao receber (COD.OPERADOR)/${e.cause}")
        }
    }

    private fun initRecyclerView() {
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