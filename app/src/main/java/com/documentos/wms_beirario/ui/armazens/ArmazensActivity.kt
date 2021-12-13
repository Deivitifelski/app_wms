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
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.model.armazens.ArmazensResponse
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import com.documentos.wms_beirario.ui.Tarefas.TipoTarefaActivity
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.armazens.adapter.AdapterArmazens
import com.documentos.wms_beirario.ui.login.LoginActivity
import com.example.coletorwms.constants.CustomSnackBarCustom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ArmazensActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityArmazensBinding
    private lateinit var mViewModel: ArmazensViewModel
    private lateinit var mAdapter: AdapterArmazens
    private var retrofitService = ServiceApi.getInstance()
    private var mTest: Boolean = false
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazensBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        mViewModel =
            ViewModelProvider(
                this,
                ArmazensViewModel.ArmazensViewModelFactory(ArmazensRepository(retrofitService))
            )[ArmazensViewModel::class.java]
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        responseObservable()
//        lifecycleScope.launch(Dispatchers.Default) {
//            initDecodeToken()
//        }
    }

    override fun onRestart() {
        super.onRestart()
        mTest = true
    }

    private fun initToolbar() {
        val toolbar = mBinding.toolbarArmazem
        toolbar.setNavigationOnClickListener {
            extensionStarBacktActivity(LoginActivity())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
       extensionStarBacktActivity(LoginActivity())
    }

    private fun initClickStartActivity() {
        mAdapter = AdapterArmazens { responseArmazens ->
            ServiceApi.IDARMAZEM = responseArmazens.id
            CustomSnackBarCustom().toastCustomSucess(this, "Armazem: ${responseArmazens.id}")
            startActivity(Intent(this, TipoTarefaActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun responseObservable() {
        mViewModel.getArmazens()
        mViewModel.mShowSucess.observe(this, { responseArmazens ->
            AppExtensions.visibilityProgressBar(mBinding.progressBarInitArmazens, false)

            if (responseArmazens.isEmpty()) {
                Toast.makeText(this, "Lista Vazia", Toast.LENGTH_SHORT).show()
            } else if (responseArmazens.size == 1 && !mTest) {
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
        ServiceApi.IDARMAZEM = armazensResponse.id
        CustomSnackBarCustom().toastCustomSucess(this, "Armazem: ${armazensResponse.id}")
        startActivity(Intent(this, TipoTarefaActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}