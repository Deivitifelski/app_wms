package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityPicking2Binding
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.PickingAdapter2
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.vibrateExtension


class PickingActivity2 : AppCompatActivity() {

    private lateinit var mBinding: ActivityPicking2Binding
    private lateinit var mAdapter: PickingAdapter2
    private var mId_area: Int = 0
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mViewModel: PickingViewModel2

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityPicking2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        getIntentExtas()
        initToolbar()
        initViewModel()
        initConst()
        initObserver()
        lerItem()
        finalizarPicking()

    }

    private fun getIntentExtas() {
        try {
            if (intent.extras != null)
                mId_area = intent.getIntExtra(ID_AREA, 0)
        } catch (e: Exception) {
            mErrorToast(e.toString())
        }
    }

    private fun initConst() {
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
        mBinding.buttonfinalizarpickin2.isEnabled = false
        mBinding.progressBarAddPicking2.isVisible = false
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initGetData()
    }

    private fun initRecyclerView() {
        mAdapter = PickingAdapter2()
        mBinding.rvPicking2.apply {
            layoutManager = LinearLayoutManager(this@PickingActivity2)
            adapter = mAdapter
        }
    }

    private fun initToolbar() {
        mBinding.toolbarPicking2.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun initGetData() {
        mViewModel.getItensPicking2(mId_area)
        mViewModel.getItensPickingFinishValidadButton()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PickingViewModel2.Picking2ViewModelFactory(PickingRepository())
        )[PickingViewModel2::class.java]
    }

    private fun lerItem() {
        mBinding.editPicking2.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editPicking2.toString() != "") {
                sendData(mBinding.editPicking2.text.toString().trim())
                clearEdit()
            } else {
                mErrorToast(getString(R.string.edit_emply))
            }
        }
    }

    private fun clearEdit() {
        mBinding.editPicking2.setText("")
        mBinding.editPicking2.text?.clear()
        mBinding.editPicking2.requestFocus()
    }

    private fun sendData(scan: String) {
        mViewModel.getItensPickingReanding2(
            idArea = mId_area,
            pickingRepository = PickingRequest1(scan)
        )
    }

    private fun initObserver() {
        /**RESPOSTAS DO PRIMEIRO GET PARA TRAZER TAREFAS DAS AREAS -->*/
        mViewModel.mSucessPickingReturnShows.observe(this, { list ->
            if (list.isEmpty()) {
                mBinding.txtInformativoPicking2.isVisible = true
            } else {
                mBinding.txtInformativoPicking2.isVisible = false
                mAdapter.update(list)
            }
        })
        mViewModel.mErrorAllShow.observe(this, { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        })
        mViewModel.mErrorPickingShow.observe(this, { errorGetPicking ->
            mAlert.alertMessageErrorSimples(this, errorGetPicking)
        })
        mViewModel.mValidProgressInitShow.observe(this, { progressInit ->
            mBinding.progressBarInitPicking2.isVisible = progressInit
        })
        mViewModel.mValidProgressEditShow.observe(this, { progressEdit ->
            mBinding.progressBarInitPicking2.isVisible = progressEdit
        })
        /**RESPOSTAS DA LEITURA -->*/
        mViewModel.mSucessPickingReadShow.observe(this, { sucessReanding ->
            mediaSonsMp3.somSucess(this)
            initGetData()
            initRecyclerView()
            vibrateExtension(500)
        })

        mViewModel.mErrorReadingPickingShow.observe(this, { erroReanding ->
            mAlert.alertMessageErrorSimples(this, erroReanding)
        })

        /**FAZ O GET DA TELA FINAL PARA VER SE CONTEM ITENS PARA HABILITAR O BUTTON-->*/
        mViewModel.mSucessShow.observe(this, { sucessValidaButton ->
            mBinding.buttonfinalizarpickin2.isEnabled = sucessValidaButton.isNotEmpty()

        })

        mViewModel.mErrorShow.observe(this, { sucessValidaButton ->
            mToast.toastCustomError(this, "Erro ao validar button!\n$sucessValidaButton")
        })
    }


    /**BUTTON FINALIZAR PICKING -->*/
    fun finalizarPicking() {
        mBinding.buttonfinalizarpickin2.setOnClickListener {
            val intent = Intent(this, PickingActivityFinish::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun mErrorToast(msg: String) {
        mToast.toastCustomError(this, msg)
        vibrateExtension(500)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    companion object {
        const val ID_AREA = "id_area_picking_1"
    }
}