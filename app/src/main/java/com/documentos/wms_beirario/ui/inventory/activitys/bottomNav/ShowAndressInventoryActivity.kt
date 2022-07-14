package com.documentos.wms_beirario.ui.inventory.activitys.bottomNav

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityShowAndressInventoryBinding
import com.documentos.wms_beirario.model.inventario.ProcessaLeituraResponseInventario2
import com.documentos.wms_beirario.model.inventario.ResponseInventoryPending1
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.inventory.viewModel.InventoryBarCodeFragmentButtonAndressViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.vibrateExtension


class ShowAndressInventoryActivity : AppCompatActivity() {

    private val TAG =
        "com.documentos.wms_beirario.ui.inventory.activitys.bottomNav.ShowAndressInventoryActivity"
    private lateinit var mViewModel: InventoryBarCodeFragmentButtonAndressViewModel
    lateinit var mBindng: ActivityShowAndressInventoryBinding
    private lateinit var mIntentDataActivity1: ResponseInventoryPending1
    private lateinit var mIntentProcessaLeitura: ProcessaLeituraResponseInventario2
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom


    override fun onCreate(savedInstanceState: Bundle?) {
        mBindng = ActivityShowAndressInventoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBindng.root)
        setSupportActionBar(mBindng.toolbar)
        startIntent()
        initConst()
        initViewModel()
        setToolbar()
        setupObservables()
    }

    private fun startIntent() {
        try {
            val getData = intent
            val mData = getData.getSerializableExtra("SEND_ANDRESS_RESPONSE_ACTIVITY_1")
            mIntentDataActivity1 = mData as ResponseInventoryPending1
            val data2 = getData.getSerializableExtra("SEND_ANDRESS_REANDING_QRCODE")
            mIntentProcessaLeitura = data2 as ProcessaLeituraResponseInventario2
            Log.e(TAG, "startIntent -> $mIntentDataActivity1 || $mIntentProcessaLeitura")
        } catch (e: Exception) {
            mErrorShow("Erro ao receber dados!")
        }
    }

    private fun initConst() {
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            InventoryBarCodeFragmentButtonAndressViewModel.BarCodeFragiewModelFactory(
                InventoryoRepository1()
            )
        )[InventoryBarCodeFragmentButtonAndressViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        callApi()


    }

    private fun setToolbar() {
        mBindng.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun callApi() {
        val idEndereco = mIntentProcessaLeitura.idEndereco
        mViewModel.getItensRecyclerView(
            idEndereco = idEndereco!!,
            mIntentDataActivity1.id,
            mIntentDataActivity1.numeroContagem
        )
    }

    private fun setupObservables() {
        //SUCESS -->
        mViewModel.mSucessShowbuttonAndress.observe(this) { responseSucess ->
            initListVol(responseSucess)
            clickBottomNav(responseSucess)
        }
        //ERROR -->
        mViewModel.mErrorShowButtonAndress.observe(this) { errorMessage ->
            CustomSnackBarCustom().snackBarErrorSimples(mBindng.root, errorMessage)
        }
        //PROGRESS -->
        mViewModel.mValidaProgressShow.observe(this) { validaProgress ->
            if (validaProgress) {
                AppExtensions.visibilityProgressBar(mBindng.initProgress, visibility = true)
            } else {
                AppExtensions.visibilityProgressBar(mBindng.initProgress, visibility = false)
            }
        }
    }

    private fun initListVol(responseSucess: ResponseListRecyclerView) {
        val bundle = bundleOf("VOLUME_SHOW_ANDRESS" to responseSucess)
        supportFragmentManager.commit {
            replace<VolumeBottomNavFragment>(
                R.id.fragment_parent_show_andress,
                args = bundle
            )
            setReorderingAllowed(true)
        }
    }

    /**AJUSTAR A MUDANÇA E FRAGMENTOS -->*/

    private fun clickBottomNav(responseSucess: ResponseListRecyclerView) {
        mBindng.bottomNav.setOnItemSelectedListener { bottomNavClick ->
            when (bottomNavClick.itemId) {
                R.id.button_produto_nav -> {
                    val bundle = bundleOf(
                        "PRODUTO_SHOW_ANDRESS" to responseSucess,
                        "CONTEM_ID_INVENTORI" to mIntentDataActivity1
                    )

                    supportFragmentManager.commit {
                        replace<ProdutoBottomNavFragment>(
                            R.id.fragment_parent_show_andress,
                            args = bundle
                        )
                        setReorderingAllowed(true)
                    }
                }
                R.id.button_volume_nav -> {
                    val bundle = bundleOf("VOLUME_SHOW_ANDRESS" to responseSucess)
                    supportFragmentManager.commit {
                        replace<VolumeBottomNavFragment>(
                            R.id.fragment_parent_show_andress,
                            args = bundle
                        )
                        setReorderingAllowed(true)
                    }
                }
            }
            true
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_printer -> {
                extensionStartActivity(BluetoohPrinterActivity())
            }
        }
        return true
    }

    private fun mErrorShow(title: String) {
        vibrateExtension(500)
        mToast.toastCustomError(this, title)
    }

    private fun mSucessShow(title: String) {
        vibrateExtension(500)
        mToast.toastCustomSucess(this, title)
    }


    /**CLICK MENU ----------->*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_open_printer, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}