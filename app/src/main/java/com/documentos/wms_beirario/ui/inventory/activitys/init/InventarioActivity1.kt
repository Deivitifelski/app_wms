package com.documentos.wms_beirario.ui.inventory.activitys.init

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityInventario1Binding
import com.documentos.wms_beirario.databinding.LayoutRadioButtonBinding
import com.documentos.wms_beirario.model.inventario.ResponseInventoryPending1
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventario1
import com.documentos.wms_beirario.ui.inventory.viewModel.PendingTaskInventoryViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class InventarioActivity1 : AppCompatActivity() {

    private lateinit var adapterInventario1: AdapterInventario1
    private lateinit var binding: ActivityInventario1Binding
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mViewModel: PendingTaskInventoryViewModel1
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInventario1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
        binding.progressBarInventario.isVisible = true
        binding.txtInfo.isVisible = false
        binding.lottie.isVisible = false
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    private fun setupToolbar() {
        binding.toolbarInventario.apply {
            subtitle = getVersionNameToolbar()
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
        adapterInventario1 = AdapterInventario1 { clickAdapter ->
            val contagens = clickAdapter.numeroContagem
            showNumberPickerModal(contagens, clickAdapter.documento, clickAdapter)
        }

        binding.rvInventario1.apply {
            layoutManager = LinearLayoutManager(this@InventarioActivity1)
            adapter = adapterInventario1
        }
    }


    private fun showNumberPickerModal(
        maxCount: Int,
        documento: Long,
        itemClick: ResponseInventoryPending1
    ) {
        val numbers = (1..maxCount).toList() // Cria uma lista de números de 1 até maxCount

        // Cria um AlertDialog Builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecione a contagem")
        builder.setMessage("Documento: $documento")

        val binding = LayoutRadioButtonBinding.inflate(LayoutInflater.from(this))
        val radioGroup: RadioGroup = binding.radioContagemInventario

        for (number in numbers) {
            val radioButton = RadioButton(this)
            radioButton.text = number.toString()
            radioButton.id = number // Define o ID do RadioButton como o próprio número
            radioGroup.addView(radioButton) // Adiciona o RadioButton ao RadioGroup
        }
        if (radioGroup.childCount > 0) {
            (radioGroup.getChildAt(0) as RadioButton).isChecked = true
        }

        // Adiciona o layout ao builder
        builder.setView(binding.root)

        // Configura o botão "OK"
        builder.setPositiveButton("OK") { dialog, which ->
            // Recupera o número selecionado
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedNumber = radioGroup.findViewById<RadioButton>(selectedId).text.toString().toInt()
            mSharedPreferences.saveInt(CustomSharedPreferences.ID_INVENTORY, itemClick.id)
            val intent = Intent(this, InventoryActivity2::class.java)
            intent.putExtra("DATA_ACTIVITY_1", itemClick)
            intent.putExtra("CONTAGEM", selectedNumber)
            startActivity(intent)
            extensionSendActivityanimation()
        }

        builder.setNegativeButton("Cancelar") { dialog, which -> dialog.dismiss() }

        builder.create().show()
    }


    private fun setupObservables() {
        /**LISTA VAZIA -->*/
        mViewModel.mValidadTxtShow.observe(this) { validadTxt ->
            binding.txtInfo.isVisible = true
            if (validadTxt) {
                binding.txtInfo.text = getString(R.string.denied_information)
            } else {
                binding.txtInfo.text = getString(R.string.click_select_item)
            }

        }
        /**LISTA COM ITENS -->*/
        mViewModel.mSucessShow.observe(this) { listPending ->
            if (listPending.isEmpty()) {
                binding.lottie.visibility = View.VISIBLE
            } else {
                binding.lottie.visibility = View.INVISIBLE
                adapterInventario1.submitList(listPending)
            }

        }
        /**ERRO AO BUSCAR LISTA--> */
        mViewModel.mErrorShow.observe(this) { messageError ->
            vibrateExtension(500)
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(binding.root, messageError)
        }
        /**PROGRESSBAR--> */
        mViewModel.mValidaProgressShow.observe(this) { validadProgress ->
            if (validadProgress) {
                binding.progressBarInventario.visibility = View.VISIBLE
            } else {
                binding.progressBarInventario.visibility = View.INVISIBLE
            }
        }
    }
}