package com.documentos.wms_beirario.ui.reimpressao.dialogFragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.DialogFragmentReimpressaoNumPedidoBinding
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault.AdapterDialogReimpressaoDefault
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogReimpressaoDefault(private val itemClick: ResponseEtiquetasReimpressao) :
    DialogFragment() {

    private var mBinding: DialogFragmentReimpressaoNumPedidoBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapterReimpressao: AdapterDialogReimpressaoDefault
    private lateinit var mAlert: CustomAlertDialogCustom
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogFragmentReimpressaoNumPedidoBinding.inflate(layoutInflater)
        initConst()
        setupRv()
        clickButtons()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        verificaConnect()
    }

    private fun verificaConnect() {
        if (BluetoohPrinterActivity.STATUS != "CONNECTED") {
            mAlert.alertSelectPrinter(
                requireContext(),
                "Nenhuma impressora está conectada!\nDeseja se conectar a uma?"
            )
        } else {
            initConfigPrinter()
        }
    }

    private fun initConfigPrinter() {
        service = BluetoothClassicService.getDefaultInstance()
        writer = BluetoothWriter(service)
    }

    private fun initConst() {
        mAlert = CustomAlertDialogCustom()
    }

    private fun clickButtons() {
        mBinding!!.toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    @DelicateCoroutinesApi
    private fun setupRv() {
        mAdapterReimpressao = AdapterDialogReimpressaoDefault { itemCick ->
            try {
                try {
                    if (BluetoohPrinterActivity.STATUS == "CONNECTED") {
                        if (itemCick.codigoZpl.isNullOrBlank()) {
                            Toast.makeText(
                                requireContext(),
                                "Não foi possivel reimprimir essa etiqueta!\nRótulo vazio",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                                writer.write(itemCick.codigoZpl)
                            }
                        }
                        mBinding!!.progressPrintDialog.isVisible = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            mBinding!!.progressPrintDialog.isVisible = false
                        }, 2000)
                    } else {
                        mAlert.alertSelectPrinter(
                            requireContext(),
                            "Para reimprimir é preciso estar conectado!\nDeseja se conectar a uma impressora?"
                        )
                    }
                } catch (e: Exception) {
                    mBinding!!.progressPrintDialog.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        "Erro ao enviar zpl a impressora! $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Erro ao receber zpl.\n${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        mBinding!!.rvDialog.apply {
            adapter = mAdapterReimpressao
            layoutManager = LinearLayoutManager(requireContext())
        }
        mAdapterReimpressao.update(itemClick)
    }
}