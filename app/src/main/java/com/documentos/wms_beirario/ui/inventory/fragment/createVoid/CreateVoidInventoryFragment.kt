package com.documentos.wms_beirario.ui.inventory.fragment.createVoid

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentCreateVoidBinding
import com.documentos.wms_beirario.databinding.LayoutCorrugadoBinding
import com.documentos.wms_beirario.databinding.LayoutRvSelectQntShoesBinding
import com.documentos.wms_beirario.model.inventario.Combinacoes
import com.documentos.wms_beirario.model.inventario.CreateVoidPrinter
import com.documentos.wms_beirario.model.inventario.Distribuicao
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugados
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterCorrugadosInventory
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterCreateVoidItem
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventorySelectNum
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterselectQntShoes
import com.documentos.wms_beirario.ui.inventory.adapter.createVoid.AdapterCreateObjectPrinter
import com.documentos.wms_beirario.ui.inventory.viewModel.CreateVoidInventoryViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.buttonEnable
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.viewmodel.ext.android.viewModel


class CreateVoidInventoryFragment : Fragment() {
    private var mBinding: FragmentCreateVoidBinding? = null
    private val _binding get() = mBinding!!
    private val mViewModel: CreateVoidInventoryViewModel by viewModel()
    private lateinit var mAdapterTamShoes: AdapterInventorySelectNum
    private lateinit var mAdapterQntShoes: AdapterselectQntShoes
    private lateinit var mAdapterCreateVoid: AdapterCreateVoidItem
    private lateinit var mAdapterPrinter: AdapterCreateObjectPrinter
    private var mQntTotalShoes: Int = 0
    private var mIdcorrugado: Int = 0
    private var mQntItensListPrinter: Int = 0
    private var mQntCorrugadoTotal: Int = 0
    private var mPosition: Int? = null
    private val mPrinterConnection = PrinterConnection()
    private lateinit var mDialog: Dialog
    private val mArgs: CreateVoidInventoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCreateVoidBinding.inflate(inflater, container, false)
        mDialog =
            CustomAlertDialogCustom().progress(requireContext(), getString(R.string.printer_open))
        mDialog.hide()
        buttonEnable(mBinding!!.buttomLimpar, visibility = false)
        buttonEnable(mBinding!!.buttomAdicionar, visibility = false)
        AppExtensions.visibilityProgressBar(mBinding!!.progressPrinter, false)
        buttonEnable(mBinding!!.buttomImprimir, visibility = false)
        mBinding!!.buttonAdicionarInventoryCreate.isChecked = true
        setViews(visibility = false)
        setObservablesCorrugado()
        getEditText()
        verificationsBluetooh()
        clickButton()
        setupNavigation()
        setupToolbar()
        setLayoutVisible(visibilidade = true)
        return _binding.root
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        if (SetupNamePrinter.applicationPrinterAddress.isEmpty()) {
            vibrateExtension(500)
            CustomAlertDialogCustom().alertSelectPrinter(requireContext())
        }
    }

    /**CREATE-->*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**ADAPTER ITENS ADICIONADOS ->*/
        mAdapterPrinter = AdapterCreateObjectPrinter(requireContext()) { _, position ->
            mAdapterPrinter.delete(position)
            Log.e("excluindo posiçao -->", position.toString())
            Toast.makeText(requireContext(), "Excluido! ", Toast.LENGTH_SHORT).show()
            mQntItensListPrinter = mAdapterPrinter.retornaList()
            mViewModel.setButtomImprimir(mQntItensListPrinter)
            val totalParesList = mAdapterPrinter.totalQnts()
            mBinding!!.txtInfAdicionados.text =
                "total de pares adicionados $totalParesList corrugado: $mQntCorrugadoTotal"
        }
        mAdapterCreateVoid = AdapterCreateVoidItem { itemClick, position ->
            alertSelectQnt(itemClick.tamanho.toInt(), position)
        }

    }

    private fun setupToolbar() {
        mBinding!!.toolbar.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
    }

    private fun setupNavigation() {
        /**ALTERANDO A VISIBILIDADE -->*/
        mBinding!!.buttonsNavigationCreateVoid.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) when (checkedId) {
                R.id.button_Adicionar_inventory_create -> {
                    CustomMediaSonsMp3().somClick(requireContext())
                    setLayoutVisible(visibilidade = true)
                }
                R.id.button_Adicionados_inventory_create -> {
                    CustomMediaSonsMp3().somClick(requireContext())
                    setLayoutVisible(visibilidade = false)
                }
            }
        }
    }

    private fun setLayoutVisible(visibilidade: Boolean) {
        if (visibilidade) {
            mBinding!!.layoutAdicionar.visibility = View.VISIBLE
            mBinding!!.layoutAdicionados.visibility = View.INVISIBLE
        } else {
            mBinding!!.layoutAdicionar.visibility = View.INVISIBLE
            mBinding!!.layoutAdicionados.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        setupRvSelectTam()
        clickCorrugado()

    }

    /**ALERTA DO CORRUGADO -->*/
    private fun alertCorrugado(listCorrugados: InventoryResponseCorrugados) {
        val mAlert = BottomSheetDialog(
            requireContext(),
            R.style.BottomSheetStyle
        )
        val mbindingDialog = LayoutCorrugadoBinding.inflate(layoutInflater)
        mAlert.setContentView(mbindingDialog.root)
        /**CLICK ITEM CORRUGADO -->*/
        val mAdapterCorrugados = AdapterCorrugadosInventory { itemClickCorrugado ->
            CustomMediaSonsMp3().somClick(requireContext())
            mQntCorrugadoTotal = itemClickCorrugado.quantidadePares
            mIdcorrugado = itemClickCorrugado.id
            mAdapterPrinter.updateCorrugado(itemClickCorrugado.quantidadePares, mIdcorrugado)
            mBinding!!.buttonSelecioneCorrugado.text = getString(
                R.string.Corrugado_select,
                itemClickCorrugado.id,
                itemClickCorrugado.quantidadePares
            )
            buttonEnable(mBinding!!.buttomLimpar, visibility = true)
            setViews(visibility = true)
            setTxtInfAdicionados()
            mAlert.dismiss()
        }
        mbindingDialog.rvCorrugados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapterCorrugados
        }
        mAdapterCorrugados.submitList(listCorrugados)
        mAlert.show()
    }

    /**CONFIGURAÇAO RECYCLERVIEW TAMANHO -->*/
    private fun setupRvSelectTam(position: Int? = null) {
        /**CLICK TAMANHO SELECIONADO -->*/
        mAdapterTamShoes = AdapterInventorySelectNum { tamSelect ->
            alertSelectQnt(tamSelect, position)
        }
        /**CHAMADA PARA CRIAR RECYCLERVIEW DE TAMANHOS -->*/
        mViewModel.getListTamShoes(first = 20, last = 45)
        /**RESULT CRIAÇAO -->*/
        mViewModel.mSucessCreateListShow.observe(viewLifecycleOwner) { listSelectTam ->
            mAdapterTamShoes.update(listSelectTam, mPosition ?: 0)
        }
        mBinding!!.rvTamShoe.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
            adapter = mAdapterTamShoes
        }
    }

    /**ALERTA SELECIONE QUNTIDADE -->*/
    private fun alertSelectQnt(tamSelect: Int, position: Int? = null) {
        val alert = AlertDialog.Builder(requireContext())
        val mbindingDialog = LayoutRvSelectQntShoesBinding.inflate(layoutInflater)
        alert.setView(mbindingDialog.root)
        mbindingDialog.txtInf.text = getString(R.string.selecione_qnt_para_numero, tamSelect)
        alert.setCancelable(true)

        alert.create()
        val mAlert = alert.show()
        /**CLICK ITEM ALERT QUANTIDADE DE SAPATATOS -->*/
        mAdapterQntShoes = AdapterselectQntShoes { qntShoes ->
            if (qntShoes == 0 && mAdapterCreateVoid.returList().isEmpty()) {
                vibrateExtension(500)
                Toast.makeText(requireContext(), "Lista vazia!!!", Toast.LENGTH_SHORT).show()
            } else if (qntShoes == 0) {
                mAdapterCreateVoid.deleteObject(tamSelect)
                vibrateExtension(500)
                Toast.makeText(requireContext(), "Excluido", Toast.LENGTH_SHORT).show()
                setupButtonAdd()
            } else {
                setupCreateVoid(tamSelect, qntShoes, position)
                setupButtonAdd()
                setTotalQntParesTxt()
            }
            mAlert.dismiss()
        }
        mbindingDialog.rvSelectAlert.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = mAdapterQntShoes
        }
        mViewModel.getListQntShoes(first = 0, last = 10)
        mViewModel.mSucessCreateListALertShow.observe(viewLifecycleOwner) { listQntShoes ->
            mAdapterQntShoes.updateAlertDialog(listQntShoes)
        }
    }

    private fun setTotalQntParesTxt() {
        val qntVoid = mAdapterCreateVoid.getQntShoesObject()
        val qntAddPrinter = mAdapterPrinter.totalQnts()
        mQntTotalShoes = qntVoid + qntAddPrinter
        mBinding!!.txtInfTotalItem.text =
            getString(R.string.quantidade_total_calcados_inventory, mQntTotalShoes)
    }

    /**CRIA OBJETOS A VULSO -->*/
    private fun setupCreateVoid(tamSelect: Int, qntShoes: Int, position: Int?) {
        mBinding!!.rvCriaObject.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = mAdapterCreateVoid
        }
        val objectoCreateVoid = Distribuicao(tamanho = tamSelect.toString(), quantidade = qntShoes)
        mAdapterCreateVoid.updateCreateVoid(
            mQntCorrugadoTotal,
            mQntTotalShoes,
            requireContext(),
            objectoCreateVoid,
            position
        )
    }

    /**CLICK BUTTON CORRUGADO / FAZ A CHAMADA A API DOS CORRUGADOS -->*/
    private fun clickCorrugado() {
        mBinding!!.buttonSelecioneCorrugado.setOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            mViewModel.getCorrugados()
        }
    }

    private fun setObservablesCorrugado() {
        mViewModel.mSucessCorrugados.observe(viewLifecycleOwner) { listCorrugados ->
            alertCorrugado(listCorrugados)
        }
        mViewModel.mErrorCorrugados.observe(viewLifecycleOwner) {
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding!!.root,
                getString(R.string.erro_ao_carregar_lista)
            )
        }
        /**RESPOSTA DA API AO IMPRIMIR -->*/
        mViewModel.mSucessPrinterShow.observe(viewLifecycleOwner) { etiqueta ->
            mPrinterConnection.printZebra(
                etiqueta.toString(),
                SetupNamePrinter.applicationPrinterAddress
            )
            mDialog.hide()
        }

        mViewModel.mErrorPrinterShow.observe(viewLifecycleOwner) { messageErrorPrinter ->
            mDialog.hide()
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(
                requireContext(),
                messageErrorPrinter
            )
        }
    }

    private fun setViews(visibility: Boolean) {
        if (visibility) {
            mBinding!!.containerParent.visibility = View.VISIBLE
        } else {
            mBinding!!.containerParent.visibility = View.INVISIBLE
        }
    }


    private fun getEditText() {
        mBinding!!.editReferencia.addTextChangedListener {
            setupButtonAdd()
        }
        mBinding!!.editLinha.addTextChangedListener {
            setupButtonAdd()
        }
        mBinding!!.editCor.addTextChangedListener {
            setupButtonAdd()
        }
        mBinding!!.editCabecal.addTextChangedListener {
            setupButtonAdd()
        }
    }

    private fun setupButtonAdd() {
        mViewModel.setButtonAdd(
            mBinding!!.editCabecal.text.toString(),
            mBinding!!.editCor.text.toString(),
            mBinding!!.editLinha.text.toString(),
            mBinding!!.editReferencia.text.toString(),
            mQntTotalShoes,
            mQntCorrugadoTotal,
            mAdapterCreateVoid.returList()

        )
        mViewModel.mResponseButtonAddShow.observe(viewLifecycleOwner) { visibilityButtonAdd ->
            if (visibilityButtonAdd) buttonEnable(
                mBinding!!.buttomAdicionar,
                true
            ) else
            buttonEnable(mBinding!!.buttomAdicionar, visibility = false)
        }
    }

    /**CLICK BUTTON ABA ADICIONAR ->*/
    private fun clickButton() {
        mBinding!!.buttomLimpar.setOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            mBinding!!.apply {
                editCabecal.setText("")
                editCor.setText("")
                editLinha.setText("")
                editReferencia.setText("")
                mAdapterCreateVoid.clearList()
                mBinding!!.buttonSelecioneCorrugado.text =
                    getString(R.string.texto_select_corrugado)
            }
            setViews(visibility = false)
        }

        mBinding!!.buttomAdicionar.setOnClickListener {
            setupRecyclerViewAdicionados()
            mBinding!!.buttonAdicionadosInventoryCreate.isChecked = true
            setLayoutVisible(visibilidade = false)
            setupfields()

        }

        mBinding!!.buttomImprimir.setOnClickListener {
            mDialog.show()
            CustomMediaSonsMp3().somClick(requireContext())
            setDataPrinter()
        }
    }

    private fun setupfields() {
        mBinding!!.apply {
            editCabecal.setText("")
            editCor.setText("")
            editLinha.setText("")
            editReferencia.setText("")
            mAdapterCreateVoid.clearList()
        }
        setTxtInfAdicionados()

    }

    @SuppressLint("SetTextI18n")
    private fun setTxtInfAdicionados() {
        val totalParesList = mAdapterPrinter.totalQnts()
        mBinding!!.txtInfAdicionados.text =
            "Total de pares adicionados: $totalParesList corrugado: $mQntCorrugadoTotal"
    }

    private fun setupRecyclerViewAdicionados() {
        /**ENVIANDO LISTA ->*/
        mBinding!!.rvObjetoImpressao.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapterPrinter
        }
        val listAdicionados = Combinacoes(
            mBinding!!.editCabecal.text.toString().toInt(),
            mBinding!!.editCor.text.toString().toInt(),
            mBinding!!.editLinha.text.toString().toInt(),
            mBinding!!.editReferencia.text.toString().toInt(),
            mQntCorrugadoTotal,
            mIdcorrugado,
            distribuicao = mAdapterCreateVoid.returList().toMutableList()
        )
        mAdapterPrinter.update(listAdicionados)
        mQntItensListPrinter = mAdapterPrinter.retornaList()
        mViewModel.setButtomImprimir(mQntItensListPrinter)
        mViewModel.mResponseListImprimirClearoffShow.observe(viewLifecycleOwner) { validButtonPrinter ->
            mBinding!!.buttomImprimir.isEnabled = validButtonPrinter
        }
    }

    /**
     * Enviando Objeto para impressao -->
     */
    private fun setDataPrinter() {
        val list = mAdapterPrinter.retornaListPrinter()
        mViewModel.postPrinter(
            idInventario =  mArgs.clickInventory01.id,
            createVoidPrinter =  CreateVoidPrinter(
                combinacoes = list,
                codigoCorrugado = mIdcorrugado
            ),
            idEndereco = mArgs.responseQrCode.result.idEndereco,
            numeroContagem = mArgs.clickInventory01.numeroContagem
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }


}