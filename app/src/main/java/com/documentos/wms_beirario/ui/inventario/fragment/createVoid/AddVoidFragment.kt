package com.documentos.wms_beirario.ui.inventario.fragment.createVoid

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentAddVoidBinding
import com.documentos.wms_beirario.databinding.LayoutCorrugadoBinding
import com.documentos.wms_beirario.databinding.LayoutRvSelectQntShoesBinding
import com.documentos.wms_beirario.extensions.buttonEnable
import com.documentos.wms_beirario.extensions.vibrate
import com.documentos.wms_beirario.model.inventario.Distribuicao
import com.documentos.wms_beirario.model.inventario.InventoryResponseCorrugados
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterCorrugadosInventory
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterCreateVoidItem
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterInventorySelectNum
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterselectQntShoes
import com.documentos.wms_beirario.ui.inventario.viewModel.CreateVoidInventoryViewModel
import com.example.coletorwms.constants.CustomSnackBarCustom
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddVoidFragment : Fragment() {

    private var mBinding: FragmentAddVoidBinding? = null
    val binding get() = mBinding!!
    private val mRetrofit = RetrofitService.getInstance()
    private lateinit var mViewModel: CreateVoidInventoryViewModel
    private lateinit var mAdapterTamShoes: AdapterInventorySelectNum
    private lateinit var mAdapterQntShoes: AdapterselectQntShoes
    private lateinit var mAdapterCreateVoid: AdapterCreateVoidItem
    private var mQntTotalShoes: Int = 0
    private var mQntCorrugadoTotal: Int = 0
    private var mPosition: Int? = null

    /**CREATE-->*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            CreateVoidInventoryViewModel.InventoryVCreateVoidiewModelFactoryBarCode(
                InventoryoRepository1(mRetrofitService = mRetrofit)
            )
        )[CreateVoidInventoryViewModel::class.java]
    }


    /**CREATE VIEW -->*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddVoidBinding.inflate(inflater, container, false)
        buttonEnable(mBinding!!.buttomLimpar, visibility = false)
        buttonEnable(mBinding!!.buttomAdicionar, visibility = false)
        setViews(visibility = false)
        setObservablesCorrugado()
        getEditText()
        clickButton()
        return binding.root
    }


    /**RESUME -->*/
    override fun onResume() {
        super.onResume()
        setupRvSelectTam()
        clickCorrugado()
        mAdapterCreateVoid = AdapterCreateVoidItem { itemClick, position ->
            alertSelectQnt(itemClick.tamanho.toInt(), position)
        }
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
            mQntCorrugadoTotal = itemClickCorrugado.quantidadePares
            mBinding!!.buttonSelecioneCorrugado.text = getString(
                R.string.Corrugado_select,
                itemClickCorrugado.id,
                itemClickCorrugado.quantidadePares
            )
            buttonEnable(mBinding!!.buttomLimpar, visibility = true)
            setViews(visibility = true)
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
                vibrate(500)
                Toast.makeText(requireContext(), "Lista vazia!!!", Toast.LENGTH_SHORT).show()
            } else if (qntShoes == 0) {
                mAdapterCreateVoid.deleteObject(tamSelect)
                vibrate(500)
                Toast.makeText(requireContext(), "Excluido", Toast.LENGTH_SHORT).show()
                setupButtonAdd()
            } else {
                setupCreateVoid(tamSelect, qntShoes, position)
                setupButtonAdd()
            }
            mAlert.dismiss()
            setSizeListViewModel()
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
            this,
            requireContext(),
            objectoCreateVoid,
            position
        )
    }

    /**CLICK BUTTON CORRUGADO -->*/
    private fun clickCorrugado() {
        mBinding!!.buttonSelecioneCorrugado.setOnClickListener {
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
    }

    private fun setViews(visibility: Boolean) {
        if (visibility) {
            mBinding!!.containerParent.visibility = View.VISIBLE
        } else {
            mBinding!!.containerParent.visibility = View.INVISIBLE
        }
    }

    /**RETORNA TAMANHO DA LISTA --> */
    private fun setSizeListViewModel() {
        mViewModel.postList(mAdapterCreateVoid.returList())
        //SET TXT QUANTIDADE DE PARES -->
        mQntTotalShoes = mAdapterCreateVoid.getQntShoesObject()
        mBinding!!.txtInfTotalItem.text =
            getString(R.string.quantidade_total_calcados_inventory, mQntTotalShoes)
        mViewModel.mGetListShow.observe(viewLifecycleOwner) { listDistribuicao ->

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
            if (visibilityButtonAdd) buttonEnable(mBinding!!.buttomAdicionar, true) else
                buttonEnable(mBinding!!.buttomAdicionar, visibility = false)
        }
    }

    private fun clickButton() {
        mBinding!!.buttomLimpar.setOnClickListener {
            mBinding!!.apply {
                editCabecal.setText("")
                editCor.setText("")
                editLinha.setText("")
                editReferencia.setText("")
                mAdapterCreateVoid.clearList()
                mBinding!!.buttonSelecioneCorrugado.text =
                    getString(R.string.texto_select_corrugado)
                txtInfTotalItem.text = getString(R.string.quantidade_total_calcados_inventory, 0)
            }
            setViews(visibility = false)
        }

        mBinding!!.buttomAdicionar.setOnClickListener {

        }
    }

}