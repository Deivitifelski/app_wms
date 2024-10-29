package com.documentos.wms_beirario.ui.picking.activitys

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityPickingFinishBinding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.AdapterPicking3
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModelFinish
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class PickingActivityFinish : AppCompatActivity() {

    private lateinit var mAdapter: AdapterPicking3
    private lateinit var mBinding: ActivityPickingFinishBinding
    private lateinit var mViewModel: PickingViewModelFinish
    private lateinit var mPick3Click: PickingResponse3
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var isEmply = false
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityPickingFinishBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initViewModel()
        setupButtonsBacks()
        setupRecyclerView()
        setupObservables()
        setupObservablesReading()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PickingViewModelFinish.Picking2ViewModelFactory(PickingRepository())
        )[PickingViewModelFinish::class.java]
    }


    private fun setupButtonsBacks() {
        mSharedPreferences = CustomSharedPreferences(this)
        val name = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER) ?: ""
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mBinding.toolbarPicking3.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupRecyclerView() {
        mAdapter = AdapterPicking3(idArmazem = idArmazem) { itemClick ->
            alertFinishPicking(itemClick)
        }

        mBinding.rvPicking3.apply {
            layoutManager = LinearLayoutManager(this@PickingActivityFinish)
            adapter = mAdapter
        }

        callApi()
    }

    private fun callApi() {
        mViewModel.getItensPicking(idArmazem, token)
    }

    private fun setupObservables() {
        mViewModel.mSucessShow.observe(this) { list ->
            isEmply = list.size == 1
            if (list.isEmpty()) {
                mBinding.txtInf.isVisible = true
            } else {
                mBinding.txtInf.isVisible = false
                mAdapter.submitList(list)
            }
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
        if (idArmazem != 67) {
            mBindingAlert.txtInf.text = "Destino para: ${itemClick.descricaoEmbalagem} - ${itemClick.quantidade}"
        } else {
            mBindingAlert.txtInf.visibility = View.GONE
        }
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        mBindingAlert.editQrcodeCustom.addTextChangedListener {
            val qrcode = it?.trim()
            if (qrcode != null) {

                if (qrcode.isNotEmpty()) {
                    mBinding.progressBarAddPicking3.isVisible = true
                    mBindingAlert.progressEdit.visibility = View.VISIBLE
                    sendReadingAlertDialog(itemClick, qrcode.trim().toString())
                    clearTextAlertScaner(mBindingAlert)
                    mShow.dismiss()
                }
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
                    itemClick.idProduto, //manda como 0 quando não for armazem 100
                    itemClick.quantidade,
                    qrcode
                ),
                idArmazem,
                token
            )
        }
    }

    private fun setupObservablesReading() {
        mViewModel.mSucessReadingShow.observe(this) {
            mBinding.progressBarAddPicking3.isVisible = false
            if (isEmply){
                CustomAlertDialogCustom().alertMessageSucess(
                    this,
                    getString(R.string.all_picking_sucess)
                )
            }else{
                CustomAlertDialogCustom().alertMessageSucess(
                    this,
                    "Picking Finalizado com Sucesso!"
                )
            }

            setupRecyclerView()
        }
        mViewModel.mErrorReadingShow.observe(this) { messageErrorReading ->
            mBinding.progressBarAddPicking3.isVisible = false
            CustomAlertDialogCustom().alertMessageErrorSimples(
                this,
                messageErrorReading, 2000
            )
        }
        mViewModel.mErrorAllShow.observe(this) { error ->
            mBinding.progressBarAddPicking3.isVisible = false
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

