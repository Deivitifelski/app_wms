package com.documentos.wms_beirario.ui.mountingVol.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityMounting1Binding
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.mountingVol.adapters.AdapterMounting1
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import java.util.*


class MountingActivity1 : AppCompatActivity() {

    private lateinit var mAdapter: AdapterMounting1
    private val TAG = "MountingActivity1"
    private lateinit var mBinding: ActivityMounting1Binding
    private lateinit var mViewModel: MountingVolViewModel1
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityMounting1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setToolbar()
        initViewModel()
        initCons()
        setupRecyclerView()
        callApi()
        setupObservables()
    }


    override fun onResume() {
        super.onResume()

        setupRecyclerView()
        callApi()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MountingVolViewModel1.MountingViewModelFactory(MountingVolRepository())
        )[MountingVolViewModel1::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbarMontagemdevolumes1.apply {
            subtitle = "[${getVersion()}]"
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun callApi() {
        mViewModel.getMounting1()
    }

    private fun initCons() {
        mBinding.refreshMounting1.apply {
            setColorSchemeColors(getColor(R.color.color_default))
            setOnRefreshListener {
                setupRecyclerView()
                callApi()
                isRefreshing = false
            }
        }
        mBinding.lotie.visibility = View.INVISIBLE
        mBinding.txtInf.visibility = View.GONE
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterMounting1 { itemClick ->
            val intent = Intent(this, MountingActivity2::class.java)
            intent.putExtra("ID_PROD_KIT", itemClick)
            startActivity(intent)
            extensionSendActivityanimation()
        }

        mBinding.rvMontagem1.apply {
            layoutManager = LinearLayoutManager(this@MountingActivity1)
            adapter = mAdapter
        }
    }

    private fun setupObservables() {
        mViewModel.mShowShow.observe(this) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding.lotie.visibility = View.VISIBLE
                mBinding.txtInf.visibility = View.VISIBLE
            } else {
                mBinding.txtInf.visibility = View.GONE
                mBinding.lotie.visibility = View.INVISIBLE
                mAdapter.submitList(listSucess)
            }
        }
        mViewModel.mErrorShow.observe(this) { messageError ->
            CustomSnackBarCustom().snackBarSimplesBlack(mBinding.root, messageError)
        }
        mViewModel.mValidaProgressShow.observe(this) { validProgress ->
            if (validProgress) mBinding.progressBarInitMontagem1.visibility = View.VISIBLE
            else
                mBinding.progressBarInitMontagem1.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}
