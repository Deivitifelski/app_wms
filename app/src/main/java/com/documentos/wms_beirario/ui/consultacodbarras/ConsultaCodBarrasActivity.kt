package com.documentos.wms_beirario.ui.consultacodbarras

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityConsultaCodBarrasBinding
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.repository.consultacodbarras.ConsultaCodBarrasRepository
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.EnderecoFragment
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.ProdutoFragment
import com.documentos.wms_beirario.ui.consultacodbarras.fragments.VolumeFragment
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class ConsultaCodBarrasActivity : AppCompatActivity() {

    private lateinit var mViewModel: ConsultaCodBarrasViewModel
    private var mRetrofitService = ServiceApi.getInstance()
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var mIdArmazem: Int = 0
    private lateinit var mToken: String
    private lateinit var mBinding: ActivityConsultaCodBarrasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityConsultaCodBarrasBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mSharedPreferences = CustomSharedPreferences(this)
        mViewModel = ViewModelProvider(
            this,
            ConsultaCodBarrasViewModel.ConsultaCodBarrasViewModelFactory(
                ConsultaCodBarrasRepository(
                    mRetrofitService
                )
            )
        )[ConsultaCodBarrasViewModel::class.java]

    }

    override fun onResume() {
        super.onResume()
        UIUtil.hideKeyboard(this)
        mViewModel.visibilityProgress(mBinding.progress, visibility = false)
        initToolbar()
        getShared()
        initData()
        setupObservables()

    }

    private fun initToolbar() {
        mBinding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }


    private fun getShared() {
        mToken = mSharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        mIdArmazem = mSharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
    }


    private fun initData() {
        mBinding.editCodBarras.requestFocus()
        mBinding.editCodBarras.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            val barcode = mBinding.editCodBarras.text.toString()
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP){
                if (barcode.isNotEmpty()) {
                    UIUtil.hideKeyboard(this)
                    AppExtensions.visibilityProgressBar(mBinding.progress, visibility = true)
                    mViewModel.getCodBarras(codigoBarras = barcode)
                    editFocus()
                }
                return@OnKeyListener true
            }
            false
        })
    }

    private fun editFocus() {
        mBinding.apply {
            editCodBarras.requestFocus()
            editCodBarras.setText("")
        }
    }

    private fun setupObservables() {
        mViewModel.mSucessShow.observe(this, { mDados ->
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
            mViewModel.checkBarCode(mDados)
        })

        mViewModel.mErrorShow.observe(this, { mErrorCodBarras ->
            mViewModel.visibilityProgress(mBinding.progress, visibility = false)
            CustomAlertDialogCustom().alertMessageErrorSimples(this, mErrorCodBarras)
        })
        //todo RESPONSE DAS VALIDAÇOES DA LEITURA ->
        mViewModel.mResponseCheckEndereco.observe(this, { endereco ->
            val bundle = bundleOf("ENDERECO" to endereco)
            supportFragmentManager.commit {
                replace<EnderecoFragment>(R.id.fragment_container_view_cod_barras, args = bundle)
                setReorderingAllowed(true)
                /**addToBackStack ele estando acionado ele faz o desligamento da instancia do fragment*/
//                addToBackStack(null)
            }
        })
        mViewModel.mResponseCheckProduto.observe(this, { produto ->
            val bundle = bundleOf("PRODUTO" to produto)
            supportFragmentManager.commit {
                replace<ProdutoFragment>(R.id.fragment_container_view_cod_barras, args = bundle)
                setReorderingAllowed(true)
            }
        })
        mViewModel.mResponseCheckVolume.observe(this, { volume ->
            val bundle = bundleOf("VOLUME" to volume)
            supportFragmentManager.commit {
                replace<VolumeFragment>(R.id.fragment_container_view_cod_barras, args = bundle)
                setReorderingAllowed(true)
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }

}


