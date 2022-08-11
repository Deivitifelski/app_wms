package com.documentos.wms_beirario.ui.unmountingVolumes.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityUnmountingVolumesBinding
import com.documentos.wms_beirario.repository.desmontagemvolumes.DisassemblyRepository
import com.documentos.wms_beirario.ui.unmountingVolumes.adapter.DisassamblyAdapter
import com.documentos.wms_beirario.ui.unmountingVolumes.viewModel.ViewModelInmounting1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class UnmountingVolumesActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityUnmountingVolumesBinding
    private lateinit var mAdapter: DisassamblyAdapter
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mViewModel: ViewModelInmounting1

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityUnmountingVolumesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setupToolbar()
        initConst()
        setObservables()
        setupSwipe()
    }

    override fun onResume() {
        super.onResume()
        setupRv()
        initData()
    }

    private fun setupToolbar() {
        mBinding.toolbarDesmonVol.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun initConst() {
        //INICIA INVISIVEL -->
        mBinding.linearDesmontagem1.visibility = View.GONE
        mBinding.lottie.visibility = View.INVISIBLE
        mViewModel = ViewModelProvider(
            this, ViewModelInmounting1.UnMounting1ViewModelFactory(
                DisassemblyRepository()
            )
        )[ViewModelInmounting1::class.java]
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
        mBinding.progressUnmonting1.isVisible = false
        /**CLICK NO ITEM -->*/
        mAdapter = DisassamblyAdapter { itemClick ->
            val intent = Intent(this, UnMountingVolumesActivity2::class.java)
            intent.putExtra("ITEM_CLICK_1", itemClick)
            startActivity(intent)
            extensionSendActivityanimation()
        }
    }

    private fun setObservables() {
        mViewModel.mSucessShow.observe(this) { listSucess ->
            try {
                if (listSucess.isEmpty()) {
                    mBinding.apply {
                        txtInfoDesmontagem1.visibility = View.VISIBLE
                        linearDesmontagem1.visibility = View.GONE
                        lottie.isVisible = true
                    }
                    mAdapter.update(listSucess)
                } else {
                    mBinding.apply {
                        linearDesmontagem1.visibility = View.VISIBLE
                        txtInfoDesmontagem1.visibility = View.GONE
                        lottie.isVisible = false
                    }
                    mAdapter.update(listSucess)
                }
            } catch (e: Exception) {
                mErrorToast(e.toString())
            }
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
        }

        mViewModel.mErrorHttpShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
        }


        mViewModel.mProgressShow.observe(this) { progress ->
            mBinding.progressUnmonting1.isVisible = progress
        }
    }

    private fun initData() {
        mViewModel.getTaskDisassembly1()
    }

    private fun setupSwipe() {
        mBinding.swipeUnMonting1.apply {
            setColorSchemeColors(getColor(R.color.color_default))
            setOnRefreshListener {
                mBinding.lottie.playAnimation()
                initData()
                setupRv()
                isRefreshing = false
            }
        }
    }

    private fun mErrorToast(toString: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, toString)
    }

    private fun setupRv() {
        mBinding.rvDemontVol.apply {
            layoutManager = LinearLayoutManager(this@UnmountingVolumesActivity)
            adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}
