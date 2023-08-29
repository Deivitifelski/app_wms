package com.documentos.wms_beirario.ui.reimpressao.dialogFragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.DialogFragmentReimpressaoNumPedidoBinding
import com.documentos.wms_beirario.model.logPrinter.BodySaveLogPrinter
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressaoItem
import com.documentos.wms_beirario.repository.reimpressao.ReimpressaoRepository
import com.documentos.wms_beirario.ui.bluetooh.BluetoohPrinterActivity
import com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault.AdapterDialogReimpressaoDefault
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogReimpressaoDefault(
    private val itemClick: ResponseEtiquetasReimpressao,
    private val mIdTarefa: String? = null,
    private val mSequencialTarefa: Int? = null,
    private val mNumeroSerie: String? = null,
    private val mIdInventarioAbastecimentoItem: String? = null,
    private val mIdOrdemMontagemVolume: String? = null,
    private val idArmazem: Int,
    private val token: String,
) :
    DialogFragment() {

    private var mBinding: DialogFragmentReimpressaoNumPedidoBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapterReimpressao: AdapterDialogReimpressaoDefault
    private lateinit var mAlert: CustomAlertDialogCustom
    private var service: BluetoothService? = null
    private lateinit var writer: BluetoothWriter
    private lateinit var mViewModel: SaveLogReimpressaoViewModel
    private lateinit var mCodZpl: String
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3

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
        observer()
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
        mediaSonsMp3 = CustomMediaSonsMp3()
        mViewModel = ViewModelProvider(
            this, SaveLogReimpressaoViewModel.SaveLogReimpressaoViewModelFactory(
                ReimpressaoRepository()
            )
        )[SaveLogReimpressaoViewModel::class.java]
        mAlert = CustomAlertDialogCustom()
    }

    private fun clickButtons() {
        mBinding!!.toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    private fun observer() {
        mViewModel.mErrorAllShow.observe(this) { messageError ->
            mAlert.alertMessageErrorSimples(requireContext(), messageError)
        }
        mViewModel.mErrorHttpShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(requireContext(), errorAll)
            Log.e("REIMPRESSÃO -->", "ERRO BANCO -> $errorAll \n não foi possivel salvar LOG")
        }
        mViewModel.mSucessSaveLogShow.observe(this) {
            writer.write(mCodZpl)
            Log.d("REIMPRESSÃO -->", "LOG SALVO COM SUCESSO")
        }
    }

    @DelicateCoroutinesApi
    private fun setupRv() {
        mAdapterReimpressao = AdapterDialogReimpressaoDefault { itemCick ->
            try {
                try {
                    if (BluetoohPrinterActivity.STATUS == "CONNECTED") {
                        if (itemCick.codigoZpl.isNullOrEmpty()) {
                            mediaSonsMp3.somError(requireContext())
                            Toast.makeText(
                                requireContext(),
                                "Não foi possivel reimprimir essa etiqueta!\nRótulo vazio",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                                //SALVA LOG DE REIMPRESSÕES -->
                                saveLog(itemCick)
                                mCodZpl = itemCick.codigoZpl
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
                        "Erro ao enviar zpl a impressora!",
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

    private fun saveLog(itemCick: ResponseEtiquetasReimpressaoItem) {
        try {
            val body = BodySaveLogPrinter(
                idTarefa = mIdTarefa,
                sequencial = mSequencialTarefa,
                numeroSerie = mNumeroSerie,
                idEtiqueta = itemCick.idEtiqueta,
                idInventarioAbastecimentoItem = mIdInventarioAbastecimentoItem,
                idOrdemMontagemVolume = mIdOrdemMontagemVolume
            )

            mViewModel.saveLog(bodySaveLogPrinter = body, idArmazem, token)
        } catch (e: Exception) {
            Log.e("SAVE_LOG", "ERRO TRY AO SALVAR LOG REIMPRESSÃO")
        }
    }


}