package com.documentos.wms_beirario.ui.picking.activitys

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityPickingFinishBinding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.model.picking.ResponsePickingReturnGroupedItem
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.AdapterPicking3
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModelFinish
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import java.util.*

class PickingActivityFinish : AppCompatActivity() {

    private lateinit var mAdapter: AdapterPicking3
    private lateinit var mBinding: ActivityPickingFinishBinding
    private lateinit var mViewModel: PickingViewModelFinish
    private lateinit var mPick3Click: PickingResponse3

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityPickingFinishBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initViewModel()
        setupRecyclerView()
        setupObservablesReading()
        setupButtonsBacks()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PickingViewModelFinish.Picking2ViewModelFactory(PickingRepository())
        )[PickingViewModelFinish::class.java]
    }


    private fun setupButtonsBacks() {
        mBinding.toolbarPicking3.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterPicking3 { itemClick ->
            alertFinishPicking(itemClick)
        }
        mBinding.rvPicking3.apply {
            layoutManager = LinearLayoutManager(this@PickingActivityFinish)
            adapter = mAdapter
        }
        callApi()
    }

    private fun callApi() {
        mViewModel.getItensPicking()
        setupObservables()
    }

    private fun setupObservables() {
        mViewModel.mSucessShow.observe(this) { list ->
            mAdapter.submitList(list)
        }
        mViewModel.mErrorShow.observe(this) { messageError ->
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError)
        }
        mViewModel.mValidProgressShow.observe(this) { validProgress ->
            mBinding.progressBarAddPicking3.isVisible = validProgress
        }
    }

    /**VALIDAR LEITURA !!!!!!!!!!!!!!!!!!!!!!!!!!!-->*/
    private fun alertFinishPicking(itemClick: PickingResponse3) {
        mPick3Click = itemClick
        CustomMediaSonsMp3().somAlerta(this)
        val mAlert = AlertDialog.Builder(this)
        val mBindingAlert = LayoutCustomFinishMovementAdressBinding.inflate(layoutInflater)
        mAlert.setView(mBindingAlert.root)
        val mShow = mAlert.show()
        mBindingAlert.editQrcodeCustom.requestFocus()
        mBindingAlert.txtInf.text =
            "Destino para: ${itemClick.descricaoEmbalagem} - ${itemClick.quantidade}"
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        mBindingAlert.editQrcodeCustom.extensionSetOnEnterExtensionCodBarras {
            val qrcode = mBindingAlert.editQrcodeCustom.text.toString()
            if (qrcode.isNotEmpty()) {
                mBindingAlert.progressEdit.visibility = View.VISIBLE
                sendReadingAlertDialog(itemClick, qrcode.trim())
                clearTextAlertScaner(mBindingAlert)
                mShow.dismiss()
            } else {
                vibrateExtension(500)
                CustomSnackBarCustom().toastCustomError(this, "Campo Vazio!")
            }
            mBindingAlert.progressEdit.visibility = View.INVISIBLE
        }
        mAlert.setOnDismissListener { it.dismiss() }
        mBindingAlert.buttonCancelCustom.setOnClickListener {
            mBindingAlert.progressEdit.visibility = View.INVISIBLE
            CustomMediaSonsMp3().somClick(this)
            mShow.dismiss()
        }
    }

    private fun clearTextAlertScaner(mBindingAlert: LayoutCustomFinishMovementAdressBinding) {
        mBindingAlert.editQrcodeCustom.setText("")
        mBindingAlert.editQrcodeCustom.text!!.clear()
        mBindingAlert.editQrcodeCustom.requestFocus()
    }

    private fun sendReadingAlertDialog(
        itemClick: PickingResponse3? = null,
        qrcode: String
    ) {
        if (itemClick != null) {
            Log.e(
                "PICKING 3",
                "ENVIANDO DADOS PARA FINALIZAR PICKING 3 -->:${itemClick.idProduto}|${itemClick.quantidade}|$qrcode"
            )
            mViewModel.finishTaskPicking(
                PickingRequest2(
                    itemClick.idProduto,
                    itemClick.quantidade,
                    qrcode
                )
            )
        }
    }

    private fun setupObservablesReading() {
        mViewModel.mSucessReadingShow.observe(this) {
            CustomAlertDialogCustom().alertMessageSucess(
                this,
                getString(R.string.all_picking_sucess)
            )
            setupRecyclerView()
        }
        mViewModel.mErrorReadingShow.observe(this) { messageErrorReading ->
            CustomAlertDialogCustom().alertMessageErrorSimples(
                this,
                messageErrorReading, 2000
            )
        }
        mViewModel.mValidProgressShow.observe(this) { progress ->
            mBinding.progressBarAddPicking3.isVisible = progress
        }
        mViewModel.mErrorAllShow.observe(this) { error ->
            CustomAlertDialogCustom().alertMessageErrorSimples(
                this,
                error, 2000
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}

