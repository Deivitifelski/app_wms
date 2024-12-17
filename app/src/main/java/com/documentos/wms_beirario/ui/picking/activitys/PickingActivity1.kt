package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityPickingBinding
import com.documentos.wms_beirario.model.picking.PickingResponseModel1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.PickingAdapter1
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModelInit1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class PickingActivity1 : AppCompatActivity() {

    private lateinit var mBinding: ActivityPickingBinding
    private lateinit var mAdapter1: PickingAdapter1
    private lateinit var mViewModel: PickingViewModelInit1
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPickingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolbar()
        initSwipe()
        initRecyclerView()
        initViewModel()
        initConst()
        setObserver()
        initData()
        setupButtonClick()
        setObserver()
    }

    private fun setupButtonClick() {
        mBinding.buttonNewFluxo.setOnClickListener {
            val inten = Intent(this, PickingActivityNewFluxo::class.java)
            startActivity(inten)
            extensionSendActivityanimation()
        }
    }

    override fun onResume() {
        super.onResume()
        initConst()
        initData()
        initRecyclerView()
    }

    private fun initConst() {
        mBinding.txtInformativoPicking1.isVisible = false
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
    }

    private fun initData() {
        mViewModel.getItensPicking1(idArmazem, token)
    }

    private fun enviarProximaActivity(it: PickingResponseModel1) {
        val mintent = Intent(this, PickingActivity2::class.java)
        mintent.putExtra(PickingActivity2.ID_AREA, it.idArea)
        mintent.putExtra("NAME_AREA", it.nomeArea)
        startActivity(mintent)
        extensionSendActivityanimation()
    }


    private fun initToolbar() {
        sharedPreferences = CustomSharedPreferences(this)
        val name = sharedPreferences.getString(CustomSharedPreferences.NAME_USER) ?: ""
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mBinding.toolbarPicking1.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, PickingViewModelInit1.Picking1ViewModelFactory1(
                PickingRepository(this)
            )
        )[PickingViewModelInit1::class.java]
    }

    private fun initRecyclerView() {
        mAdapter1 = PickingAdapter1 {
            enviarProximaActivity(it)
        }
        mBinding.rvPicking1.apply {
            this.layoutManager = LinearLayoutManager(this@PickingActivity1)
            this.adapter = mAdapter1
        }

    }

    /**--------------------Respostas da api Picking areas com tarefa----------------------------->*/
    private fun setObserver() {
        mViewModel.mSucessPickingReturnShows.observe(this) { sucessList ->
            mBinding.txtInformativoPicking1.isVisible = true
            if (sucessList.isEmpty()) {
                mBinding.txtInformativoPicking1.text = "Sem áreas para Picking"
            } else {
                mBinding.txtInformativoPicking1.text = "Selecione a área"
                mAdapter1.update(sucessList)
            }
        }
        mViewModel.mValidProgressInitShow.observe(this) { progressInit ->
            mBinding.progressBarInitPicking1.isVisible = progressInit
        }

        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }

        mViewModel.mErrorPickingShow.observe(this) { error ->
            mAlert.alertMessageErrorSimples(this, error)
        }
    }

    /**--------------------SWIPE----------------------------------------------------------------->*/
    private fun initSwipe() {
        mBinding.swipepicking1.apply {
            setColorSchemeColors(getColor(R.color.color_default))
            setOnRefreshListener {
                mBinding.txtInformativoPicking1.isVisible = false
                initRecyclerView()
                initData()
                isRefreshing = false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}