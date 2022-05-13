package com.documentos.wms_beirario.ui.inventory.activitys.init

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityInventario1Binding
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventario1
import com.documentos.wms_beirario.ui.inventory.viewModel.PendingTaskInventoryViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class InventarioActivity1 : AppCompatActivity() {

    private lateinit var mAdapter: AdapterInventario1
    private lateinit var mBinding: ActivityInventario1Binding
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mViewModel: PendingTaskInventoryViewModel1
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityInventario1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        setupToolbar()
        initViewModel()
        setupObservables()
        initConst()
        setupRecyclerView()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PendingTaskInventoryViewModel1.PendingTaskFragiewModelFactory(InventoryoRepository1())
        )[PendingTaskInventoryViewModel1::class.java]
    }

    override fun onResume() {
        super.onResume()
        callApi()
    }

    private fun initConst() {
        mBinding.progressBarInventario.isVisible = true
        mBinding.txtInfo.isVisible = false
        mBinding.lottie.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupToolbar() {
        mBinding.toolbarInventario.apply {
            subtitle = "[${getVersion()}]"
            setNavigationOnClickListener {
                finish()
                extensionBackActivityanimation(this@InventarioActivity1)
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            finish()
            extensionBackActivityanimation(this@InventarioActivity1)
        }
    }

    private fun callApi() {
        /**CHAMADA A API COM DELAY DE 200MILLIS -->*/
        Handler(Looper.getMainLooper()).postDelayed({ mViewModel.getPending1() }, 200)
    }

    private fun setupRecyclerView() {
        /**CLIQUE EM UM ITEM--> */
        mAdapter = AdapterInventario1 { clickAdapter ->

            mSharedPreferences.saveInt(CustomSharedPreferences.ID_INVENTORY, clickAdapter.id)
            val intent = Intent(this, InventoryActivity2::class.java)
            intent.putExtra("DATA_ACTIVITY_1", clickAdapter)
            startActivity(intent)
            extensionSendActivityanimation()
        }
        mBinding.rvInventario1.apply {
            layoutManager = LinearLayoutManager(this@InventarioActivity1)
            adapter = mAdapter
        }
    }

    private fun setupObservables() {
        /**LISTA VAZIA -->*/
        mViewModel.mValidadTxtShow.observe(this, { validadTxt ->
            mBinding.txtInfo.isVisible = true
            if (validadTxt) {
                mBinding.txtInfo.text = getString(R.string.denied_information)
            } else {
                mBinding.txtInfo.text = getString(R.string.click_select_item)
            }

        })
        /**LISTA COM ITENS -->*/
        mViewModel.mSucessShow.observe(this, { listPending ->
            if (listPending.isEmpty()) {
                mBinding.lottie.visibility = View.VISIBLE
            } else {
                mBinding.lottie.visibility = View.INVISIBLE
                mAdapter.submitList(listPending)
            }

        })
        /**ERRO AO BUSCAR LISTA--> */
        mViewModel.mErrorShow.observe(this) { messageError ->
            vibrateExtension(500)
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(mBinding.root, messageError)
        }
        /**PROGRESSBAR--> */
        mViewModel.mValidaProgressShow.observe(this, { validadProgress ->
            if (validadProgress) {
                mBinding.progressBarInventario.visibility = View.VISIBLE
            } else {
                mBinding.progressBarInventario.visibility = View.INVISIBLE
            }
        })
    }
}