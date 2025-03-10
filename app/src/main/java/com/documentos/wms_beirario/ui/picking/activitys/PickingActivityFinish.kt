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
import com.documentos.wms_beirario.utils.extensions.toastDefault

class PickingActivityFinish : AppCompatActivity() {

    private lateinit var adapterItens: AdapterPicking3
    private lateinit var binding: ActivityPickingFinishBinding
    private lateinit var viewModel: PickingViewModelFinish
    private lateinit var mPick3Click: PickingResponse3
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private var isEmply = false
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPickingFinishBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewModel()
        setupButtonsBacks()
        setupRecyclerView()
        setupObservables()
        setupObservablesReading()
        cliqueButtonFinalizarTodos()
        visibilityButtonFinalizarTodos()
    }

    private fun visibilityButtonFinalizarTodos() {
        if (idArmazem != 100) {
            binding.buttonFinalizarTodos.visibility = View.VISIBLE
        } else {
            binding.buttonFinalizarTodos.visibility = View.GONE
        }
    }

    private fun cliqueButtonFinalizarTodos() {
        binding.buttonFinalizarTodos.setOnClickListener {
            alertFinishPicking()
        }
    }


    private fun alertFinishPicking() {
        CustomMediaSonsMp3().somAlerta(this)
        val mAlert = AlertDialog.Builder(this)
        val mBindingAlert = LayoutCustomFinishMovementAdressBinding.inflate(layoutInflater)
        mAlert.setView(mBindingAlert.root)
        val mShow = mAlert.show()
        mBindingAlert.editQrcodeCustom.requestFocus()
        mBindingAlert.txtInf.text = "Finalizar todos os ${adapterItens.returnQtd()?:0} itens"
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        mBindingAlert.editQrcodeCustom.addTextChangedListener {
            val qrcode = it?.trim()
            if (qrcode != null) {

                if (qrcode.isNotEmpty()) {
                    binding.progressBarAddPicking3.isVisible = true
                    mBindingAlert.progressEdit.visibility = View.VISIBLE
                    sendReadingTodosAlertDialog(qrcode.trim().toString())
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

    private fun sendReadingTodosAlertDialog(enderecoLeitura: String) {
        viewModel.finishTaskPicking(
            PickingRequest2(
                idProduto = 0,
                quantidade = adapterItens.returnQtd(),
                enderecoLeitura = enderecoLeitura
            ),
            idArmazem = idArmazem,
            token = token
        )
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(
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
        binding.toolbarPicking3.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            subtitle = getVersionNameToolbar()
        }
    }

    private fun setupRecyclerView() {
        adapterItens = AdapterPicking3(idArmazem = idArmazem) { itemClick ->
            alertFinishPicking(itemClick)
        }

        binding.rvPicking3.apply {
            layoutManager = LinearLayoutManager(this@PickingActivityFinish)
            adapter = adapterItens
        }

        callApi()
    }

    private fun callApi() {
        viewModel.getItensPicking(idArmazem, token)
    }

    private fun setupObservables() {
        viewModel.mSucessShow.observe(this) { list ->
            isEmply = list.isEmpty()
            if (list.isEmpty()) {
                binding.txtInf.isVisible = true
            } else {
                binding.txtInf.isVisible = false
                adapterItens.submitList(list)
            }
        }
        viewModel.mErrorShow.observe(this) { messageError ->
            CustomAlertDialogCustom().alertMessageErrorSimples(this, messageError)
        }
        viewModel.mValidProgressShow.observe(this) { validProgress ->
            binding.progressBarAddPicking3.isVisible = validProgress
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
            mBindingAlert.txtInf.text =
                "Destino para: ${itemClick.descricaoEmbalagem} - ${itemClick.quantidade}"
        } else {
            mBindingAlert.txtInf.visibility = View.GONE
        }
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        mBindingAlert.editQrcodeCustom.addTextChangedListener {
            val qrcode = it?.trim()
            if (qrcode != null) {

                if (qrcode.isNotEmpty()) {
                    binding.progressBarAddPicking3.isVisible = true
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
            viewModel.finishTaskPicking(
                PickingRequest2(
                    itemClick.idProduto,
                    itemClick.quantidade,
                    qrcode
                ),
                idArmazem,
                token
            )
        }
    }

    private fun setupObservablesReading() {
        viewModel.mSucessReadingShow.observe(this) {
            binding.progressBarAddPicking3.isVisible = false
            if (isEmply) {
                CustomAlertDialogCustom().alertMessageSucessAction(
                    this,
                    getString(R.string.all_picking_sucess),
                    action = { finish() }
                )
            } else {
                CustomAlertDialogCustom().alertMessageSucess(
                    this,
                    "Picking Finalizado com Sucesso!"
                )
            }

            setupRecyclerView()
        }
        viewModel.mErrorReadingShow.observe(this) { messageErrorReading ->
            binding.progressBarAddPicking3.isVisible = false
            CustomAlertDialogCustom().alertMessageErrorSimples(
                this,
                messageErrorReading, 2000
            )
        }
        viewModel.mErrorAllShow.observe(this) { error ->
            binding.progressBarAddPicking3.isVisible = false
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

