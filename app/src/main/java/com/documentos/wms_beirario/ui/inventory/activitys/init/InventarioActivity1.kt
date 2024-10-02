package com.documentos.wms_beirario.ui.inventory.activitys.init

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
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
import com.documentos.wms_beirario.utils.extensions.*

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
            showNumberPickerModal(contagens,clickAdapter.documento)
//            mSharedPreferences.saveInt(CustomSharedPreferences.ID_INVENTORY, clickAdapter.id)
//            val intent = Intent(this, InventoryActivity2::class.java)
//            intent.putExtra("DATA_ACTIVITY_1", clickAdapter)
//            startActivity(intent)
//            extensionSendActivityanimation()
        }
        binding.rvInventario1.apply {
            layoutManager = LinearLayoutManager(this@InventarioActivity1)
            adapter = adapterInventario1
        }
    }


    private fun showNumberPickerModal(maxCount: Int, documento: Long) {
        val numbers = (1..maxCount).toList() // Cria uma lista de números de 1 até maxCount
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecione a contagem")
        builder.setMessage("Documento: $documento")
        val dropdownBinding =
            layoutInflater.inflate(R.layout.drop_dow_layout, null) // Crie um layout para o dropdown
        val spinner: Spinner = dropdownBinding.findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        builder.setView(dropdownBinding)

        // Configura o botão "OK"
        builder.setPositiveButton("OK") { dialog, which ->
            val selectedNumber = spinner.selectedItem as Int
            toastDefault(this, selectedNumber.toString())
            // Lógica adicional com o número selecionado, se necessário
        }

        // Configura o botão "Cancelar"
        builder.setNegativeButton("Cancelar") { dialog, which -> dialog.dismiss() }

        // Exibe o dialog
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